package com.example.manager.enums;

/**
 * 错误枚举类
 */


public enum HttpCode {

    // 成功
    OK(200, "SUCCESS"),
    // 没有登录
    NO_LOGIN(0, "NO_LOGIN"),
    // 参数错误
    PARAM_FAIL(401,"参数有误"),
    // 更新数据失败
    UP_DATA_FAIL(505,"更新数据失败"),

    // --------  用户相关  -----------
    // 用户已存在错误,
    USER_AUTH_TOKEN_FAIL(4000, "用户token校验失败"),
    USER_AUTH_TOKEN_EXPIRED_FAIL(4001, "用户token已过期"),
    USER_UN_LOGIN(4002, "用户token已过期"),
    USER_EXIST_FAIL(4010, "用户已存在"),
    USER_NOT_EXIST_FAIL(4011, "用户不存在"),
    USER_PASSWORD_NOT_MATCH(4012, "用户密码不正确"),

    FILE_NOT_FIND(401, "文件没有找到"),
    FILE_UPLOAD_PATH_IS_FILES(401, "上传路径不能是文件"),
    FILE_UPLOAD_FILE_EXITS(401, "上传文件已经存在"),
    FILE_UPLOAD_FILE_NAME_EMPTY(401, "上传文件名字为空"),
    UN_ZIP_FILE_FAIL(401, "文件解压缩失败"),


    // 未知异常
    UN_KNOW_ERROR(400, "出现系统未知错误");


    private int code;

    private String msg;


    HttpCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
