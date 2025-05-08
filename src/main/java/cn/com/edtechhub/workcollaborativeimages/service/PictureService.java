package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
     * 图片添加服务
     */
    Boolean pictureDelete(PictureDeleteRequest pictureDeleteRequest);

    /**
     * 图片更新服务
     */
    Picture pictureUpdate(PictureUpdateRequest pictureUpdateRequest);

    /**
     * 图片查询服务
     */
    List<Picture> pictureSearch(PictureSearchRequest pictureSearchRequest);

    /**
     * 图片上传测试服务
     */
    String pictureTestUpload(String cosFilepath, MultipartFile multipartFile);

    /**
     * 图片下载测试服务
     */
    void pictureTestDownload(String cosFilepath, HttpServletResponse response) throws IOException;

}
