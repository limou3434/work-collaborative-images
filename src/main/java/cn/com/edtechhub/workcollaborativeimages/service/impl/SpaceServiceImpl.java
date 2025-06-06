package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.annotation.LogParams;
import cn.com.edtechhub.workcollaborativeimages.constant.SpaceConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceUserRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.mapper.SpaceMapper;
import cn.com.edtechhub.workcollaborativeimages.model.dto.SpaceLevelInfo;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceService.SpaceUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceUserService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    /// 依赖注入 ///

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

    /**
     * 注入图片服务依赖
     */
    @Resource
    @Lazy
    PictureService pictureService;

    /**
     * 注入空间用户服务依赖
     */
    @Resource
    @Lazy
    SpaceUserService spaceUserService;
    @Autowired
    private SpaceService spaceService;

    @Override
    @LogParams
    public Space spaceAdd(SpaceAddRequest spaceAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceAddRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        String name = spaceAddRequest.getName();
        Integer level = spaceAddRequest.getLevel();
        ThrowUtils.throwIf(
                name == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "必须设置名字"
        );
        ThrowUtils.throwIf(
                name.length() > SpaceConstant.MAX_NAME_LENGTH,
                CodeBindMessageEnums.PARAMS_ERROR,
                "名字不得大于" + SpaceConstant.MAX_NAME_LENGTH + "位字符"
        );
        ThrowUtils.throwIf(
                level == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "必须设置空间等级"
        );
        ThrowUtils.throwIf(
                SpaceLevelEnums.getEnums(level) == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "空间等级不合法"
        );

        // 服务实现
        return transactionTemplate.execute(status -> { // 必须在锁内处理事务
            // 创建实例
            var space = SpaceAddRequest.copyProperties2Entity(spaceAddRequest);
            // 操作数据库
            try {
                boolean result = this.save(this.fillSpaceBySpaceLevel(space));
                ThrowUtils.throwIf(!result, CodeBindMessageEnums.OPERATION_ERROR, "添加出错");
            } catch (DuplicateKeyException e) {
                ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "已经存在该空间, 或者曾经被删除");
            }
            return this.getById(space.getId());
        });
    }

    @Override
    @LogParams
    public Boolean spaceDelete(SpaceDeleteRequest spaceDeleteRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceDeleteRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = spaceDeleteRequest.getId();
        ThrowUtils.throwIf(
                id == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不能为空"
        );
        ThrowUtils.throwIf(
                id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得非法, 必须是正整数"
        );

        // 服务实现
        return transactionTemplate.execute(status -> {
            // 先考虑把和私有图库绑定的图片全部从远端图库中删除
            Page<Picture> picturePage = pictureService.pictureSearch(new PictureSearchRequest().setSpaceId(id));
            List<Picture> pictureList = picturePage.getRecords();
            if (pictureList.isEmpty()) {
                log.debug("该空间下没有图片资源, 无需清空");
            } else {
                log.debug("该空间下存在图片资源, 需要清空");
                // 这里调用远端图库删除图片
                for (Picture pic : pictureList) {
                    try {
                        pictureService.pictureDelete(new PictureDeleteRequest().setId(pic.getId()));
                    } catch (Exception e) {
                        ThrowUtils.throwIf(true, CodeBindMessageEnums.SYSTEM_ERROR, "删除图片 " + pic.getId() + "失败");
                    }
                }
            }

            // 如果是协作空间还需要把所有的成员记录都删除
            List<SpaceUser> spaceUserList = spaceUserService.spaceUserSearch(
                    new SpaceUserSearchRequest()
                            .setSpaceId(id)
            ).getRecords();
            if (spaceUserList != null) {
                List<Long> spaceUserIds = spaceUserList
                        .stream()
                        .map(SpaceUser::getId)
                        .toList()
                ;
                boolean result = userService.removeByIds(spaceUserIds); // 批量删除
                ThrowUtils.throwIf(!result, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "删除协作空间的成员记录失败");
            }

            // 最后才把图库本身删除
            boolean result = this.removeById(id);
            ThrowUtils.throwIf(!result, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "删除空间失败, 也许该空间不存在或者已经被删除");

            return true;
        });
    }

    @Override
    @LogParams
    public Space spaceUpdate(SpaceUpdateRequest spaceUpdateRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceUpdateRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = spaceUpdateRequest.getId();
        String name = spaceUpdateRequest.getName();
        Integer level = spaceUpdateRequest.getLevel();
        ThrowUtils.throwIf(
                id == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不能为空");
        ThrowUtils.throwIf(
                id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "标识不得非法, 必须是正整数"
        );
        ThrowUtils.throwIf(
                StrUtil.isNotBlank(name) && name.length() > SpaceConstant.MAX_NAME_LENGTH,
                CodeBindMessageEnums.PARAMS_ERROR,
                "名字不得大于" + SpaceConstant.MAX_NAME_LENGTH + "位字符"
        );
        ThrowUtils.throwIf(
                level != null && SpaceLevelEnums.getEnums(level) == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "等级不合法"
        );

        // 服务实现
        return transactionTemplate.execute(status -> {
            // 创建实例
            Space space = SpaceUpdateRequest.copyProperties2Entity(spaceUpdateRequest);
            if (spaceUpdateRequest.getLevel() != null) this.fillSpaceBySpaceLevel(space);
            // 操作数据库
            this.updateById(space);
            return this.getById(id);
        });
    }

    @Override
    @LogParams
    public Page<Space> spaceSearch(SpaceSearchRequest spaceSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(spaceSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "请求体不能为空");
        Long id = spaceSearchRequest.getId();
        String name = spaceSearchRequest.getName();
        Integer level = spaceSearchRequest.getLevel();
        ThrowUtils.throwIf(
                id != null && id <= 0,
                CodeBindMessageEnums.PARAMS_ERROR,
                "空间标识不得非法"
        );
        ThrowUtils.throwIf(
                StrUtil.isNotBlank(name) && name.length() > SpaceConstant.MAX_NAME_LENGTH,
                CodeBindMessageEnums.PARAMS_ERROR,
                "名字不能大于" + SpaceConstant.MAX_NAME_LENGTH + "位字符"
        );
        ThrowUtils.throwIf(
                level != null && SpaceLevelEnums.getEnums(level) == null,
                CodeBindMessageEnums.PARAMS_ERROR,
                "等级不合法"
        );

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

    @Override
    @LogParams
    public void spaceCheckAndIncreaseCurrent(Picture picture) {
        if (picture.getSpaceId() == null) {
            log.debug("该图片属于公有图库无需改动额度");
            return;
        }

        Space space = this.getById(picture.getSpaceId());
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.SYSTEM_ERROR, "图片所属的空间发生意外错误");

        Long oldTotalSize = space.getTotalSize();
        Long oldTotalCount = space.getTotalCount();
        Long futureTotalSize = oldTotalSize + picture.getPicSize();
        Long futureTotalCount = oldTotalCount + 1;
        space.setTotalSize(futureTotalSize);
        space.setTotalCount(futureTotalCount);
        log.debug("之前的大小 {} -> 将来的大小 {}", oldTotalSize, futureTotalSize);
        log.debug("之前的数量 {} -> 将来的数量 {}", oldTotalCount, futureTotalCount);

        ThrowUtils.throwIf(space.getMaxSize() < space.getTotalSize(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "大小额度不够");
        ThrowUtils.throwIf(space.getMaxCount() < space.getTotalCount(), CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "数量额度不够");

        this.updateById(space);
        log.debug("额度充足");
    }

    @Override
    @LogParams
    public void spaceCheckAndDecreaseCurrent(Picture picture) {
        if (picture.getSpaceId() == null) {
            log.debug("该图片属于公有图库无需改动额度");
            return;
        }

        Space space = this.getById(picture.getSpaceId());
        ThrowUtils.throwIf(space == null, CodeBindMessageEnums.SYSTEM_ERROR, "图片所属的空间发生意外错误");

        Long oldTotalSize = space.getTotalSize();
        Long oldTotalCount = space.getTotalCount();
        ThrowUtils.throwIf(oldTotalSize == 0 || oldTotalCount == 0, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您的存量为 0 没有任何资源需要删除");

        Long futureTotalSize = oldTotalSize - picture.getPicSize();
        Long futureTotalCount = oldTotalCount - 1;
        space.setTotalSize(futureTotalSize);
        space.setTotalCount(futureTotalCount);
        log.debug("之前的大小 {} -> 将来的大小 {}", oldTotalSize, futureTotalSize);
        log.debug("之前的数量 {} -> 将来的数量 {}", oldTotalCount, futureTotalCount);

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

    @Override
    @LogParams
    public List<SpaceLevelInfo> spaceGetLevelInfo() {
        return Arrays
                .stream(SpaceLevelEnums.values()) // 获取所有枚举
                .map(
                        spaceLevelEnum -> new SpaceLevelInfo(
                                spaceLevelEnum.getCode(),
                                spaceLevelEnum.getDescription(),
                                spaceLevelEnum.getMaxCount(),
                                spaceLevelEnum.getMaxSize())
                )
                .collect(Collectors.toList()
                );
    }

    @Override
    @LogParams
    public Space spaceGetCurrentLoginUserSpace(SpaceTypeEnums spaceType) {
        // 检查参数
        ThrowUtils.throwIf(spaceType == null, CodeBindMessageEnums.PARAMS_ERROR, "查询空间的类型不能为空");

        // 设置查询请求体
        var spaceSearchRequest = new SpaceSearchRequest().setUserId(userService.userGetCurrentLonginUserId());
        if (spaceType == SpaceTypeEnums.SELF) { // 如果是查询私有空间
            log.debug("查询当前登录用户的私有空间");
            spaceSearchRequest.setType(spaceType.getCode());
        } else if (spaceType == SpaceTypeEnums.COLLABORATIVE) { // 如果是查询协作空间
            log.debug("查询当前登录用户的协作空间");
            spaceSearchRequest.setType(spaceType.getCode());
        } else { // 如果是其他类型
            ThrowUtils.throwIf(true, CodeBindMessageEnums.PARAMS_ERROR, "不支持的空间类型, 只能获取私有空间和共享空间");
        }

        // 查询对应的空间
        List<Space> spaceList = this.spaceSearch(spaceSearchRequest).getRecords();
        if (spaceList.isEmpty()) {
            log.debug("不存在对应的专属空间");
            return null;
        }
        Space space = spaceList.get(0);
        log.debug("查询到的专属空间为 {}", space);
        return space;
    }

    @Override
    @LogParams
    public Space spaceSetCurrentLoginUserSpace(SpaceTypeEnums spaceType, SpaceLevelEnums spaceLevel, String spaceName) {
        // 检查参数
        log.debug("用户需要查看 {}", spaceType);
        ThrowUtils.throwIf(spaceType == null, CodeBindMessageEnums.PARAMS_ERROR, "添加空间的类型不能为空");
        ThrowUtils.throwIf(this.spaceGetCurrentLoginUserSpace(spaceType) != null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "一个用户不允许拥有多个" + spaceType.getDescription());

        // 先创建请求实例
        Long userId = userService.userGetCurrentLonginUserId();
        var spaceAddRequest = new SpaceAddRequest()
                .setType(spaceType.getCode())
                .setLevel(spaceLevel.getCode())
                .setUserId(userId)
                .setName(spaceName);

        // 添加新的专属空间
        Space space = this.spaceAdd(spaceAddRequest);

        // 特殊情况下协作空间需要添加当前登录用户为管理员
        if (SpaceTypeEnums.getEnums(spaceType.getCode()) == SpaceTypeEnums.COLLABORATIVE) {
            spaceUserService.spaceUserAdd(
                    new SpaceUserAddRequest()
                            .setSpaceId(space.getId())
                            .setUserId(userId)
                            .setSpaceRole(SpaceUserRoleEnums.MANGER_ROLE.getCode())
            );
        }

        // 添加新的空间
        return space;
    }

    /// 私有方法 ///

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<Space> getQueryWrapper(SpaceSearchRequest spaceSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(spaceSearchRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空");

        // 取得需要查询的参数
        Long id = spaceSearchRequest.getId();
        String name = spaceSearchRequest.getName();
        Integer level = spaceSearchRequest.getLevel();
        Integer type = spaceSearchRequest.getType();
        Long userId = spaceSearchRequest.getUserId();
        String sortField = spaceSearchRequest.getSortField();
        String sortOrder = spaceSearchRequest.getSortOrder();

        // 获取包装器进行返回
        LambdaQueryWrapper<Space> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(id != null, Space::getId, id)
                .like(StringUtils.isNotBlank(name), Space::getName, name)
                .eq(level != null, Space::getLevel, level)
                .eq(type != null, Space::getType, type)
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
        SpaceLevelEnums spaceLevelEnums = SpaceLevelEnums.getEnums(space.getLevel());
        ThrowUtils.throwIf(spaceLevelEnums == null, CodeBindMessageEnums.PARAMS_ERROR, "空间等级非法, 必须设置一个合法的等级, 否则无法设置额度");
        space.setMaxSize(spaceLevelEnums.getMaxSize());
        space.setMaxCount(spaceLevelEnums.getMaxCount());
        return space;
    }

    /**
     * 检查 name
     */
    private void checkName(String name) {
        ThrowUtils.throwIf(StrUtil.isNotBlank(name) && name.length() > SpaceConstant.MAX_NAME_LENGTH, CodeBindMessageEnums.PARAMS_ERROR, "名字不得大于" + SpaceConstant.MAX_NAME_LENGTH + "位字符");
    }

    /**
     * 检查 level
     */
    private void checkLevel(Integer level) {
    }

}
