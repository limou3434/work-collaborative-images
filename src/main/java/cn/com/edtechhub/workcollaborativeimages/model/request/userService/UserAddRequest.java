package cn.com.edtechhub.workcollaborativeimages.model.request.userService;

import cn.com.edtechhub.workcollaborativeimages.constant.UserConstant;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 添加用户请求
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Accessors(chain = true) // 实现链式调用
public class UserAddRequest implements Serializable {

    /**
     * 账户号(业务层需要决定某一种或多种登录方式, 因此这里不限死为非空)
     */
    private String account;

    /**
     * 微信号
     */
    private String wxUnion;

    /**
     * 公众号
     */
    private String mpOpen;

    /**
     * 邮箱号
     */
    private String email;

    /**
     * 电话号
     */
    private String phone;

    /**
     * 身份证
     */
    private String ident;

    /**
     * 用户密码(业务层强制刚刚注册的用户重新设置密码, 交给用户时默认密码为 123456, 并且加盐密码)
     */
    private String passwd = UserConstant.DEFAULT_PASSWD;

    /**
     * 用户头像(业务层需要考虑默认头像使用 cos 对象存储)
     */
    private String avatar;

    /**
     * 用户标签(业务层需要 json 数组格式存储用户标签数组)
     */
    private String tags;

    /**
     * 用户昵称
     */
    private String nick;

    /**
     * 用户名字
     */
    private String name;

    /**
     * 用户简介
     */
    private String profile;

    /**
     * 用户生日
     */
    private String birthday;

    /**
     * 用户国家
     */
    private String country;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 用户角色(业务层需知 -1 为封号, 0 为用户, 1 为管理, ...)
     */
    private Integer role;

    /**
     * 用户等级(业务层需知 0 为 level0, 1 为 level1, 2 为 level2, 3 为 level3, ...)
     */
    private Integer level;

    /**
     * 用户性别(业务层需知 0 为未知, 1 为男性, 2 为女性)
     */
    private Integer gender;

    /// 序列化字段 ///
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求转换为实体方法
     */
    public static User copyProperties2Entity(UserAddRequest request) {
        var entity = new User();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }

    /**
     * 实体转化为请求方法
     */
    public static UserAddRequest copyProperties2Request(User entity) {
        var request = new UserAddRequest();
        BeanUtils.copyProperties(entity, request);
        return request;
    }

}