package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
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
     * 图片转链服务
     */
    Picture pictureUpload(Long pictureId, String pictureCategory, String pictureIntroduction, List<String> pictureTags, MultipartFile multipartFile);

    /**
     * 图片查询服务
     */
    List<Picture> pictureSearch(PictureSearchRequest pictureSearchRequest);

}
