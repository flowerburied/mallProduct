package com.example.mall.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * ����������Ϣ
 *
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:57:25
 */
@Data
@TableName("oms_order_setting")
public class OrderSettingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * ��ɱ������ʱ�ر�ʱ��(��)
     */
    private Integer flashOrderOvertime;
    /**
     * ����������ʱʱ��(��)
     */
    private Integer normalOrderOvertime;
    /**
     * �������Զ�ȷ���ջ�ʱ�䣨�죩
     */
    private Integer confirmOvertime;
    /**
     * �Զ���ɽ���ʱ�䣬���������˻����죩
     */
    private Integer finishOvertime;
    /**
     * ������ɺ��Զ�����ʱ�䣨�죩
     */
    private Integer commentOvertime;
    /**
     * ��Ա�ȼ���0-���޻�Ա�ȼ���ȫ��ͨ�ã�����-��Ӧ��������Ա�ȼ���
     */
    private Integer memberLevel;

}
