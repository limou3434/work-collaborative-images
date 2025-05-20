package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.mapper.SpaceMapper;
import cn.com.edtechhub.workcollaborativeimages.model.dto.SpaceLevelInfo;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.AdminSpaceUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 注入用户服务依赖
     */
    @Resource
    UserService userService;

    public Space spaceAdd(AdminSpaceAddRequest adminSpaceAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(adminSpaceAddRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间添加请求体为空"));
        ThrowUtils.throwIf(StrUtil.isNotBlank(adminSpaceAddRequest.getSpaceName()) && adminSpaceAddRequest.getSpaceName().length() > 30, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间名称不能大于 30 个字符"));
        ThrowUtils.throwIf(adminSpaceAddRequest.getSpaceLevel() == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间等级不能为空"));
        ThrowUtils.throwIf(SpaceLevelEnums.getLevelDescription(adminSpaceAddRequest.getSpaceLevel()) == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间等级不合法"));

        // 创建空间实例
        var space = new Space();
        BeanUtils.copyProperties(adminSpaceAddRequest, space);

        // 保存实例, 同时不能数据库的利用唯一键约束避免并发问题, 因为这样后续就无法拓展单用户多空间
        Long userId = userService.userGetCurrentLonginUserId();
        String lock = String.valueOf(userId).intern();
        synchronized (lock) { // 针对用户进行加锁 TODO: 这种加锁有可能导致字符串池膨胀(目前概率较低), 可以考虑使用 Guava Cache
            return transactionTemplate.execute(status -> { // 必须在锁内处理事务
                // 锁内事务原理
                // A 线程进入方法, 开始事务
                // B 线程也进入方法, 开始另一个事务
                // A 线程执行到 synchronized 加锁, 拿到锁
                // A 查询数据库，此时还没有空间 → 合法 → 创建空间 → 提交事务
                // A 释放锁
                // B 线程获得锁, 但它事务早就开始了
                // B 线程查询数据库, 此时看不到 A 的新数据（隔离级别问题）
                // B 线程也判断为合法 → 也创建了空间
                // 若普通用户尚未存在自己的空间则允许创建多余的空间
                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).exists();
                ThrowUtils.throwIf(exists, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "每个用户仅能有一个私有空间"));
                this.save(this.fillSpaceBySpaceLevel(space));

                // 添加空间后最好把空间的信息也返回, 这样方便前端做实时的数据更新
                return this.getById(space.getId());
            });
        }
    }

    @Transactional
    public Boolean spaceDelete(AdminSpaceDeleteRequest adminSpaceDeleteRequest) {
        // 检查参数
        ThrowUtils.throwIf(adminSpaceDeleteRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间删除请求体不能为空"));
        ThrowUtils.throwIf(adminSpaceDeleteRequest.getId() == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间标识不能为空"));
        ThrowUtils.throwIf(adminSpaceDeleteRequest.getId() <= 0, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间标识不合法"));

        // 操作数据库
        boolean result = this.removeById(adminSpaceDeleteRequest.getId());
        ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "删除空间失败, 也许该空间不存在或者已经被删除"));
        return true;
    }

    @Transactional
    public Space spaceUpdate(AdminSpaceUpdateRequest adminSpaceUpdateRequest) {
        // 检查参数
        ThrowUtils.throwIf(adminSpaceUpdateRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间更新请求体不能为空"));
        ThrowUtils.throwIf(adminSpaceUpdateRequest.getId() == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间标识不能为空"));
        ThrowUtils.throwIf(adminSpaceUpdateRequest.getId() <= 0, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间标识不合法"));
        ThrowUtils.throwIf(StrUtil.isNotBlank(adminSpaceUpdateRequest.getSpaceName()) && adminSpaceUpdateRequest.getSpaceName().length() > 30, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间名称不能大于 30 个字符"));
        ThrowUtils.throwIf(adminSpaceUpdateRequest.getSpaceLevel() != null && SpaceLevelEnums.getLevelDescription(adminSpaceUpdateRequest.getSpaceLevel()) == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间等级非法"));

        // 更新空间
        Space space = new Space();
        BeanUtils.copyProperties(adminSpaceUpdateRequest, space);
        if (space.getSpaceLevel() != null) {
            this.fillSpaceBySpaceLevel(space);
        }
        this.updateById(space);

        // 更新空间后最好把空间的信息也返回, 这样方便前端做实时的数据更新
        Long id = space.getId();
        return this.getById(id);
    }

    public Page<Space> spaceSearch(AdminSpaceSearchRequest adminSpaceSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(adminSpaceSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间查询请求不能为空"));
        ThrowUtils.throwIf(adminSpaceSearchRequest.getId() != null && adminSpaceSearchRequest.getId() <= 0, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间标识不合法"));
        ThrowUtils.throwIf(StrUtil.isNotBlank(adminSpaceSearchRequest.getSpaceName()) && adminSpaceSearchRequest.getSpaceName().length() > 30, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间名称不能大于 30 个字符"));
        ThrowUtils.throwIf(adminSpaceSearchRequest.getSpaceLevel() != null && SpaceLevelEnums.getLevelDescription(adminSpaceSearchRequest.getSpaceLevel()) == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间等级非法"));

        // TODO: 如果空间传递了 id 选项, 则必然是查询一条记录, 为了提高效率直接查询一条数据

        // 获取查询对象
        LambdaQueryWrapper<Space> queryWrapper = this.getQueryWrapper(adminSpaceSearchRequest); // 构造查询条件

        // 获取分页对象
        Page<Space> page = new Page<>(adminSpaceSearchRequest.getPageCurrent(), adminSpaceSearchRequest.getPageSize()); // 这里还指定了页码和条数

        // 查询空间分页后直接取得内部的列表进行返回
        return this.page(page, queryWrapper); // 调用 MyBatis-Plus 的分页查询方法
    }

    @Transactional
    public void spaceCheckAndIncreaseCurrent(Picture picture) {
        if (picture.getSpaceId() == 0) {
            log.debug("该图片属于公有图库无需改动额度");
            return;
        }

        Space space = this.getById(picture.getSpaceId());
        ThrowUtils.throwIf(space == null, new BusinessException(CodeBindMessageEnums.SYSTEM_ERROR, "图片所属的空间发生意外错误"));

        space.setTotalSize(space.getTotalSize() + picture.getPicSize());
        space.setTotalCount(space.getTotalCount() + 1);

        ThrowUtils.throwIf(space.getMaxSize() < space.getTotalSize(), new BusinessException(CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "大小额度不够"));
        ThrowUtils.throwIf(space.getMaxCount() < space.getTotalCount(), new BusinessException(CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "数量额度不够"));

        this.updateById(space);
        log.debug("额度充足");
    }

    @Transactional
    public void spaceCheckAndDecreaseCurrent(Picture picture) {
        if (picture.getSpaceId() == 0) {
            log.debug("该图片属于公有图库无需改动额度");
            return;
        }

        Space space = this.getById(picture.getSpaceId());
        ThrowUtils.throwIf(space == null, new BusinessException(CodeBindMessageEnums.SYSTEM_ERROR, "图片所属的空间发生意外错误"));

        space.setTotalSize(space.getTotalSize() - picture.getPicSize());
        space.setTotalCount(space.getTotalCount() - 1);

        if (space.getTotalSize() < 0) {
            log.debug("当前大小计算误差, 予以修正");
            space.setTotalSize(0L);
        }

        if (space.getTotalCount() < 0) {
            log.debug("当前数量计算误差, 予以修正");
            space.setTotalCount(0L);
        }

        this.updateById(space);
        log.debug("额度充足");
    }

    public List<SpaceLevelInfo> spaceGetLevelInfo() {
        return Arrays.stream(SpaceLevelEnums.values()) // 获取所有枚举
                .map(spaceLevelEnum -> new SpaceLevelInfo(
                        spaceLevelEnum.getCode(),
                        spaceLevelEnum.getDescription(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
    }

    public Space spaceGetCurrentLoginUserPrivateSpace() {
        Long userId = userService.userGetCurrentLonginUserId();
        List<Space> spaceList = this.spaceSearch(new AdminSpaceSearchRequest().setUserId(userId)).getRecords();
        return spaceList.isEmpty() ? null : spaceList.get(0);
    }

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<Space> getQueryWrapper(AdminSpaceSearchRequest adminSpaceSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(adminSpaceSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空"));

        // 取得需要查询的参数
        Long id = adminSpaceSearchRequest.getId();
        String name = adminSpaceSearchRequest.getSpaceName();
        Integer level = adminSpaceSearchRequest.getSpaceLevel();
        Long userId = adminSpaceSearchRequest.getUserId();
        String sortField = adminSpaceSearchRequest.getSortField();
        String sortOrder = adminSpaceSearchRequest.getSortOrder();

        // 获取包装器进行返回
        LambdaQueryWrapper<Space> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(id != null, Space::getId, id)
                .like(StringUtils.isNotBlank(name), Space::getSpaceName, name)
                .eq(level !=  null, Space::getSpaceLevel, level)
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
    private Space fillSpaceBySpaceLevel(Space space) {
        SpaceLevelEnums spaceLevelEnums = SpaceLevelEnums.getLevelDescription(space.getSpaceLevel());
        ThrowUtils.throwIf(spaceLevelEnums == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "空间等级非法, 必须设置一个合法的等级, 否则无法设置额度"));
        space.setMaxSize(spaceLevelEnums.getMaxSize());
        space.setMaxCount(spaceLevelEnums.getMaxCount());
        return space;
    }

}





