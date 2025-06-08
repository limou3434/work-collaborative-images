package cn.com.edtechhub.workcollaborativeimages.manager.satoken;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 描述用户状态
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Accessors(chain = true) // 实现链式调用
public class UserTokenStatus {

    /**
     * 是否登录
     */
    Boolean isLogin;

    /**
     * Token 名称
     */
    String tokenName;

    /**
     * Token 有效时间
     */
    String tokenTimeout;

    /**
     * 登录用户 id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    String userId;

    /**
     * 登录权限
     */
    Integer userRole;

}
