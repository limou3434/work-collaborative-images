package cn.com.edtechhub.workcollaborativeimages.model.vo;

import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户脱敏
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Accessors(chain = true) // 实现链式调用
public class UserVO implements Serializable {

    /**
     * 本用户唯一标识(业务层需要考虑使用雪花算法用户标识的唯一性)
     */
    @JsonSerialize(using = ToStringSerializer.class) // 非常重要的注解, 只转换 Long, 为字符串, 避免前端 JS 精度不行导致获取到错误的 ID
    private Long id;

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
     * 用户空间权限(0-viewer 1-editor 2-manger)
     */
    private Integer spaceRole;

    /**
     * 用户性别(业务层需知 0 为未知, 1 为男性, 2 为女性)
     */
    private Integer gender;

    /// 序列化字段 ///
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 脱敏方法
     */
    public static UserVO removeSensitiveData(User user) {
        if (user == null) {
            return null;
        }
        var userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 脱敏方法(列表脱敏)
     */
    public static List<UserVO> removeSensitiveData(List<User> userList) {
        if (userList == null) {
            return null;
        }
        return userList
                .stream()
                .map(UserVO::removeSensitiveData)
                .collect(Collectors.toList());
    }

    /**
     * 脱敏方法(分页脱敏)
     */
    public static Page<UserVO> removeSensitiveData(Page<User> userPage) {
        if (userPage == null) {
            return null;
        }
        var userList = userPage
                .getRecords()
                .stream()
                .map(UserVO::removeSensitiveData)
                .collect(Collectors.toList());
        var newUserPage = new Page<UserVO>();
        newUserPage.setCurrent(userPage.getCurrent());
        newUserPage.setSize(userPage.getSize());
        newUserPage.setTotal(userPage.getTotal());
        newUserPage.setRecords(userList);
        return newUserPage;
    }

}