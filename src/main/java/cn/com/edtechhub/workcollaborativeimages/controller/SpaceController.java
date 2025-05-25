package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.model.dto.SpaceLevelInfo;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.*;
import cn.com.edtechhub.workcollaborativeimages.model.vo.SpaceVO;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 空间控制层
 * 1. 基础实现
 * 由于一张图片只能属于一个空间, 因此在图片表 picture 中新增字段 spaceId, 实现图片与空间的关联, 同时增加索引以提高查询性能,
 * (1) 公共图库中的图片无需登录就能查看, 任何人都可以访问, 不需要进行用户认证或成员管理
 * (2) 私有空间则要求用户登录, 且访问权限严格控制, 通常只有空间管理员(或团队成员)才能查看或修改空间内容
 * (3) 公共图库没有额度限制, 私有空间会有图片大小、数量等方面的限制, 从而管理用户的存储资源和空间配额, 而公共图库完全不受这些限制
 * (4) 公有图库需要经过审核, 但是私有图库没有审核的说法, 只需要是内部成员就可以看到
 * (5) space私有图库最终会在远端 COS 中使用 space 目录来存储, 和公有图库中的 public 不同
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@RestController // 返回值默认为 json 类型
@RequestMapping("/space")
@Slf4j
public class SpaceController { // 通常控制层有服务层中的所有方法, 并且还有组合而成的方法, 如果组合的方法开始变得复杂就会封装到服务层内部

    /// 依赖注入 ///

    /**
     * 注入用户服务依赖
     */
    @Resource
    UserService userService;

    /**
     * 注入空间服务依赖
     */
    @Resource
    private SpaceService spaceService;

    /// 管理接口 ///

    @Operation(summary = "👑添加空间网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/add")
    public BaseResponse<Space> adminSpaceAdd(@RequestBody SpaceAddRequest spaceAddRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceAdd(spaceAddRequest));
    }

    @Operation(summary = "👑删除空间网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminSpaceDelete(@RequestBody SpaceDeleteRequest spaceDeleteRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceDelete(spaceDeleteRequest));
    }

    @Operation(summary = "👑更新空间网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/update")
    public BaseResponse<Space> adminSpaceUpdate(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceUpdate(spaceUpdateRequest)); // 可以直接绕过 COS 进行更新落库
    }

    @Operation(summary = "👑查询空间网络接口")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/search")
    public BaseResponse<Page<Space>> adminSpaceSearch(@RequestBody SpaceSearchRequest spaceSearchRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceSearch(spaceSearchRequest)); // 这个接口只是获取用户 id 不用获取详细的用户信息
    }

    /// 普通接口 ///

    @Operation(summary = "创建私有空间网络接口")
    @SaCheckLogin
    @PostMapping("/create")
    public BaseResponse<SpaceVO> spaceCreateSelf(@RequestBody SpaceCreateSelfRequest spaceCreateSelfRequest) {
        // 如果当前登录用户是否已经具有私有空间则不允许创建
        ThrowUtils.throwIf(spaceService.spaceGetCurrentLoginUserPrivateSpaces() != null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "每个用户仅能有一个私有空间");

        // 先创建请求实例
        var spaceAddRequest = new SpaceAddRequest();
        BeanUtils.copyProperties(spaceCreateSelfRequest, spaceAddRequest);

        // 创建私有空间
        spaceAddRequest
                .setUserId(userService.userGetCurrentLonginUserId()) // 强制用户只能创建属于自己的私有空间
                .setLevel(SpaceLevelEnums.COMMON.getCode()) // 强制用户只能得到普通版本私有空间
                .setType(SpaceTypeEnums.SELF.getCode()) // 设置类型为私有空间类型
        ;
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(spaceService.spaceAdd(spaceAddRequest)));
    }

    @Operation(summary = "销毁私有空间网络接口")
    @SaCheckLogin
    @PostMapping("/destroy")
    public BaseResponse<Boolean> spaceDestroySelf() {
        // 如果用户本来就没有私有空间就不允许删除
        ThrowUtils.throwIf(spaceService.spaceGetCurrentLoginUserPrivateSpaces() == null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户并没有私有空间");

        // 销毁用户的私有空间
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceDelete(new SpaceDeleteRequest().setId(spaceService.spaceGetCurrentLoginUserPrivateSpaces().getId())));
    }

    @Operation(summary = "编辑私有空间网络接口")
    @SaCheckLogin
    @PostMapping("/edit")
    public BaseResponse<SpaceVO> spaceEditSelf(@RequestBody SpaceEditRequestSelf spaceEditRequest) {
        // 如果用户本来就没有私有空间就不允许修改
        ThrowUtils.throwIf(spaceService.spaceGetCurrentLoginUserPrivateSpaces() == null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户并没有私有空间");

        // 先创建请求实例
        var spaceUpdateRequest = new SpaceUpdateRequest();
        BeanUtils.copyProperties(spaceEditRequest, spaceUpdateRequest);

        // 修改空间的信息
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(spaceService.spaceUpdate(spaceUpdateRequest)));
    }

    @Operation(summary = "查找私有空间网络接口")
    @SaCheckLogin
    @PostMapping("/query")
    public BaseResponse<SpaceVO> spaceQuerySelf() {
        // 如果用户本来就没有私有空间就不允许查询
        ThrowUtils.throwIf(spaceService.spaceGetCurrentLoginUserPrivateSpaces() == null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户并没有私有空间");

        // 查询私有空间信息
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(spaceService.spaceGetCurrentLoginUserPrivateSpaces()));
    }

    @Operation(summary = "获取空间等级描述网络接口")
    @SaCheckLogin
    @PostMapping("/level")
    public BaseResponse<List<SpaceLevelInfo>> spaceLevel() {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceGetLevelInfo());
    }

}
