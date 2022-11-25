package com.hengdao.hs.rabbit.spring.boot.starter.exception;

/**
 * 返回状态码
 *
 * @author Verite
 */
public class MqStatus {
    /**
     * 操作成功
     */
    public static final int SUCCESS = 2000;

    /**
     * 对象创建成功
     */
    public static final int CREATED = 2001;

    /**
     * 请求已经被接受
     */
    public static final int ACCEPTED = 2002;

    /**
     * 操作已经执行成功，但是没有返回数据
     */
    public static final int NO_CONTENT = 2004;

    /**
     * 资源已被移除
     */
    public static final int MOVED_PERM = 3001;

    /**
     * 资源没有被修改
     */
    public static final int NOT_MODIFIED = 3004;

    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 4001;

    /**
     * 访问受限，授权过期
     */
    public static final int FORBIDDEN = 4003;

    /**
     * 不允许的Mq方法
     */
    public static final int BAD_METHOD = 4005;

    /**
     * 不支持的数据，媒体类型
     */
    public static final int UNSUPPORTED_TYPE = 4006;


    /**
     * 接口未实现
     */
    public static final int NOT_IMPLEMENTED = 5001;


    /**
     * 未绑定的
     */
    public static final int NOT_BIND = 5002;

    /**
     * 需要绑定
     */
    public static final int NEED_BIND = 5003;

    /**
     * 验证连接
     */
    public static final int VERIFY_CONNECT = 5004;

    /**
     * 参数列表错误（缺少，格式不匹配）
     */
    public static final int BAD_REQUEST = 4000;

    /**
     * 资源，服务未找到
     */
    public static final int NOT_FOUND = 4004;

    /**
     * 资源冲突，或者资源被锁
     */
    public static final int CONFLICT = 4009;

    /**
     * 消息ID不能为空
     */
    public static final int MESSAGE_ID_EMPTY = 1000;

    /**
     * 消息主题不能为空
     */
    public static final int MESSAGE_TOPIC_EMPTY = 1001;

    /**
     * 消息发布者不能为空
     */
    public static final int MESSAGE_PRODUCER_EMPTY = 1002;

    /**
     * 消息签名不能为空
     */
    public static final int MESSAGE_SIGN_EMPTY = 1003;

    /**
     * 消息主题不存在
     */
    public static final int MESSAGE_TOPIC_EXISTS = 1004;

    /**
     * 消息主题不可用
     */
    public static final int MESSAGE_TOPIC_USE = 1005;

    /**
     * 签名无效
     */
    public static final int MESSAGE_SIGN_INVALID = 1006;

    /**
     * 锁定无效
     */
    public static final int LOCK_INVALID = 1007;

    /**
     * 发送无效
     */
    public static final int SEND_INVALID = 1008;

    /**
     * 未定义错误+具体异常信息
     */
    public static final int UNDEFINED_ERROR = 5000;
}
