package org.weixin.framework.web.core.exception;

public enum BaseErrorCode implements IErrorCode {
    // ========== 一级宏观错误码 客户端错误 ==========
    CLIENT_ERROR("A000001", "用户端错误"),
    VERIFY_CODE_ERROR("A000002", "图片验证码不正确"),
    PASSWORD_ERROR("A000003", "密码不正确"),
    CHECK_PASSWORD_ERROR("A000004", "确认密码不一致"),
    UNAUTHORIZED_ERROR("A000401", "未认证"),
    FORBIDDEN_ERROR("A000403", "没有操作权限"),
    // ========== 一级宏观错误码 系统执行出错 ==========
    SERVICE_ERROR("B000001", "系统执行出错"),
    UPDATE_ERROR("B000002", "更新失败"),
    CREATE_ERROR("B000003", "创建失败"),
    DELETE_ERROR("B000004", "删除失败"),
    USER_EXIST_ERROR("B000005", "用户已存在"),
    USER_NO_EXIST_ERROR("B000006", "用户不存在"),
    EMAIL_CODE_NO_EXIST_ERROR("B000007", "邮箱验证码过期"),
    EMAIL_CODE_ERROR("B000008", "邮箱验证码不正确"),
    UPLOAD_FILE_ERROR("B000009", "上传文件失败"),
    EMAIL_SEND_ERROR("B000010", "邮件发送失败");


    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
