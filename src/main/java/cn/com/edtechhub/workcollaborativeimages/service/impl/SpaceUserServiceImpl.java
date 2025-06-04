package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.annotation.LogParams;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.mapper.SpaceUserMapper;
import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceUserService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * @author ljp
 * @description 针对表【space_user(空间用户关联表)】的数据库操作Service实现
 * @createDate 2025-06-03 13:21:29
 */
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
        implements SpaceUserService {

    /// 依赖注入 ///

    /**
     * 注入事务管理依赖
     */
    @Resource
    TransactionTemplate transactionTemplate;

    /**
     * 注入空间服务依赖
     */
    @Resource
    SpaceService spaceService;

    /**
     * 注入用户服务依赖
     */
    @Resource
    UserService userService;

    /// 服务实现 ///

    /**
     * 空间用户关联添加服务
     */
    public SpaceUser spaceUserAdd(SpaceUserAddRequest spaceUserAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceUserAddRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");

        Long spaceId = spaceUserAddRequest.getSpaceId();
        ThrowUtils.throwIf(
                spaceId == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "空间标识不能为空"
        );
        ThrowUtils.throwIf(
                spaceService.spaceSearchById(spaceId) == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "该空间不存在"
        );

        Long userId = spaceUserAddRequest.getUserId();
        ThrowUtils.throwIf(
                userId == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "用户标识不能为空"
        );
        ThrowUtils.throwIf(
                userService.userSearchById(userId) == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "该用户不存在"
        );

        Integer spaceUserRole = spaceUserAddRequest.getSpaceRole();
        ThrowUtils.throwIf(
                spaceUserRole == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "空间权限不能为空"
        );
        ThrowUtils.throwIf(
                SpaceRoleEnums.getEnums(spaceUserRole) == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "该角色不存在"
        );

        // 服务实现
        return transactionTemplate.execute(status -> {
            // 创建实例
            SpaceUser spaceUser = SpaceUserAddRequest.copyProperties2Entity(spaceUserAddRequest);
            // 操作数据库
            try {
                boolean result = this.save(spaceUser); // 保存实例的同时利用唯一键约束避免并发问题
                ThrowUtils.throwIf(!result, CodeBindMessageEnums.OPERATION_ERROR, "添加出错");
            } catch (DuplicateKeyException e) { // 无需加锁, 只需要设置唯一键就足够应对并发场景
                ThrowUtils.throwIf(true, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "已经存在该空间用户关联");
            }
            return this.getById(spaceUser.getId());
        });
    }

    /**
     * 空间用户关联删除服务
     */
    public Boolean spaceUserDelete(SpaceUserDeleteRequest spaceUserDeleteRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceUserDeleteRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = spaceUserDeleteRequest.getId();
        ThrowUtils.throwIf(id == null, CodeBindMessageEnums.PARAMS_ERROR, "空间用户关联标识不能为空");
        ThrowUtils.throwIf(id <= 0, CodeBindMessageEnums.PARAMS_ERROR, "空间用户关联标识不得非法, 必须是正整数");

        // 服务实现
        return transactionTemplate.execute(status -> {
            boolean result = this.removeById(id);
            ThrowUtils.throwIf(!result, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "删除空间用户关联失败");
            return true;
        });
    }

    /**
     * 空间用户关联更新服务
     */
    public SpaceUser spaceUserUpdate(SpaceUserUpdateRequest spaceUserUpdateRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceUserUpdateRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = spaceUserUpdateRequest.getId();
        ThrowUtils.throwIf(
                id == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得为空"
        );
        ThrowUtils.throwIf(
                id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得非法, 必须是正整数"
        );

        Long spaceId = spaceUserUpdateRequest.getSpaceId();
        ThrowUtils.throwIf(
                spaceId != null && spaceService.spaceSearchById(spaceId) == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "该空间不存在"
        );

        Long userId = spaceUserUpdateRequest.getUserId();
        ThrowUtils.throwIf(
                userId != null && userService.userSearchById(userId) == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "该用户不存在"
        );

        Integer spaceUserRole = spaceUserUpdateRequest.getSpaceRole();
        ThrowUtils.throwIf(
                spaceUserRole != null && SpaceRoleEnums.getEnums(spaceUserRole) == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "不存在该空间角色"
        );

        // 服务实现
        return transactionTemplate.execute(status -> {
            // 创建实例
            SpaceUser spaceUser = SpaceUserUpdateRequest.copyProperties2Entity(spaceUserUpdateRequest);
            // 操作数据库
            this.updateById(spaceUser);
            return this.getById(id);
        });
    }

    /**
     * 空间用户关联查询服务
     */
    public Page<SpaceUser> spaceUserSearch(SpaceUserSearchRequest spaceUserSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceUserSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = spaceUserSearchRequest.getId();
        ThrowUtils.throwIf(
                id != null && id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "空间用户关联标识不得非法"
        );
        Long spaceId = spaceUserSearchRequest.getId();
        ThrowUtils.throwIf(
                spaceId != null && spaceId <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "空间标识不得非法"
        );
        Long userId = spaceUserSearchRequest.getId();
        ThrowUtils.throwIf(
                userId != null && userId <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "用户标识不得非法"
        );

        // 服务实现
        LambdaQueryWrapper<SpaceUser> queryWrapper = this.getQueryWrapper(spaceUserSearchRequest); // 构造查询条件
        Page<SpaceUser> page = new Page<>(spaceUserSearchRequest.getPageCurrent(), spaceUserSearchRequest.getPageSize()); // 获取分页对象
        return this.page(page, queryWrapper); // 返回分页结果
    }

    @Override
    @LogParams
    public SpaceUser spaceUserSearchById(Long id) {
        ThrowUtils.throwIf(
                id == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得为空"
        );
        ThrowUtils.throwIf(
                id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得非法"
        );
        return this.getById(id);
    }

    /// 私有方法 ///

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<SpaceUser> getQueryWrapper(SpaceUserSearchRequest spaceUserSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(spaceUserSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空");

        // 取得需要查询的参数
        Long id = spaceUserSearchRequest.getId();
        Long spaceId = spaceUserSearchRequest.getSpaceId();
        Long userId = spaceUserSearchRequest.getUserId();
        String sortOrder = spaceUserSearchRequest.getSortOrder();
        String sortField = spaceUserSearchRequest.getSortField();

        // 获取包装器进行返回
        LambdaQueryWrapper<SpaceUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(id != null, SpaceUser::getId, id)
                .eq(spaceId != null, SpaceUser::getSpaceId, spaceId)
                .eq(userId != null, SpaceUser::getUserId, userId)
                .orderBy(
                        StringUtils.isNotBlank(sortField) && !StringUtils.containsAny(sortField, "=", "(", ")", " "), // 不能包含 =、(、) 或空格等特殊字符, 避免潜在的 SQL 注入或不合法的排序规则
                        sortOrder.equals("ascend"), // 这里结果为 true 代表 ASC 升序, false 代表 DESC 降序
                        SpaceUser::getId // 默认按照标识排序
                );
        return lambdaQueryWrapper;
    }

}




