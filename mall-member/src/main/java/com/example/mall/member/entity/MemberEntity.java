package com.example.mall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author FlowerBuried
 * @email 2842511561@qq.com
 * @date 2023-12-19 13:46:43
 */
@Data
@TableName("ums_member")
public class MemberEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     *
     */
    private Long levelId;
    /**
     *
     */
    private String username;
    /**
     *
     */
    private String password;
    /**
     *
     */
    private String nickname;
    /**
     *
     */
    private String mobile;
    /**
     *
     */
    private String email;
    /**
     * ͷ
     */
    private String header;
    /**
     *
     */
    private Integer gender;
    /**
     *
     */
    private Date birth;
    /**
     *
     */
    private String city;
    /**
     * ְҵ
     */
    private String job;
    /**
     *
     */
    private String sign;
    /**
     *
     */
    private Integer sourceType;
    /**
     *
     */
    private Integer integration;
    /**
     *
     */
    private Integer growth;
    /**
     *
     */
    private Integer status;
    /**
     * ע
     */
    private Date createTime;

    private Long social_uid;
    private String access_token;
    private Long expires_in;
}
