package com.example.mall.auth.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Data
public class MemberRespondVo {
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
