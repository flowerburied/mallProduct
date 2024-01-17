package com.example.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.common.constant.OrderConstant;
import com.example.common.exception.NoStockException;
import com.example.common.utils.R;
import com.example.common.vo.MemberRespondVo;
import com.example.mall.order.dao.OrderItemDao;
import com.example.mall.order.entity.OrderItemEntity;
import com.example.mall.order.enume.OrderStatusEnum;
import com.example.mall.order.feign.CartFeignService;
import com.example.mall.order.feign.MemberFeignService;
import com.example.mall.order.feign.ProductFeignService;
import com.example.mall.order.feign.WmsFeignService;
import com.example.mall.order.interceptor.LoginUserInterceptor;
import com.example.mall.order.service.OrderItemService;
import com.example.mall.order.to.OrderCreateTo;
import com.example.mall.order.vo.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mall.order.dao.OrderDao;
import com.example.mall.order.entity.OrderEntity;
import com.example.mall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Resource
    MemberFeignService memberFeignService;
    @Resource
    CartFeignService cartFeignService;
    @Resource
    ThreadPoolExecutor threadPoolExecutor;
    @Resource
    WmsFeignService wmsFeignService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    ProductFeignService productFeignService;

    @Resource
    OrderItemService orderItemService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 订单确认页返回需要的数据
     *
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespondVo memberRespondVo = LoginUserInterceptor.loginUser.get();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> addressThread = CompletableFuture.runAsync(() -> {
//设置线程
            System.out.println("address线程===" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //        查询所以收货地址
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespondVo.getId());
            confirmVo.setAddresses(address);
        }, threadPoolExecutor);

        CompletableFuture<Void> cartThread = CompletableFuture.runAsync(() -> {
            //设置线程
            System.out.println("cart线程===" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //        查询购物车所选的购物项
            List<OrderItemVo> currentCartItem = cartFeignService.getCurrentCartItem();
            confirmVo.setOrderItems(currentCartItem);
        }, threadPoolExecutor).thenRunAsync(() -> {
            List<OrderItemVo> orderItems = confirmVo.getOrderItems();
            List<Long> collect = orderItems.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R skuHasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuStockVo> data = skuHasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getStock));
                confirmVo.setStocks(map);

            }

        }, threadPoolExecutor);


        //查询用户积分
        Integer integration = memberRespondVo.getIntegration();
        confirmVo.setIntegral(integration);
        //防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        confirmVo.setOrderToken(token);

        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespondVo.getId(), token, 30, TimeUnit.MINUTES);
        CompletableFuture.allOf(addressThread, cartThread).get();
        System.out.println("confirmVo===" + confirmVo);

        return confirmVo;
    }

    //    @GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        System.out.println("orderSubmitVo===" + orderSubmitVo);
        confirmVoThreadLocal.set(orderSubmitVo);
        SubmitOrderResponseVo submitVo = new SubmitOrderResponseVo();
        MemberRespondVo memberRespondVo = LoginUserInterceptor.loginUser.get();

        submitVo.setCode(0);
        //创建订单 验令牌  验价格  锁库存
        String orderToken = orderSubmitVo.getOrderToken();
        // 查-比-删，全部成功返回1，这里是有并发问题的，必须要原子操作
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
//        原子验证令牌和删除令牌
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespondVo.getId()), orderToken);
        if (result == 0L) {
            submitVo.setCode(1);
            //令牌验证失败
            return submitVo;
        } else {
            //令牌验证通过
            OrderCreateTo order = createOrder();
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = orderSubmitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
//金额对比

//                保存订单
                saveOrder(order);
//                库存锁定,只要有异常回滚订单数据
                WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                wareSkuLockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                wareSkuLockVo.setLocks(locks);

                //远程锁库存
                R r = wmsFeignService.orderLockStock(wareSkuLockVo);
                if (r.getCode() == 0) {
                    //锁成功
                    submitVo.setOrder(order.getOrder());

                    //调试出错
                    int i = 10 / 0;  //回滚测试

                    return submitVo;
                } else {
                    submitVo.setCode(3);
                    throw new NoStockException(0L);
                    //锁失败
//                    submitVo.setCode(3);
//                    return submitVo;
                }

            } else {
                submitVo.setCode(2);
                return submitVo;
            }
        }
//        String redisToken = stringRedisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespondVo.getId());

//        if (orderToken!=null && orderToken.equals(redisToken)){
//            //令牌验证通过
//
//        }else {
//            return submitVo;
//        }


    }


    //保存订单数据
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());

        baseMapper.insert(orderEntity);

        List<OrderItemEntity> items = order.getItems();

        orderItemService.saveBatch(items);

    }

    private OrderCreateTo createOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //创建订单号
        String orderSn = IdWorker.getTimeId();
        OrderEntity orderEntity = buildOrder(orderSn);
        //获取到所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);
//        验价
        computerPrice(orderEntity, itemEntities);
        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setItems(itemEntities);

        return orderCreateTo;
    }

    //订单价格相关
    private void computerPrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal couponAmount = BigDecimal.ZERO;
        BigDecimal integrationAmount = BigDecimal.ZERO;
        BigDecimal promotionAmount = BigDecimal.ZERO;

        Integer giftGrowth = 0;
        Integer giftIntegration = 0;
        //叠加每个订单项的总额
        for (OrderItemEntity itemEntity : itemEntities) {
            couponAmount = couponAmount.add(itemEntity.getCouponAmount());
            integrationAmount = integrationAmount.add(itemEntity.getIntegrationAmount());
            promotionAmount = promotionAmount.add(itemEntity.getPromotionAmount());
//            BigDecimal multiply = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
            BigDecimal realAmount = itemEntity.getRealAmount();
            total = total.add(realAmount);

            giftGrowth += itemEntity.getGiftGrowth();
            giftIntegration += itemEntity.getGiftIntegration();
        }

        orderEntity.setTotalAmount(total);
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount())); // 应付总价
        orderEntity.setPromotionAmount(promotionAmount); // 阶梯价减免
        orderEntity.setCouponAmount(couponAmount);  // 优惠券减免价
        orderEntity.setIntegrationAmount(integrationAmount); // 积分减免价

        orderEntity.setIntegration(giftIntegration); // 设置购物积分
        orderEntity.setGrowth(giftGrowth); // 设置成长积分
        orderEntity.setDeleteStatus(0); // 订单是否删除

    }

    private OrderEntity buildOrder(String orderSn) {
        //创建订单号
        MemberRespondVo memberRespondVo = LoginUserInterceptor.loginUser.get();

        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setMemberId(memberRespondVo.getId());
        orderEntity.setOrderSn(orderSn);
        //获取收货地址信息
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        System.out.println("orderSubmitVo===" + orderSubmitVo);
        R fare = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo data = fare.getData(new TypeReference<FareVo>() {
        });

        // 设置收货人信息
        orderEntity.setFreightAmount(data.getFare());
        orderEntity.setReceiverCity(data.getAddressVo().getCity());
        orderEntity.setReceiverDetailAddress(data.getAddressVo().getDetailAddress());
        orderEntity.setReceiverName(data.getAddressVo().getName());
        orderEntity.setReceiverPhone(data.getAddressVo().getPhone());
        orderEntity.setReceiverPostCode(data.getAddressVo().getPostCode());
        orderEntity.setReceiverProvince(data.getAddressVo().getProvince());
        orderEntity.setReceiverRegion(data.getAddressVo().getRegion());

        //设置订单状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setConfirmStatus(14);


        return orderEntity;
    }

    //构建订单项数据
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //最后确认各个商品价格
        List<OrderItemVo> currentCartItem = cartFeignService.getCurrentCartItem();
        if (currentCartItem != null && currentCartItem.size() > 0) {
            List<OrderItemEntity> itemEntities = currentCartItem.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = buildOrderItem(cartItem);
//                orderItemEntity.setIntegrationAmount();
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;

            }).collect(Collectors.toList());

            return itemEntities;
        }
        return null;
    }

    //构建某一个订单项
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        Long skuId = item.getSkuId();
        //商品spu信息
        R spuInfoBuSkuId = productFeignService.getSpuInfoBuSkuId(skuId);

        if (spuInfoBuSkuId.getCode() == 0) {
            SpuInfoVo data = spuInfoBuSkuId.getData(new TypeReference<SpuInfoVo>() {
            });
            itemEntity.setSpuId(data.getId());
            itemEntity.setSpuBrand(data.getBrandId().toString());
            itemEntity.setSpuName(data.getSpuName());
            itemEntity.setCategoryId(data.getCatalogId());
        } else {
//            log.error("远程调用出错:productFeignService.getSpuInfoBySkuId");
        }

        //商品sku信息
        itemEntity.setSkuId(skuId);
        itemEntity.setSkuName(item.getTitle());
        itemEntity.setSkuPic(item.getImage());
        itemEntity.setSkuPrice(item.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(item.getSkuAttr(), ";");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(item.getCount());
        //积分信息
        itemEntity.setGiftGrowth(item.getPrice().multiply(BigDecimal.valueOf(item.getCount())).intValue());
        itemEntity.setGiftIntegration(item.getPrice().multiply(BigDecimal.valueOf(item.getCount())).intValue());
        // 订单项的价格信息
        itemEntity.setPromotionAmount(BigDecimal.ZERO);
        itemEntity.setCouponAmount(BigDecimal.ZERO);
        itemEntity.setIntegrationAmount(BigDecimal.ZERO);
        //实际金额
        BigDecimal origin = itemEntity.getSkuPrice().
                multiply(BigDecimal.valueOf(itemEntity.getSkuQuantity()));
        BigDecimal subtract = origin.subtract(itemEntity.getCouponAmount()).
                subtract(itemEntity.getPromotionAmount()).
                subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(subtract);

        return itemEntity;
    }

}