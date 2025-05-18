package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.AdminPictureAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.AdminPictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.AdminPictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.AdminPictureUpdateRequest;
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
    Picture pictureAdd(AdminPictureAddRequest adminPictureAddRequest);

    /**
     * 图片删除服务
     */
    Boolean pictureDelete(AdminPictureDeleteRequest adminPictureDeleteRequest);

    /**
     * 图片更新服务
     */
    Picture pictureUpdate(AdminPictureUpdateRequest adminPictureUpdateRequest);

    /**
     * 图片查询服务
     */
    Page<Picture> pictureSearch(AdminPictureSearchRequest adminPictureSearchRequest);

    /**
     * 图片审核服务
     */
    Boolean pictureReview(Long id, Integer reviewStatus, String reviewMessage);

    /**
     * 图片批量服务
     */
    Integer pictureBatch(String searchText, Integer searchCount, String namePrefix, String category);

    /**
     * 图片转链服务
     */
    Picture pictureUpload(Long userId, Long spaceId, Long pictureId, String pictureCategory, String pictureName, String pictureIntroduction, String pictureTags, String pictureUrl, MultipartFile multipartFile);

    /**
     * 图片获取种类服务
     */
    List<String> pictureGetCategorys();

}
