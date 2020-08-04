package com.eyoung.springbootadmin.security.entity;

import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements UserDetails, Serializable {

    private Long id;
    private String username;
    private String password;
    private Date accountExpireAt;
    private Date passwordExpireAt;
    private String enabled;
    private String hasLocked;
    private String openId;

    private List<Role> authorities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public List<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Role> authorities) {
        this.authorities = authorities;
    }

    public Date getAccountExpireAt() {
        return accountExpireAt;
    }

    public void setAccountExpireAt(Date accountExpireAt) {
        this.accountExpireAt = accountExpireAt;
    }

    public Date getPasswordExpireAt() {
        return passwordExpireAt;
    }

    public void setPasswordExpireAt(Date passwordExpireAt) {
        this.passwordExpireAt = passwordExpireAt;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getHasLocked() {
        return hasLocked;
    }

    public void setHasLocked(String hasLocked) {
        this.hasLocked = hasLocked;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 用户账号是否过期
     */
    @Override
    public boolean isAccountNonExpired() {
        if (this.accountExpireAt == null){
            return true;
        }
        return System.currentTimeMillis() < this.accountExpireAt.getTime();
    }

    /**
     * 用户账号是否被锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return "0".equalsIgnoreCase(this.hasLocked);
    }

    /**
     * 用户密码是否过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        if (this.passwordExpireAt == null){
            return true;
        }
        return System.currentTimeMillis() < this.passwordExpireAt.getTime();
    }

    /**
     * 用户是否可用
     */
    @Override
    public boolean isEnabled() {
        return "1".equalsIgnoreCase(this.enabled);
//        return true;
    }

}
