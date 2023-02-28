package com.jhchen.domain;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"出现错误"),
    SIGN_NOT_VERIFY(501,"签名验证失败"),
    IP_NOT_NULL(502,"ip不能为空"),
    ADDR_ERROR(503, "地址公钥不匹配"),
    BLOCK_NOT_VERIFIED(504, "区块验证出错"),
    ACCOUNT_EXISTS(506, "已有本地账户"),
    ACCOUNT_LOAD_ERROR(507, "加载账户数据错误"),
    HTTP_ERROR(508, "http链接错误"),
    NICKNAME_NOT_NULL(509, "昵称不能为空"),
    PASSWORD_NOT_NULL(510, "密码不能为空"),
    EMAIL_NOT_NULL(511, "邮箱不能为空"),
    NICKNAME_EXIST(512, "昵称已存在"),
    LOGIN_ERROR(505,"用户名或密码错误");
    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
