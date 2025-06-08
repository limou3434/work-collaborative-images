package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnum;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnum;
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
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 空间控制层
 * 1. 基础实现
 * 由于一张图片只能属于一个空间, 因此在图片表 picture 中新增字段 spaceId, 实现图片与空间的关联, 同时增加索引以提高查询性能,
 * (1) 公共图库中的图片无需登录就能查看, 任何人都可以访问, 不需要进行用户认证或成员管理
 * (2) 私有空间则要求用户登录, 且访问权限严格控制, 通常只有拥有者才能查看或修改空间内容
 * (3) 公共图库没有额度限制, 私有空间会有图片大小、数量等方面的限制, 从而管理用户的存储资源和空间配额, 而公共图库完全不受这些限制
 * (4) 公有图库需要经过审核, 但是私有图库没有审核的说法, 只需要是拥有者就可以看到
 * (5) space 私有图库最终会在远端 COS 中使用 space 目录来存储, 和公有图库中的 public 不同
 * <p>
 * 2. 协作空间
 * 协作空间是上述两个功能的衍生, 需要实现以下目标:
 * (1) 数据关联表及管理服务
 * (2) 使用 Sa-token 框架实现 RBAC 角色权限控制以及对应的操作服务
 * a. 基于 用户、角色、权限 三个对象的角色访问控制, 一个用户可以有多个角色, 一个角色可以有多个权限
 * b. 因此正常来说标准实现需要 5 张表: 用户表、角色表、权限表、用户角色关联表、角色权限关联表
 * (3) 使用 Apache ShardingSphere 框架实现动态分表, 对高级用户的协作空间进行单独表维护
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

    @Operation(summary = "获取空间等级描述网络接口")
    @SaCheckLogin
    @PostMapping("/level")
    public BaseResponse<List<SpaceLevelInfo>> spaceLevel() {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceGetLevelInfo());
    }

    @Operation(summary = "根据类型来创建当前用户的专属空间(私有空间/协作空间)网络接口")
    @SaCheckLogin
    @PostMapping("/create")
    public BaseResponse<SpaceVO> spaceCreate(@RequestBody SpaceCreateRequest spaceCreateRequest) {
        Integer spaceType = spaceCreateRequest.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnums(spaceType);
        ThrowUtils.throwIf(spaceTypeEnum == null, CodeBindMessageEnums.PARAMS_ERROR, "不存在该空间类型");
        ThrowUtils.throwIf(spaceService.spaceGetCurrentLoginUserSpace(spaceTypeEnum) != null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "每个当前登录用户仅能为自己创建一个" + spaceTypeEnum.getDescription());

        Space space = spaceService.spaceSetCurrentLoginUserSpace(spaceTypeEnum, SpaceLevelEnum.COMMON, spaceCreateRequest.getSpaceName());
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(space));
    }

    @Operation(summary = "根据类型来销毁当前用户的专属空间(私有空间/协作空间)网络接口")
    @SaCheckLogin
    @PostMapping("/destroy")
    public BaseResponse<Boolean> spaceDestroy(@RequestBody SpaceDestroyRequest spaceDestroyRequest) {
        Integer spaceType = spaceDestroyRequest.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnums(spaceType);
        Space space = spaceService.spaceGetCurrentLoginUserSpace(spaceTypeEnum);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "当前登录用户空间不存在" + spaceTypeEnum.getDescription() + "无法进行销毁");
        ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && !Objects.equals(space.getUserId(), userService.userGetCurrentLonginUserId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您不是该空间的所属者无法修改该空间");

        boolean result = spaceService.spaceDelete(new SpaceDeleteRequest().setId(space.getId()));
        return TheResult.success(CodeBindMessageEnums.SUCCESS, result);
    }

    @Operation(summary = "根据类型来编辑当前用户的专属空间(私有空间/协作空间)网络接口")
    @SaCheckLogin
    @PostMapping("/edit")
    public BaseResponse<SpaceVO> spaceEdit(@RequestBody SpaceEditRequest spaceEditRequest) {
        Integer spaceType = spaceEditRequest.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnums(spaceType);
        Space space = spaceService.spaceGetCurrentLoginUserSpace(spaceTypeEnum);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "当前登录用户空间不存在" + spaceTypeEnum.getDescription() + "无法进行编辑");
        ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && !Objects.equals(space.getUserId(), userService.userGetCurrentLonginUserId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您不是该空间的所属者无法修改该空间");

        // TODO: 目前最多就修改个名字, 后续可以添加修改空间类型或是修改空间等级, 或者是提供捐赠公共空间模式
        Space myspace = spaceService.spaceUpdate(
                new SpaceUpdateRequest()
                        .setId(space.getId())
                        .setName(spaceEditRequest.getSpaceName())
        );
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(myspace));
    }

    @Operation(summary = "根据类型来查找当前用户的专属空间(私有空间/协作空间)网络接口")
    @SaCheckLogin
    @PostMapping("/query")
    public BaseResponse<SpaceVO> spaceQuery(@RequestBody SpaceQueryRequest spaceEditRequest) {
        Integer spaceType = spaceEditRequest.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnums(spaceType);
        Space space = spaceService.spaceGetCurrentLoginUserSpace(spaceTypeEnum);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "当前登录用户空间不存在" + spaceTypeEnum.getDescription() + "无法进行查找");
        ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && !Objects.equals(space.getUserId(), userService.userGetCurrentLonginUserId()), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您不是该空间的所属者无法修改该空间");

        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(space));
    }

    @Operation(summary = "根据具体的空间标识值来查找指定的协作空间网络接口, 注意需要这种查询只能查看当前用户加入的协作空间")
    @SaCheckLogin
    @SaCheckPermission({"picture:view"})
    @PostMapping("/query/id")
    public BaseResponse<SpaceVO> spaceQueryById(@RequestBody SpaceQueryByIdRequest spaceQueryByIdRequest) {
        Long spaceId = spaceQueryByIdRequest.getSpaceId();
        Space space = spaceService.spaceSearchById(spaceId);
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "该空间不存在");
        ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) == SpaceTypeEnum.SELF && space.getUserId() != userService.userGetCurrentLonginUserId(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "当前用户无法查看该私有空间");
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(space));
    }

}
