package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.model.dto.UserStatus;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserUpdateRequest;
import cn.dev33.satoken.session.SaSession;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Limou
 * @description 针对表【user(用户信息表)】的数据库操作Service
 * @createDate 2025-05-04 19:38:16
 */
public interface UserService extends IService<User> {

    /// 实体服务 ///
    /**
     * 用户添加服务
     */
    User userAdd(UserAddRequest userAddRequest);

    /**
     * 用户删除服务
     */
    Boolean userDelete(UserDeleteRequest userDeleteRequest);

    /**
     * 用户更新服务
     */
    User userUpdate(UserUpdateRequest userUpdateRequest);

    /**
     * 用户查询服务
     */
    Page<User> userSearch(UserSearchRequest userSearchRequest);

    /// 其他服务 ///
    User userSearchById(Long id);

    /**
     * 用户封禁服务
     */
    Boolean userDisable(Long userId, Long disableTime);

    /**
     * 用户注册服务
     */
    Boolean userRegister(String account, String password, String checkPassword);

    /**
     * 用户登入服务
     */
    User userLogin(String account, String passwd, String device);

    /**
     * 用户登出服务
     */
    Boolean userLogout(String device);

    /**
     * 获取当前登录用户标识服务
     */
    Long userGetCurrentLonginUserId();

    /**
     * 获取当前登录用户状态服务
     */
    UserStatus userCurrentLonginUserStatus();

    /**
     * 获取当前登录用户信息服务
     */
    User userCurrentLonginUserInfo();

    /**
     * 获取当前登录用户会话服务
     */
    User userCurrentLonginUserSession();

    /**
     * 确认当前用户是否为管理员服务
     */
    Boolean userIsAdmin();

}
