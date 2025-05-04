package cn.com.edtechhub.workcollaborativeimages.constant;

import org.springframework.stereotype.Component;

/**
 * 用户常量
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public interface UserConstant {

    /**
     * 默认盐值
     */
    String SALT = "edtechhub"; // 如果需要安全则可以进行修改

    /**
     * 默认密码
     */
    String DEFAULT_PASSWD = "123456";

    /**
     * 默认账户最小长度
     */
    Integer ACCOUNT_LENGTH = 5;

    /**
     * 默认密码最小长度
     */
    Integer PASSWD_LENGTH = 6;

}