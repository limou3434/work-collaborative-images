package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.annotation.LogParams;
import cn.com.edtechhub.workcollaborativeimages.constant.SpaceConstant;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnums;
import cn.com.edtechhub.workcollaborativeimages.mapper.SpaceMapper;
import cn.com.edtechhub.workcollaborativeimages.model.dto.SpaceLevelInfo;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
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

    @Override
    @LogParams
    public Space spaceAdd(SpaceAddRequest spaceAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceAddRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        this.checkParameters(spaceAddRequest.getSpaceName(), spaceAddRequest.getSpaceLevel());

        // 服务实现
        Long userId = userService.userGetCurrentLonginUserId(); // 但是不能数据库的利用唯一键约束避免并发问题, 因为这样后续就无法拓展单用户多空间
        synchronized (String.valueOf(userId).intern()) { // 针对用户进行加锁 TODO: 这种加锁有可能导致字符串池膨胀(目前概率较低), 可以考虑使用 Guava Cache
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
                var space = SpaceAddRequest.copyProperties2Entity(spaceAddRequest); // 创建实例
                Integer type = spaceAddRequest.getType();
                space = this.fillSpaceBySpaceLevel(space);
                boolean exists = this
                        .lambdaQuery()
                        .eq(Space::getUserId, userId)
                        .eq(Space::getType, type) // TODO: 还是有点问题的
                        .exists();
                ThrowUtils.throwIf(exists, CodeBindMessageEnums.OPERATION_ERROR, "每个用户仅能有一个私有空间");
                try {
                    boolean result = this.save(space);
                    ThrowUtils.throwIf(!result, CodeBindMessageEnums.OPERATION_ERROR, "添加出错");
                } catch (DuplicateKeyException e) {
                    ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "已经存在该空间, 或者曾经被删除");
                }
                return this.getById(space.getId());
            });
        }
    }

    @Override
    @LogParams
    public Boolean spaceDelete(SpaceDeleteRequest spaceDeleteRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceDeleteRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "空间删除请求体不能为空");
        Long id = spaceDeleteRequest.getId();
        ThrowUtils.throwIf(id == null, CodeBindMessageEnums.PARAMS_ERROR, "空间标识不能为空");
        ThrowUtils.throwIf(id <= 0, CodeBindMessageEnums.PARAMS_ERROR, "空间标识不合法, 必须是正整数");

        // 服务实现
        return transactionTemplate.execute(status -> {
            boolean result = this.removeById(spaceDeleteRequest.getId());
            ThrowUtils.throwIf(!result, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "删除空间失败, 也许该空间不存在或者已经被删除");
            return true;
        });
    }

    @Override
    @LogParams
    public Space spaceUpdate(SpaceUpdateRequest spaceUpdateRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceUpdateRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "空间更新请求体不能为空");
        Long id = spaceUpdateRequest.getId();
        String name = spaceUpdateRequest.getSpaceName();
        Integer level = spaceUpdateRequest.getSpaceLevel();
        ThrowUtils.throwIf(id == null, CodeBindMessageEnums.PARAMS_ERROR, "空间标识不能为空");
        ThrowUtils.throwIf(id <= 0, CodeBindMessageEnums.PARAMS_ERROR, "空间标识不合法, 必须是正整数");
        this.checkParameters(name, level);

        // 服务实现
        return transactionTemplate.execute(status -> {
            Space space = SpaceUpdateRequest.copyProperties2Entity(spaceUpdateRequest);
            this.fillSpaceBySpaceLevel(space);
            this.updateById(space);
            return this.getById(id);
        });
    }

    @Override
    @LogParams
    public Page<Space> spaceSearch(SpaceSearchRequest spaceSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "空间查询请求不能为空");
        Long id = spaceSearchRequest.getId();
        String name = spaceSearchRequest.getSpaceName();
        ThrowUtils.throwIf(id != null && id <= 0, CodeBindMessageEnums.PARAMS_ERROR, "空间标识不合法");
        ThrowUtils.throwIf(StrUtil.isNotBlank(name) && name.length() > SpaceConstant.NAME_LENGTH, CodeBindMessageEnums.PARAMS_ERROR, "空间名称不能大于" + SpaceConstant.NAME_LENGTH + "位字符");
        ThrowUtils.throwIf(spaceSearchRequest.getSpaceLevel() != null && SpaceLevelEnums.getLevelDescription(spaceSearchRequest.getSpaceLevel()) == null, CodeBindMessageEnums.PARAMS_ERROR, "空间等级非法");

        // 服务实现
        LambdaQueryWrapper<Space> queryWrapper = this.getQueryWrapper(spaceSearchRequest); // 构造查询条件
        Page<Space> page = new Page<>(spaceSearchRequest.getPageCurrent(), spaceSearchRequest.getPageSize()); // 获取分页对象
        return this.page(page, queryWrapper); // 返回分页结果
    }

    @Override
    @LogParams
    public Space spaceSearchById(Long id) {
        ThrowUtils.throwIf(id == null, CodeBindMessageEnums.PARAMS_ERROR, "请求 id 不能为空");
        return this.getById(id);
    }

    public void spaceCheckAndIncreaseCurrent(Picture picture) {
        if (picture.getSpaceId() == 0) {
            log.debug("该图片属于公有图库无需改动额度");
            return;
        }

        Space space = this.getById(picture.getSpaceId());
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.SYSTEM_ERROR, "图片所属的空间发生意外错误");

        space.setTotalSize(space.getTotalSize() + picture.getPicSize());
        space.setTotalCount(space.getTotalCount() + 1);

        ThrowUtils.throwIf(space.getMaxSize() < space.getTotalSize(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "大小额度不够");
        ThrowUtils.throwIf(space.getMaxCount() < space.getTotalCount(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "数量额度不够");

        this.updateById(space);
        log.debug("额度充足");
    }

    public void spaceCheckAndDecreaseCurrent(Picture picture) {
        if (picture.getSpaceId() == 0) {
            log.debug("该图片属于公有图库无需改动额度");
            return;
        }

        Space space = this.getById(picture.getSpaceId());
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.SYSTEM_ERROR, "图片所属的空间发生意外错误");

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
        List<Space> spaceList = this.spaceSearch(new SpaceSearchRequest().setUserId(userId)).getRecords();
        return spaceList.isEmpty() ? null : spaceList.get(0);
    }

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<Space> getQueryWrapper(SpaceSearchRequest spaceSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(spaceSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空");

        // 取得需要查询的参数
        Long id = spaceSearchRequest.getId();
        String name = spaceSearchRequest.getSpaceName();
        Integer level = spaceSearchRequest.getSpaceLevel();
        Long userId = spaceSearchRequest.getUserId();
        String sortField = spaceSearchRequest.getSortField();
        String sortOrder = spaceSearchRequest.getSortOrder();

        // 获取包装器进行返回
        LambdaQueryWrapper<Space> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(id != null, Space::getId, id)
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
    private Space fillSpaceBySpaceLevel(Space space) {
        SpaceLevelEnums spaceLevelEnums = SpaceLevelEnums.getLevelDescription(space.getSpaceLevel());
        ThrowUtils.throwIf(spaceLevelEnums == null, CodeBindMessageEnums.PARAMS_ERROR, "空间等级非法, 必须设置一个合法的等级, 否则无法设置额度");
        space.setMaxSize(spaceLevelEnums.getMaxSize());
        space.setMaxCount(spaceLevelEnums.getMaxCount());
        return space;
    }

    /**
     * 检查添加或更新的参数
     */
    private void checkParameters(String name, Integer level) {
        ThrowUtils.throwIf(StrUtil.isNotBlank(name) && name.length() > SpaceConstant.NAME_LENGTH, CodeBindMessageEnums.PARAMS_ERROR, "空间名称不能大于 SpaceConstant.NAME_LENGTH 个字符");
        ThrowUtils.throwIf(level == null, CodeBindMessageEnums.PARAMS_ERROR, "空间等级不能为空");
        ThrowUtils.throwIf(SpaceLevelEnums.getLevelDescription(level) == null, CodeBindMessageEnums.PARAMS_ERROR, "空间等级不合法");
    }

}
