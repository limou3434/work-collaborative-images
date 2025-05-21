package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.UserRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.model.dto.UserTokenStatus;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.*;
import cn.com.edtechhub.workcollaborativeimages.model.vo.UserVO;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.DeviceUtils;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制层
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@RestController // 返回值默认为 json 类型
@RequestMapping("/user")
@Slf4j
public class UserController { // 通常控制层有服务层中的所有方法, 并且还有组合而成的方法, 如果组合的方法开始变得复杂就会封装到服务层内部

    /**
     * 注入用户服务实例
     */
    @Resource
    private UserService userService;

    /// 管理接口 ///

    @Operation(summary = "👑添加用户网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/add")
    public BaseResponse<User> adminUserAdd(@RequestBody UserAddRequest userAddRequest) {
        User user = userService.userAdd(userAddRequest);
        return TheResult.success(CodeBindMessageEnums.SUCCESS, user);
    }

    @Operation(summary = "👑删除用户网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminUserDelete(@RequestBody UserDeleteRequest userDeleteRequest) {
        Boolean result = userService.userDelete(userDeleteRequest);
        return TheResult.success(CodeBindMessageEnums.SUCCESS, result);
    }

    @Operation(summary = "👑修改用户网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/update")
    public BaseResponse<User> adminUserUpdate(@RequestBody UserUpdateRequest userUpdateRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, userService.userUpdate(userUpdateRequest));
    }

    @Operation(summary = "👑查询用户网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/search")
    public BaseResponse<Page<User>> adminUserSearch(@RequestBody UserSearchRequest userSearchRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, userService.userSearch(userSearchRequest));
    }

    @Operation(summary = "👑封禁用户网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/disable")
    public BaseResponse<Boolean> adminUserDisable(@RequestBody UserDisableRequest userDisableRequest) {
        Boolean result = userService.userDisable(userDisableRequest.getId(), userDisableRequest.getDisableTime(), UserRoleEnums.USER_ROLE);
        return TheResult.success(CodeBindMessageEnums.SUCCESS, result);
    }

    @Operation(summary = "👑获取指定用户凭证网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @GetMapping("/admin/token")
    public BaseResponse<UserTokenStatus> adminGetUserToken(Long id) {
        UserTokenStatus userTokenStatus = userService.userGetTokenById(id);
        return TheResult.success(CodeBindMessageEnums.SUCCESS, userTokenStatus);
    }

    /// 普通接口 ///

    @Operation(summary = "用户注册网络接口")
    @SaIgnore
    @PostMapping("/register")
    public BaseResponse<Boolean> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        Boolean result = userService.userRegister(userRegisterRequest.getAccount(), userRegisterRequest.getPasswd(), userRegisterRequest.getCheckPasswd());
        return TheResult.success(CodeBindMessageEnums.SUCCESS, result);
    }

    @Operation(summary = "用户登入网络接口")
    @SaIgnore
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        User user = userService.userLogin(userLoginRequest.getAccount(), userLoginRequest.getPasswd(), DeviceUtils.getRequestDevice(request)); // 这里同时解析用户的设备, 以支持同端互斥
        UserVO userVO = UserVO.removeSensitiveData(user);
        return TheResult.success(CodeBindMessageEnums.SUCCESS, userVO);
    }

    @Operation(summary = "用户登出网络接口")
    @SaCheckLogin
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        Boolean result = userService.userLogout(DeviceUtils.getRequestDevice(request));
        return TheResult.success(CodeBindMessageEnums.SUCCESS, result);
    }

    @Operation(summary = "获取登录凭证网络接口")
    @SaIgnore
    @GetMapping("/token")
    public BaseResponse<UserTokenStatus> userGetToken() {
        UserTokenStatus userTokenStatus = userService.userGetTokenById(userService.userGetCurrentLonginUserId());
        return TheResult.success(CodeBindMessageEnums.SUCCESS, userTokenStatus);
    }

    @Operation(summary = "获取登录会话网络接口")
    @SaIgnore
    @GetMapping("/session")
    public BaseResponse<UserVO> userSession() {
        User user = userService.userGetSessionById(this.userService.userGetCurrentLonginUserId());
        return TheResult.success(CodeBindMessageEnums.SUCCESS, UserVO.removeSensitiveData(user));
    }

}
