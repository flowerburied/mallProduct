package com.example.mall.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * ֧����Ϣ��
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:57:25
 */
@Data
@TableName("oms_payment_info")
public class PaymentInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * �����ţ�����ҵ��ţ�
     */
    private String orderSn;
    /**
     * ����id
     */
    private Long orderId;
    /**
     * ֧����������ˮ��
     */
    private String alipayTradeNo;
    /**
     * ֧���ܽ��
     */
    private BigDecimal totalAmount;
    /**
     * ��������
     */
    private String subject;
    /**
     * ֧��״̬
     */
    private String paymentStatus;
    /**
     * ����ʱ��
     */
    private Date createTime;
    /**
     * ȷ��ʱ��
     */
    private Date confirmTime;
    /**
     * �ص�����
     */
    private String callbackContent;
    /**
     * �ص�ʱ��
     */
    private Date callbackTime;

}
