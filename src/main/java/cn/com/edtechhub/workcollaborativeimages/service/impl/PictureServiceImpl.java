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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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

    public Picture pictureUpload(Long pictureId, String pictureCategory, String pictureIntroduction, List<String> pictureTags, MultipartFile multipartFile) {
        // 如果是更新图片
        if (pictureId != null) {
            boolean exists = this
                    .lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!exists, new BusinessException(CodeBindMessageEnums.NOT_FOUND_ERROR, "指定 id 所对应的图片不存在所以无法更新"));

            // 如果还没有携带新的图片但是数据库中图片存在, 那么就只更新图片的元数据
            if (multipartFile == null) {
                Picture picture = this.getById(pictureId);
                picture.setCategory(pictureCategory);
                picture.setIntroduction(pictureIntroduction);
                picture.setTags(pictureTags == null ? null : pictureTags.toString());
                boolean result = this.updateById(picture);
                ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "图片保存到数据库中失败"));
                Picture newPicture = this.getById(pictureId);
                log.debug("检查更新入库后的图片 {}", newPicture);
                return newPicture;
            }
        }

        // 如果是新增图片
        Long userId = Long.valueOf(StpUtil.getLoginId().toString());
        String uploadPathPrefix = String.format("public/%s", userId); // 构造和用户相关的图片父目录
        UploadPictureResult uploadPictureResult = cosManager.uploadPicture(uploadPathPrefix, multipartFile);
        Picture picture = new Picture(); // 构造要入库的图片信息
        picture.setCategory(pictureCategory);
        picture.setIntroduction(pictureIntroduction);
        picture.setTags(pictureTags == null ? null : pictureTags.toString());
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(userId);
        if (pictureId != null) {
            picture.setId(pictureId);
            // TODO: 这里需要修改更新时间
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, new BusinessException(CodeBindMessageEnums.OPERATION_ERROR, "图片保存到数据库中失败"));
        Picture newPicture = this.getById(picture.getId());
        log.debug("检查新增入库后的图片 {}", newPicture);
        return newPicture;
    }

    public Boolean pictureDownload() {
        return null;
    }

    public List<Picture> pictureSearch(PictureSearchRequest pictureSearchRequest) {
        return null;
    }

}




