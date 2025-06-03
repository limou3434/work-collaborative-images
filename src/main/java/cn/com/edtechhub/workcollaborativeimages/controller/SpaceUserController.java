package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceUserService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

}
