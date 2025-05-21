package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.enums.UserRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.model.dto.UserTokenStatus;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Limou
 * @description 针对表【user(用户信息表)】的数据库操作Service
 * @createDate 2025-05-04 19:38:16
 */
public interface UserService extends IService<User> {

    /// 基础服务(添加/更新请求具有转化方法, 删除只依赖 id 进行删除, 查询依赖构造查询对象) ///

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

    /**
     * 根据标识查询单个用户服务
     */
    User userSearchById(Long id);

    /**
     * 对指定进行封禁/解禁服务
     */
    Boolean userDisable(Long userId, Long disableTime, UserRoleEnums userRoleEnums);

    /**
     * 注册账户服务
     */
    Boolean userRegister(String account, String password, String checkPassword);

    /**
     * 验证账户服务
     */
    User userValidation(String account, String passwd);

    /**
     * 登入账户服务
     */
    User userLogin(String account, String passwd, String device);

    /**
     * 登出账户服务
     */
    Boolean userLogout(String device);

    /**
     * 获取当前登录用户标识服务
     */
    Long userGetCurrentLonginUserId();

    /**
     * 获取指定用户会话服务
     */
    User userGetSessionById(Long id);

    /**
     * 获取指定用户凭证服务
     */
    UserTokenStatus userGetTokenById(Long id);

    /**
     * 获取指定用户是否为管理服务
     */
    Boolean userIsAdmin(Long id);

}
