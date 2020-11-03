package com.nowcoder.community.entity;

import java.util.Date;

/**
 * 记录登录用户信息的实体类
 *
 * ticket是登录的关键凭证。具有唯一性。
 *
 *
 *
 */
public class LoginTicket {
    private int id;
    private int userId;
    /** 登录凭证 */
    private String ticket;
    /** 0-正常，1-凭证过期 */
    private int status;
    /** 凭证的过期时间 */
    private Date expired;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }
}
