package com.user.springboot.domain;

import javax.persistence.*;

import cn.afterturn.easypoi.excel.annotation.Excel;

@Table(name = "t_user")
public class User {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 姓名
     */
    @Excel(name = "名字", orderNum = "0")
    @Column(name = "user_name")
    private String userName;

    /**
     * 密码
     */
    @Excel(name = "密码", orderNum = "10")
    private String password;

    /**
     * 电话
     */
    @Excel(name = "电话", orderNum = "2")
    private String phone;

    /**
     * @return user_id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取姓名
     *
     * @return user_name - 姓名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置姓名
     *
     * @param userName 姓名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取电话
     *
     * @return phone - 电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置电话
     *
     * @param phone 电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}