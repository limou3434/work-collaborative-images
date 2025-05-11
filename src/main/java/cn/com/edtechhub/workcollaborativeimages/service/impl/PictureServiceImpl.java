package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.PictureReviewStatusEnum;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.manager.CosManager;
import cn.com.edtechhub.workcollaborativeimages.mapper.PictureMapper;
import cn.com.edtechhub.workcollaborativeimages.model.dto.UploadPictureResult;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Limou
 * @description 针对表【picture(图片表)】的数据库操作Service实现
 * @createDate 2025-05-04 22:19:43
 */
@SuppressWarnings("ALL")
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    /**
     * 注入 COS 管理器依赖
     */
    @Resource
    CosManager cosManager;

    public Picture pictureAdd(PictureAddRequest pictureAddRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureAddRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "图片添加请求体为空"));

        // 创建用户实例的同时加密密码
        var picture = new Picture();
        BeanUtils.copyProperties(pictureAddRequest, picture);

        // 保存实例的同时利用唯一键约束避免并发问题
        try {
            this.save(picture);
        } catch (DuplicateKeyException e) { // 无需加锁, 只需要设置唯一键就足够因对并发场景
            ThrowUtils.throwIf(true, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "已经存在该用户, 或者曾经被删除"));
        }
        return picture;
    }

    public Boolean pictureDelete(PictureDeleteRequest pictureDeleteRequest) {
        ThrowUtils.throwIf(pictureDeleteRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "图片删除请求体不能为空"));
        Long id = pictureDeleteRequest.getId();
        ThrowUtils.throwIf(id <= 0, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "请求中的 id 不合法"));

        // 操作数据库
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "删除图片失败, 也许该图片不存在或者已经被删除"));
        return true;
    }

    public Picture pictureUpdate(PictureUpdateRequest pictureUpdateRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureUpdateRequest.getId() == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "用户 id 不能为空"));
        ThrowUtils.throwIf(pictureUpdateRequest.getId() <= 0, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "用户 id 必须是正整数"));

        // 更新用户并且需要考虑密码的问题
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        this.updateById(picture);

        // 更新用户后最好把用户的信息也返回, 这样方便前端做实时的数据更新
        Long id = picture.getId();
        return this.getById(id);
    }

    public Page<Picture> pictureSearch(PictureSearchRequest pictureSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "图片查询请求不能为空"));

        // 如果用户传递了 id 选项, 则必然是查询一条记录, 为了提高效率直接查询一条数据
        Long pictureId = pictureSearchRequest.getId();
        if (pictureId != null) {
            log.debug("本次查询只需要查询一条记录, 使用 id 字段来提高效率");
            Picture picture = this.getById(pictureId);
            log.debug("单条查询的图片记录为 {}", picture);
            Page<Picture> resultPage = new Page<>();
            if (picture != null && picture.getReviewStatus() == PictureReviewStatusEnum.PASS.getValue()) {
                resultPage.setRecords(Collections.singletonList(picture));
                resultPage.setTotal(1);
                resultPage.setSize(1);
                resultPage.setCurrent(1);
            } else {
                resultPage.setRecords(Collections.emptyList());
                resultPage.setTotal(0);
                resultPage.setSize(1);
                resultPage.setCurrent(1);
            }
            return resultPage;
        }

        // 获取查询对象
        LambdaQueryWrapper<Picture> queryWrapper = this.getQueryWrapper(pictureSearchRequest); // 构造查询条件

        // 获取分页对象
        Page<Picture> page = new Page<>(pictureSearchRequest.getPageCurrent(), pictureSearchRequest.getPageSize()); // 这里还指定了页码和条数

        // 查询用户分页后直接取得内部的列表进行返回
        return this.page(page, queryWrapper); // 调用 MyBatis-Plus 的分页查询方法
    }

    public Boolean pictureReview(Long id, Integer reviewStatus, String reviewMessage) {
        // TODO: 简单使用 AI 接口来进行文本审核和图片审核

        // 检查参数
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        ThrowUtils.throwIf(id == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "图片 id 不能为空"));
        ThrowUtils.throwIf(reviewStatusEnum == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "需要指定最终的有效审核状态"));

        // 判断图片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, new BusinessException(CodeBindMessageEnums.NOT_FOUND_ERROR, "id 所对应的图片不存在"));

        // 已是该状态
        ThrowUtils.throwIf(oldPicture.getReviewStatus().equals(reviewStatus), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "该图片已经处于本次请求的指定状态, 请勿重复审核"));

        // 更新审核状态(注意数据库默认设置为待审状态)
        Picture updatePicture = new Picture();
        updatePicture.setId(id);
        updatePicture.setReviewStatus(reviewStatus);
        updatePicture.setReviewMessage(reviewMessage);
        updatePicture.setReviewerId(Long.valueOf(StpUtil.getLoginId().toString()));
        updatePicture.setReviewTime(LocalDateTime.now());
        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.SYSTEM_ERROR, "审核失败, 无法更新"));
        return true;
    }

    public Picture pictureUpload(Long pictureId, String pictureCategory, String pictureName, String pictureIntroduction, String pictureTags, MultipartFile multipartFile) {
        // 如果是更新图片(没有携带 id)
        if (pictureId != null) {
            boolean exists = this
                    .lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!exists, new BusinessException(CodeBindMessageEnums.NOT_FOUND_ERROR, "指定 id 所对应的图片不存在所以无法更新"));

            // 如果还没有携带新的图片但是数据库中图片存在, 那么就只更新图片的元数据
            if (multipartFile == null) {
                Picture picture = this.getById(pictureId);
                picture.setCategory(StringUtils.isNotBlank(pictureCategory) ? pictureCategory : null);
                picture.setName(StringUtils.isNotBlank(pictureName) ? pictureName : null);
                picture.setIntroduction(StringUtils.isNotBlank(pictureIntroduction) ? pictureIntroduction : null);
                picture.setTags(StringUtils.isNotBlank(pictureTags) ? pictureTags : null);
                picture.setUpdateTime(LocalDateTime.now()); // 更新修改时间
                picture.setReviewStatus(0); // 重新设置为待审核状态 TODO: 如果当前用户为管理员状态则无需审核
                boolean result = this.updateById(picture);
                ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "图片保存到数据库中失败"));
                Picture newPicture = this.getById(pictureId);
                log.debug("检查更新入库后的图片 {}", newPicture);
                return newPicture;
            }

            return null;
        }
        // 如果是新增图片(确有携带 id)
        else {
            Long userId = Long.valueOf(StpUtil.getLoginId().toString());
            String uploadPathPrefix = String.format("public/%s", userId); // 构造和用户相关的图片父目录
            UploadPictureResult uploadPictureResult = cosManager.uploadPicture(uploadPathPrefix, multipartFile); // 执行具体的上传任务

            Picture picture = new Picture(); // 构造要入库的图片信息
            picture.setCategory(pictureCategory);
            picture.setName(StringUtils.isNotBlank(pictureName) ? pictureName : uploadPictureResult.getPicName());
            picture.setTags(StringUtils.isNotBlank(pictureTags) ? pictureTags : null);
            picture.setIntroduction(pictureIntroduction);
            picture.setUrl(uploadPictureResult.getUrl());
            picture.setPicSize(uploadPictureResult.getPicSize());
            picture.setPicWidth(uploadPictureResult.getPicWidth());
            picture.setPicHeight(uploadPictureResult.getPicHeight());
            picture.setPicScale(uploadPictureResult.getPicScale());
            picture.setPicFormat(uploadPictureResult.getPicFormat());
            picture.setUserId(userId);
            picture.setReviewStatus(0); // 重新设置为待审核状态 TODO: 如果当前用户为管理员状态则无需审核

            boolean result = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "图片保存到数据库中失败"));

            Picture newPicture = this.getById(picture.getId());
            log.debug("检查新增入库后的图片 {}", newPicture);
            return newPicture;
        }
    }

    public List<String> pictureGetCategorys() {
        return Arrays.asList("动漫", "艺术", "表情", "素材", "海报");
    }

    /**
     * 获取查询封装器的方法
     */
    private LambdaQueryWrapper<Picture> getQueryWrapper(PictureSearchRequest pictureSearchRequest) {
        // 查询请求不能为空
        ThrowUtils.throwIf(pictureSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "查询请求不能为空"));

        // 取得需要查询的参数
        String name = pictureSearchRequest.getName();
        String introduction = pictureSearchRequest.getIntroduction();
        String category = pictureSearchRequest.getCategory();
        String tags = pictureSearchRequest.getTags();
        Long picSize = pictureSearchRequest.getPicSize();
        Integer picWidth = pictureSearchRequest.getPicWidth();
        Integer picHeight = pictureSearchRequest.getPicHeight();
        Double picScale = pictureSearchRequest.getPicScale();
        String picFormat = pictureSearchRequest.getPicFormat();
        Long userId = pictureSearchRequest.getUserId();
        Integer reviewStatus = pictureSearchRequest.getReviewStatus();
        String sortField = pictureSearchRequest.getSortField();
        String sortOrder = pictureSearchRequest.getSortOrder();

        log.debug("用户需要查询的审核状态 {}", reviewStatus);

        // 获取包装器进行返回
        LambdaQueryWrapper<Picture> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(StringUtils.isNotBlank(name), Picture::getName, name)
                .like(StringUtils.isNotBlank(introduction), Picture::getIntroduction, introduction)
                .like(StringUtils.isNotBlank(category), Picture::getCategory, category)
                .like(StringUtils.isNotBlank(tags), Picture::getTags, tags)
                .eq(picSize != null, Picture::getPicSize, picSize)
                .eq(picWidth != null, Picture::getPicWidth, picWidth)
                .eq(picHeight != null, Picture::getPicHeight, picHeight)
                .eq(picScale != null, Picture::getPicScale, picScale)
                .eq(picFormat != null, Picture::getPicFormat, picFormat)
                .eq(userId != null, Picture::getUserId, userId)
                .eq(reviewStatus != null, Picture::getReviewStatus, reviewStatus)
                .orderBy(
                        StringUtils.isNotBlank(sortField) && !StringUtils.containsAny(sortField, "=", "(", ")", " "), // 不能包含 =、(、) 或空格等特殊字符, 避免潜在的 SQL 注入或不合法的排序规则
                        sortOrder.equals("ascend"), // 这里结果为 true 代表 ASC 升序, false 代表 DESC 降序
                        Picture::getCreateTime // 默认按照创建时间排序
                )
        ;
        return lambdaQueryWrapper;
    }

}




