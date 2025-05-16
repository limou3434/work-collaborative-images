package cn.com.edtechhub.workcollaborativeimages.controller;

import cn.com.edtechhub.workcollaborativeimages.annotation.CacheSearchOptimization;
import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.*;
import cn.com.edtechhub.workcollaborativeimages.model.vo.SpaceVO;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
     * 注入空间服务依赖
     */
    @Resource
    private SpaceService spaceService;

    /**
     * 注入事务管理依赖
     */
    @Resource
    TransactionTemplate transactionTemplate;

    /// 管理接口 ///
    @Operation(summary = "空间添加网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/add")
//    @SentinelResource(value = "adminSpaceAdd")
    public BaseResponse<Space> adminSpaceAdd(@RequestBody AdminSpaceAddRequest adminSpaceAddRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceAdd(adminSpaceAddRequest)); // 可以直接绕过 COS 进行添加落库
    }

    @Operation(summary = "空间删除网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/delete")
//    @SentinelResource(value = "adminSpaceDelete")
    public BaseResponse<Boolean> adminSpaceDelete(@RequestBody AdminSpaceDeleteRequest adminSpaceDeleteRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceDelete(adminSpaceDeleteRequest)); // TODO: 实际上管理员删除接口最重要的一点就是可以直接清理 COS 上的空间, 但是普通用户只是去除数据库中的关联而已
    }

    @Operation(summary = "空间更新网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/update")
//    @SentinelResource(value = "adminSpaceUpdate")
    public BaseResponse<Space> adminSpaceUpdate(@RequestBody AdminSpaceUpdateRequest adminSpaceUpdateRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceUpdate(adminSpaceUpdateRequest)); // 可以直接绕过 COS 进行更新落库
    }

    @Operation(summary = "空间查询网络接口(管理)")
    @SaCheckLogin
    @SaCheckRole("admin")
    @PostMapping("/admin/search")
//    @SentinelResource(value = "adminSpaceSearch")
    public BaseResponse<Page<Space>> adminSpaceSearch(@RequestBody AdminSpaceSearchRequest adminSpaceSearchRequest) {
        return TheResult.success(CodeBindMessageEnums.SUCCESS, spaceService.spaceSearch(adminSpaceSearchRequest)); // 这个接口只是获取用户 id 不用获取详细的用户信息
    }

    /// 普通接口 ///
    @Operation(summary = "创建空间网络接口")
    @SaCheckLogin
    @PostMapping("/create")
    public BaseResponse<SpaceVO> spaceCreate(@RequestBody SpaceCreateRequest spaceCreateRequest) {
        Long userId = Long.valueOf(StpUtil.getLoginId().toString()); // 获取当前登录用户的 id 值
        String lock = String.valueOf(userId).intern();
        synchronized (lock) { // 针对用户进行加锁 TODO: 这种加锁有可能导致字符串池膨胀(目前概率较低), 可以考虑使用 Guava Cache
            return TheResult.success(
                    CodeBindMessageEnums.SUCCESS,
                    transactionTemplate.execute(status -> {
                        // 若普通用户已经存在自己的空间则不允创建多余的空间
                        var adminSpaceSearchRequest = new AdminSpaceSearchRequest();
                        BeanUtils.copyProperties(spaceCreateRequest, adminSpaceSearchRequest);
                        log.debug("检查报文是否转化正常 {}", adminSpaceSearchRequest);
                        Page<Space> spacePage = spaceService.spaceSearch(adminSpaceSearchRequest);
                        ThrowUtils.throwIf(spacePage.getTotal() >= 1, new BusinessException(CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "每个用户仅能有一个私有空间"));

                        // 若普通用户尚未存在自己的空间则允许创建多余的空间
                        var adminSpaceAddRequest = new AdminSpaceAddRequest();
                        adminSpaceAddRequest.setSpaceLevel(SpaceLevelEnums.COMMON.getCode()); // 默认为普通版
                        BeanUtils.copyProperties(spaceCreateRequest, adminSpaceAddRequest);
                        return SpaceVO.removeSensitiveData(spaceService.spaceAdd(adminSpaceAddRequest));
                    }));
        }
    }

    @Operation(summary = "销毁空间网络接口")
    @SaCheckLogin
    @PostMapping("/destroy")
    public BaseResponse<Boolean> spaceDestroy(@RequestBody SpaceDestroyRequest SpaceDestroyRequest) {
        return TheResult.notyet();
    }

    @Operation(summary = "编辑空间网络接口")
    @SaCheckLogin
    @PostMapping("/edit")
    public BaseResponse<SpaceVO> spaceEdit(@RequestBody SpaceEditRequest SpaceEditRequest) {
        return TheResult.notyet();
    }

    @Operation(summary = "查找空间网络接口")
    @SaCheckLogin
    @PostMapping("/query")
    @CacheSearchOptimization(ttl = 60)
    public BaseResponse<Page<SpaceVO>> spaceQuery(@RequestBody SpaceQueryRequest SpaceQueryRequest) {
        return TheResult.notyet();
    }

}

