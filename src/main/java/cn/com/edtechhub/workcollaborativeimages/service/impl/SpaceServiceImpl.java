package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.mapper.SpaceMapper;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author ljp
 * @description 针对表【space(空间表)】的数据库操作Service实现
 * @createDate 2025-05-16 08:17:55
 */
@Service
@Slf4j
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceService {

    /**
     * 注入事务管理依赖
     */
    @Resource
    TransactionTemplate transactionTemplate;

    public Space spaceAdd(AdminSpaceAddRequest adminSpaceAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(adminSpaceAddRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间添加请求体为空"));
        ThrowUtils.throwIf(StrUtil.isBlank(adminSpaceAddRequest.getSpaceName()), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间名称不能为空"));
        ThrowUtils.throwIf(adminSpaceAddRequest.getSpaceLevel() == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间等级不能为空"));

        // 创建空间实例
        var space = new Space();
        BeanUtils.copyProperties(adminSpaceAddRequest, space);
        this.fillSpaceBySpaceLevel(space);
        space.setUserId(Long.valueOf(StpUtil.getLoginId().toString()));

        // 保存实例, 同时不能利用唯一键约束避免并发问题, 因为这样后续就无法拓展单用户多空间
        Long userId = Long.valueOf(StpUtil.getLoginId().toString()); // 获取当前登录用户的 id 值
        String lock = String.valueOf(userId).intern();
        synchronized (lock) { // 针对用户进行加锁 TODO: 这种加锁有可能导致字符串池膨胀(目前概率较低), 可以考虑使用 Guava Cache
            return transactionTemplate.execute(status -> { // 必须在锁内处理事务
                // 锁内事务原理
                // A线程进入方法，开始事务
                // B线程也进入方法，开始另一个事务
                // A线程执行到 synchronized 加锁，拿到锁
                // A查询数据库，此时还没有空间 → 合法 → 创建空间 → 提交事务
                // A释放锁
                // B线程获得锁，但它事务早就开始了
                // B线程查询数据库，此时看不到 A 的新数据（隔离级别问题）
                // B线程也判断为合法 → 也创建了空间

                // 若普通用户已经存在自己的空间则不允创建多余的空间
                var adminSpaceSearchRequest = new AdminSpaceSearchRequest();
                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).exists();
                ThrowUtils.throwIf(exists, new BusinessException(CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "每个用户仅能有一个私有空间"));

                // 若普通用户尚未存在自己的空间则允许创建多余的空间
                this.save(space);
                return this.getById(space.getId());
            });
        }
    }

    public Boolean spaceDelete(AdminSpaceDeleteRequest adminSpaceDeleteRequest) {
        ThrowUtils.throwIf(adminSpaceDeleteRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间删除请求体不能为空"));
        Long id = adminSpaceDeleteRequest.getId();
        ThrowUtils.throwIf(id <= 0, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "请求中的 id 不合法"));

        // 操作数据库
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "删除空间失败, 也许该空间不存在或者已经被删除"));
        return true;
    }

    public Space spaceUpdate(AdminSpaceUpdateRequest adminSpaceUpdateRequest) {
        // 检查参数
        ThrowUtils.throwIf(adminSpaceUpdateRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间更新请求体不能为空"));
        ThrowUtils.throwIf(adminSpaceUpdateRequest.getId() == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间 id 不能为空"));
        ThrowUtils.throwIf(adminSpaceUpdateRequest.getId() <= 0, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间 id 必须是正整数"));
        ThrowUtils.throwIf(StrUtil.isNotBlank(adminSpaceUpdateRequest.getSpaceName()) && adminSpaceUpdateRequest.getSpaceName().length() > 30, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间名称不能过长"));
        ThrowUtils.throwIf(adminSpaceUpdateRequest.getSpaceLevel() != null && SpaceLevelEnums.getLevelDescription(adminSpaceUpdateRequest.getSpaceLevel()) == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间等级非法"));

        // 更新空间
        Space space = new Space();
        if (space.getSpaceLevel() != null) this.fillSpaceBySpaceLevel(space);
        BeanUtils.copyProperties(adminSpaceUpdateRequest, space);
        this.updateById(space);

        // 更新空间后最好把空间的信息也返回, 这样方便前端做实时的数据更新
        Long id = space.getId();
        return this.getById(id);
    }

    public Page<Space> spaceSearch(AdminSpaceSearchRequest adminSpaceSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(adminSpaceSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间查询请求不能为空"));

        // 如果空间传递了 id 选项, 则必然是查询一条记录, 为了提高效率直接查询一条数据
        Long spaceId = adminSpaceSearchRequest.getId();
        if (spaceId != null) {
            log.debug("本次查询只需要查询一条记录, 使用 id 字段来提高效率");
            Space space = this.getById(spaceId);
            log.debug("单条查询的图片记录为 {}", space);
            Page<Space> resultPage = new Page<>();
            // 如果空间存在就允许返回结果
            if (space != null) {
                resultPage.setRecords(Collections.singletonList(space));
                resultPage.setTotal(1);
                resultPage.setSize(1);
                resultPage.setCurrent(1);
            }
            // 否则直接返回空页面
            else {
                resultPage.setRecords(Collections.emptyList());
                resultPage.setTotal(0);
                resultPage.setSize(1);
                resultPage.setCurrent(1);
            }
            return resultPage;
        }

        // 获取查询对象
        LambdaQueryWrapper<Space> queryWrapper = this.getQueryWrapper(adminSpaceSearchRequest); // 构造查询条件

        // 获取分页对象
        Page<Space> page = new Page<>(adminSpaceSearchRequest.getPageCurrent(), adminSpaceSearchRequest.getPageSize()); // 这里还指定了页码和条数

        // 查询空间分页后直接取得内部的列表进行返回
        return this.page(page, queryWrapper); // 调用 MyBatis-Plus 的分页查询方法
    }

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<Space> getQueryWrapper(AdminSpaceSearchRequest adminSpaceSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(adminSpaceSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空"));

        // 取得需要查询的参数
        String name = adminSpaceSearchRequest.getSpaceName();
        Integer level = adminSpaceSearchRequest.getSpaceLevel();
        Long userId = adminSpaceSearchRequest.getUserId();
        String sortField = adminSpaceSearchRequest.getSortField();
        String sortOrder = adminSpaceSearchRequest.getSortOrder();

        // 获取包装器进行返回
        LambdaQueryWrapper<Space> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(StringUtils.isNotBlank(name), Space::getSpaceName, name)
                .eq(level != null, Space::getSpaceLevel, level)
                .eq(userId != null, Space::getUserId, userId)
                .orderBy(
                        StringUtils.isNotBlank(sortField) && !StringUtils.containsAny(sortField, "=", "(", ")", " "), // 不能包含 =、(、) 或空格等特殊字符, 避免潜在的 SQL 注入或不合法的排序规则
                        sortOrder.equals("ascend"), // 这里结果为 true 代表 ASC 升序, false 代表 DESC 降序
                        Space::getCreateTime // 默认按照创建时间排序
                )
        ;
        return lambdaQueryWrapper;
    }

    /**
     * 根据空间的等级填充空间图片的最大总大小和最大图片数量
     */
    private void fillSpaceBySpaceLevel(Space space) {
        SpaceLevelEnums spaceLevelEnums = SpaceLevelEnums.getLevelDescription(space.getSpaceLevel());
        ThrowUtils.throwIf(spaceLevelEnums == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间等级非法, 必须设置一个合法的等级, 否则无法设置额度"));
        space.setMaxSize(spaceLevelEnums.getMaxSize());
        space.setMaxCount(spaceLevelEnums.getMaxCount());
    }

}





