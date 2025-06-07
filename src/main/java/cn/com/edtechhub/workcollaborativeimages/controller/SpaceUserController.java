package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.*;
import cn.com.edtechhub.workcollaborativeimages.model.request.userService.UserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.vo.SpaceUserVO;
import cn.com.edtechhub.workcollaborativeimages.model.vo.SpaceVO;
import cn.com.edtechhub.workcollaborativeimages.model.vo.UserVO;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceUserService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 空间用户关联控制层
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@RestController // 返回值默认为 json 类型
@RequestMapping("/space_user")
@Slf4j
public class SpaceUserController { // 通常控制层有服务层中的所有方法, 并且还有组合而成的方法, 如果组合的方法开始变得复杂就会封装到服务层内部

    /// 依赖注入 ///

    /**
     * 注入空间用户关联服务依赖
     */
    @Resource
    private SpaceUserService spaceUserService;

    /**
     * 注入空间服务依赖
     */
    @Resource
    private SpaceService spaceService;

    /**
     * 注入用户服务依赖
     */
    @Resource
    private UserService userService;

    /// 管理接口 ///

    @Operation(summary = "👑添加空间用户关联网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/add")
    public BaseResponse<SpaceUser> adminSpaceUserAdd(@RequestBody SpaceUserAddRequest spaceUserAddRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceUserService.spaceUserAdd(spaceUserAddRequest));
    }

    @Operation(summary = "👑删除空间用户关联网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminSpaceUserDelete(@RequestBody SpaceUserDeleteRequest spaceUserDeleteRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceUserService.spaceUserDelete(spaceUserDeleteRequest));
    }

    @Operation(summary = "👑更新空间用户关联网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/update")
    public BaseResponse<SpaceUser> adminSpaceUserUpdate(@RequestBody SpaceUserUpdateRequest spaceUserUpdateRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceUserService.spaceUserUpdate(spaceUserUpdateRequest)); // 可以直接绕过 COS 进行更新落库
    }

    @Operation(summary = "👑查询空间用户关联网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/search")
    public BaseResponse<Page<SpaceUser>> adminSpaceUserSearch(@RequestBody SpaceUserSearchRequest spaceUserSearchRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceUserService.spaceUserSearch(spaceUserSearchRequest)); // 这个接口只是获取用户 id 不用获取详细的用户信息
    }

    /// 普通接口 ///
    @Operation(summary = "对指定的协作空间移进成员网络接口")
    @SaCheckLogin
    @SaCheckPermission({"spaceUser:manager"})
    @PostMapping("/move/in")
    public BaseResponse<SpaceUser> spaceUserMoveIn(@RequestBody SpaceUserMoveInRequest spaceUserMoveInRequest) {
        Space space = spaceService.spaceGetCurrentLoginUserSpace(SpaceTypeEnums.COLLABORATIVE);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户没有协作空间无法做添加成员操作");

        Integer type = space.getType();
        SpaceTypeEnums spaceTypeEnums = SpaceTypeEnums.getEnums(type);
        ThrowUtils.throwIf(spaceTypeEnums != SpaceTypeEnums.COLLABORATIVE, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "不能对除了协作空间以外的空间进行操作");

        Long spaceId = spaceUserMoveInRequest.getSpaceId(); // TODO: 这么做的主要目的是方便后续拓展多个协作空间
        ThrowUtils.throwIf(spaceId == null, CodeBindMessageEnums.PARAMS_ERROR, "空间标识不能为空");
        ThrowUtils.throwIf(!Objects.equals(spaceId, space.getId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户无法操作该协作空间, 因为该协作空间不属于您");

        Long userId = spaceUserMoveInRequest.getUserId();
        ThrowUtils.throwIf(userService.userSearchById(userId) == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "指定的用户不存在无法操作");
        ThrowUtils.throwIf(Objects.equals(userId, space.getUserId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "不允许对协作空间的拥有者进行操作");

        Integer spaceRole = spaceUserMoveInRequest.getSpaceRole();

        return TheResult.success(
                CodeBindMessageEnums.SUCCESS,
                spaceUserService.spaceUserAdd(
                        new SpaceUserAddRequest()
                                .setSpaceId(spaceId)
                                .setUserId(userId)
                                .setSpaceRole(spaceRole)
                )
        );
    }

    @Operation(summary = "对指定的协作空间移出成员网络接口")
    @SaCheckLogin
    @SaCheckPermission({"spaceUser:manager"})
    @PostMapping("/move/out")
    public BaseResponse<Boolean> spaceUserMoveOut(@RequestBody SpaceUserMoveOutRequest spaceUserMoveOutRequest) {
        Space space = spaceService.spaceGetCurrentLoginUserSpace(SpaceTypeEnums.COLLABORATIVE);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户没有协作空间无法做移除成员操作");

        Integer type = space.getType();
        SpaceTypeEnums spaceTypeEnums = SpaceTypeEnums.getEnums(type);
        ThrowUtils.throwIf(spaceTypeEnums != SpaceTypeEnums.COLLABORATIVE, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "不能对除了协作空间以外的空间进行操作");

        Long spaceId = spaceUserMoveOutRequest.getSpaceId(); // TODO: 这么做的主要目的是方便后续拓展多个协作空间
        ThrowUtils.throwIf(spaceId == null, CodeBindMessageEnums.PARAMS_ERROR, "空间标识不能为空");
        ThrowUtils.throwIf(!Objects.equals(spaceId, space.getId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户无法操作该协作空间, 因为该协作空间不属于您");

        Long userId = spaceUserMoveOutRequest.getUserId();
        ThrowUtils.throwIf(userService.userSearchById(userId) == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "指定的用户不存在无法操作");
        ThrowUtils.throwIf(Objects.equals(userId, space.getUserId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "不允许对协作空间的拥有者进行操作");

        List<SpaceUser> spaceUserList = spaceUserService.spaceUserSearch(
                new SpaceUserSearchRequest()
                        .setSpaceId(spaceId)
                        .setUserId(userId)
        ).getRecords();
        ThrowUtils.throwIf(spaceUserList.isEmpty(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "该用户不存在于您的协作空间中, 无需移除");
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceUserService.spaceUserDelete(new SpaceUserDeleteRequest().setId(spaceUserList.get(0).getId())));
    }

    @Operation(summary = "对指定的协作空间编辑成员网络接口")
    @SaCheckLogin
    @SaCheckPermission({"spaceUser:manager"})
    @PostMapping("/edit")
    public BaseResponse<SpaceUserVO> spaceUserEdit(@RequestBody SpaceUserEditRequest spaceUserEditRequest) {
        Space space = spaceService.spaceGetCurrentLoginUserSpace(SpaceTypeEnums.COLLABORATIVE);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "当前用户没有协作空间无法做编辑成员操作");

        Integer type = space.getType();
        SpaceTypeEnums spaceTypeEnums = SpaceTypeEnums.getEnums(type);
        ThrowUtils.throwIf(spaceTypeEnums != SpaceTypeEnums.COLLABORATIVE, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "不能对除了协作空间以外的空间进行操作");

        Long spaceId = spaceUserEditRequest.getSpaceId(); // TODO: 这么做的主要目的是方便后续拓展多个协作空间
        ThrowUtils.throwIf(spaceId == null, CodeBindMessageEnums.PARAMS_ERROR, "空间标识不能为空");
        ThrowUtils.throwIf(!Objects.equals(spaceId, space.getId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户无法操作该协作空间, 因为该协作空间不属于您");

        Long userId = spaceUserEditRequest.getUserId();
        User user = userService.userSearchById(userId);
        ThrowUtils.throwIf(user == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "该用户不存在");

        Integer spaceRole = spaceUserEditRequest.getSpaceRole();

        List<SpaceUser> spaceUserList = spaceUserService.spaceUserSearch(
                new SpaceUserSearchRequest()
                        .setSpaceId(spaceId)
                        .setUserId(userId)
        ).getRecords();
        ThrowUtils.throwIf(spaceUserList.isEmpty(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "该用户不存在于您的协作空间中, 无需编辑");
        Long spaceUserId = spaceUserList.get(0).getId();

        return TheResult.success(
                CodeBindMessageEnums.SUCCESS,
                SpaceUserVO.removeSensitiveData(
                                spaceUserService.spaceUserUpdate(
                                        new SpaceUserUpdateRequest()
                                                .setId(spaceUserId)
                                                .setSpaceRole(spaceRole)
                                )
                        )
                        .setSpaceVO(SpaceVO.removeSensitiveData(space))
                        .setUserVO(UserVO.removeSensitiveData(user))
        );
    }

    @Operation(summary = "获取当前登录用户的协作空间中所有的成员信息网络接口")
    @SaCheckLogin
    @GetMapping("/page/user")
    public BaseResponse<Page<UserVO>> spaceUserPageUser() {
        // 获取协作空间
        Space space = spaceService.spaceGetCurrentLoginUserSpace(SpaceTypeEnums.COLLABORATIVE);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户没有协作空间无法做查询成员操作");

        // 查询协作空间下所有成员记录
        List<SpaceUser> spaceUserList = Optional.ofNullable(
                        spaceUserService.spaceUserSearch(new SpaceUserSearchRequest().setSpaceId(space.getId()))
                                .getRecords())
                .orElse(Collections.emptyList());

        if (spaceUserList.isEmpty()) {
            return TheResult.success(CodeBindMessageEnums.SUCCESS, new Page<>());
        }

        // 构造用户ID到空间角色的映射
        Map<Long, Integer> userRoleMap = spaceUserList.stream()
                .collect(Collectors.toMap(SpaceUser::getUserId, SpaceUser::getSpaceRole));

        Set<Long> userIdSet = userRoleMap.keySet();

        // 查询所有用户, 并过滤出属于该空间的用户
        Page<User> userPage = userService.userSearch(new UserSearchRequest());
        List<UserVO> filteredList = userPage.getRecords().stream()
                .filter(user -> userIdSet.contains(user.getId()))
                .map(user -> {
                    UserVO vo = UserVO.removeSensitiveData(user);
                    // 把空间角色设置进去，假设UserVO有setSpaceRole方法
                    vo.setSpaceRole(userRoleMap.get(user.getId()));
                    return vo;
                })
                .toList();

        // 构造分页对象
        Page<UserVO> voPage = new Page<>();
        voPage.setCurrent(userPage.getCurrent());
        voPage.setSize(userPage.getSize());
        voPage.setTotal(filteredList.size());
        voPage.setRecords(filteredList);

        return TheResult.success(CodeBindMessageEnums.SUCCESS, voPage);
    }

    @Operation(summary = "获取当前登录用户已经加入的所有协作空间的相关记录")
    @SaCheckLogin
    @GetMapping("/page/my_collaborative_space")
    public BaseResponse<List<SpaceUserVO>> spaceUserPageMyCollaborativeSpace() {
        // 查询当前登录用户的用户空间关联记录
        List<SpaceUser> spaceUserList = Optional
                .ofNullable(spaceUserService.spaceUserSearch(new SpaceUserSearchRequest().setUserId(userService.userGetCurrentLonginUserId())).getRecords())
                .orElse(Collections.emptyList());

        List<SpaceUserVO> spaceUserVOList = SpaceUserVO.removeSensitiveData(spaceUserList)
                .stream()
                .peek(spaceUserVO -> {
                    spaceUserVO.setSpaceVO(SpaceVO.removeSensitiveData(spaceService.spaceSearchById(spaceUserVO.getSpaceId())));
                    spaceUserVO.setUserVO(UserVO.removeSensitiveData(userService.userSearchById(spaceUserVO.getUserId())));
                })
                .toList();

        // TODO: 如果用户的协作空间较多导致查询次数较多则可以考虑优化这里

        // 获取这些关联记录中记载的空间标识
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceUserVOList);
    }

}
