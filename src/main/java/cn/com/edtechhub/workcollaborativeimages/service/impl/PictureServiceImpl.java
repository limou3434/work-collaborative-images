package cn.com.edtechhub.workcollaborativeimages.service.impl;

import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.manager.CosManager;
import cn.com.edtechhub.workcollaborativeimages.mapper.PictureMapper;
import cn.com.edtechhub.workcollaborativeimages.model.dto.UploadPictureResult;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Limou
 * @description 针对表【picture(图片表)】的数据库操作Service实现
 * @createDate 2025-05-04 22:19:43
 */
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    /**
     * 注入 COS 管理器依赖
     */
    @Resource
    CosManager cosManager;

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

            boolean result = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "图片保存到数据库中失败"));

            Picture newPicture = this.getById(picture.getId());
            log.debug("检查新增入库后的图片 {}", newPicture);
            return newPicture;
        }
    }

    public List<Picture> pictureSearch(PictureSearchRequest pictureSearchRequest) {
        // 检查参数
        ThrowUtils.throwIf(pictureSearchRequest == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "图片查询请求不能为空"));

        // 如果用户传递了 id 选项, 则必然是查询一条记录, 为了提高效率直接查询一条数据
        Long pictureId = pictureSearchRequest.getId();
        if (pictureId != null) {
            log.debug("本次查询只需要查询一条记录, 使用 id 字段来提高效率");
            Picture picture = this.getById(pictureId);
            return new ArrayList<>() {{
                add(picture);
            }};
        }

        // 获取查询对象
        LambdaQueryWrapper<Picture> queryWrapper = this.getQueryWrapper(pictureSearchRequest); // 构造查询条件

        // 获取分页对象
        Page<Picture> page = new Page<>(pictureSearchRequest.getPageCurrent(), pictureSearchRequest.getPageSize()); // 这里还指定了页码和条数

        // 查询用户分页后直接取得内部的列表进行返回
        Page<Picture> picturePage = this.page(page, queryWrapper); // 调用 MyBatis-Plus 的分页查询方法
        return picturePage.getRecords(); // 返回分页结果
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
        String sortField = pictureSearchRequest.getSortField();
        String sortOrder = pictureSearchRequest.getSortOrder();

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
                .eq(userId != null, Picture::getPicSize, userId)
                .orderBy(
                        StringUtils.isNotBlank(sortField) && !StringUtils.containsAny(sortField, "=", "(", ")", " "), // 不能包含 =、(、) 或空格等特殊字符, 避免潜在的 SQL 注入或不合法的排序规则
                        sortOrder.equals("ascend"), // 这里结果为 true 代表 ASC 升序, false 代表 DESC 降序
                        Picture::getId // 默认按照标识排序
                )
        ;
        return lambdaQueryWrapper;
    }

}




