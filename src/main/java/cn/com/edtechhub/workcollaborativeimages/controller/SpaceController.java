package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnums;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @Operation(summary = "👑空间添加网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/add")
    public BaseResponse<Space> adminSpaceAdd(@RequestBody SpaceAddRequest spaceAddRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceAdd(spaceAddRequest)); // 可以直接绕过 COS 进行添加落库
    }

    @Operation(summary = "👑空间删除网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminSpaceDelete(@RequestBody SpaceDeleteRequest spaceDeleteRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceDelete(spaceDeleteRequest)); // TODO: 实际上管理员删除接口最重要的一点就是可以直接清理 COS 上的空间, 但是普通用户只是去除数据库中的关联而已
    }

    @Operation(summary = "👑空间更新网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/update")
    public BaseResponse<Space> adminSpaceUpdate(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceUpdate(spaceUpdateRequest)); // 可以直接绕过 COS 进行更新落库
    }

    @Operation(summary = "👑空间查询网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/search")
    public BaseResponse<Page<Space>> adminSpaceSearch(@RequestBody SpaceSearchRequest spaceSearchRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceSearch(spaceSearchRequest)); // 这个接口只是获取用户 id 不用获取详细的用户信息
    }

    /// 普通接口 ///
    @Operation(summary = "创建空间网络接口")
    @SaCheckLogin
    @PostMapping("/create")
    public BaseResponse<SpaceVO> spaceCreate(@RequestBody SpaceCreateRequest spaceCreateRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceCreateRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "错误调用");

        // 处理请求
        var request = SpaceAddRequest.copyProperties(spaceCreateRequest);
        ThrowUtils.throwIf(spaceService.spaceGetCurrentLoginUserPrivateSpaces() != null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "每个用户仅能有一个私有空间");
        request
                .setUserId(userService.userGetCurrentLonginUserId()) // 强制用户只能创建属于自己的私有空间
                .setSpaceLevel(SpaceLevelEnums.COMMON.getCode()) // 强制用户只能得到普通版本私有空间
        ;

        // 响应数据
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(spaceService.spaceAdd(request)));
    }

    @Operation(summary = "销毁空间网络接口")
    @SaCheckLogin
    @PostMapping("/destroy")
    public BaseResponse<Boolean> spaceDestroy(@RequestBody SpaceDestroyRequest spaceDestroyRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceDestroyRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "错误调用");

        // 处理请求
        var request = SpaceDeleteRequest.copyProperties(spaceDestroyRequest);
        Long userId = userService.userGetCurrentLonginUserId();
        List<Space> spaceList = spaceService.spaceSearch(new SpaceSearchRequest().setId(spaceDestroyRequest.getId())).getRecords(); // 获取对应的空间
        ThrowUtils.throwIf(spaceList.isEmpty(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "不存在该空间, 无法销毁");
        ThrowUtils.throwIf(!Objects.equals(spaceList.get(0).getUserId(), userId), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您无法销毁不是自己的空间"); // 若用户不是空间的所属人则不允许销毁空间

        // 响应数据
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceDelete(request));
    }

    @Operation(summary = "编辑空间网络接口")
    @SaCheckLogin
    @PostMapping("/edit")
    public BaseResponse<SpaceVO> spaceEdit(@RequestBody SpaceEditRequest spaceEditRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceEditRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "错误调用");

        // 处理请求
        var request = SpaceUpdateRequest.copyProperties(spaceEditRequest);
        Long userId = userService.userGetCurrentLonginUserId();
        List<Space> spaceList = spaceService.spaceSearch(new SpaceSearchRequest().setId(spaceEditRequest.getId())).getRecords(); // 获取对应的空间
        ThrowUtils.throwIf(spaceList.isEmpty(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "不存在该空间, 无法修改");
        ThrowUtils.throwIf(!Objects.equals(spaceList.get(0).getUserId(), userId), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您无法修改不是自己的空间"); // 若用户不是空间的所属人则不允许修改空间

        // 响应数据
        return TheResult.success(CodeBindMessageEnums.SUCCESS, SpaceVO.removeSensitiveData(spaceService.spaceUpdate(request)));
    }

    @Operation(summary = "查找空间网络接口")
    @SaCheckLogin
    @PostMapping("/query")
    public BaseResponse<Page<SpaceVO>> spaceQuery(@RequestBody SpaceQueryRequest SpaceQueryRequest) {
        // 检查参数
        ThrowUtils.throwIf(SpaceQueryRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "错误调用");

        // 处理请求
        var request = SpaceSearchRequest.copyProperties(SpaceQueryRequest);
        Long userId = userService.userGetCurrentLonginUserId();
        request
                .setUserId(userId) // 强制用户只能查询属于自己的私有空间
        ;

        // 响应数据
        Page<Space> spacePage = spaceService.spaceSearch(request);
        List<SpaceVO> spaceVOList = spacePage.getRecords()
                .stream()
                .map(SpaceVO::removeSensitiveData)
                .collect(Collectors.toList());
        Page<SpaceVO> spaceVOPage = new Page<>();
        spaceVOPage.setRecords(spaceVOList);
        spaceVOPage.setTotal(spacePage.getTotal());
        spaceVOPage.setSize(spacePage.getSize());
        spaceVOPage.setCurrent(spacePage.getCurrent());
        System.out.println(spaceVOPage);
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceVOPage);
    }

    @Operation(summary = "获取空间等级描述网络接口")
    @SaCheckLogin
    @PostMapping("/level")
    public BaseResponse<List<SpaceLevelInfo>> spaceLevel() {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceGetLevelInfo());
    }

}
