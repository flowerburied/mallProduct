package com.example.mall.product;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mall.product.entity.BrandEntity;
import com.example.mall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MallProductApplicationTests {

    @Autowired
    BrandService brandService;




    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();

//        brandEntity.setName("XMi");
//        System.out.println(brandEntity);
//        brandService.save(brandEntity);

//        brandEntity.setBrandId(1L);
//        brandEntity.setName("apple");
//        brandService.updateById(brandEntity);


        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item) -> {
            System.out.println("success${}" + item);
        });


    }

}
