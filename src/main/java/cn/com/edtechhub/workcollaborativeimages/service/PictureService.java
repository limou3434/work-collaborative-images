package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Limou
 * @description 针对表【picture(图片表)】的数据库操作Service
 * @createDate 2025-05-04 22:19:43
 */
public interface PictureService extends IService<Picture> {

    /**
     * 图片添加服务
     */
    Picture pictureAdd(PictureAddRequest pictureAddRequest);

    /**
     * 图片删除服务
     */
    Boolean pictureDelete(PictureDeleteRequest pictureDeleteRequest);

    /**
     * 图片更新服务
     */
    Picture pictureUpdate(PictureUpdateRequest pictureUpdateRequest);

    /**
     * 图片查询服务
     */
    Page<Picture> pictureSearch(PictureSearchRequest pictureSearchRequest);

    /**
     * 图片审核服务
     */
    Boolean pictureReview(Long id, Integer reviewStatus, String reviewMessage);

    /**
     * 图片转链服务
     */
    Picture pictureUpload(Long pictureId, String pictureCategory, String pictureName, String pictureIntroduction, String pictureTags, MultipartFile multipartFile);

    /**
     * 图片获取种类服务
     */
    List<String> pictureGetCategorys();

}
