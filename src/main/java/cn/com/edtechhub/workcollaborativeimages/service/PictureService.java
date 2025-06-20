package cn.com.edtechhub.workcollaborativeimages.service;

import cn.com.edtechhub.workcollaborativeimages.enums.PictureReviewStatusEnum;
import cn.com.edtechhub.workcollaborativeimages.manager.ai.CreateOutPaintingTaskRequest;
import cn.com.edtechhub.workcollaborativeimages.manager.ai.CreateOutPaintingTaskResponse;
import cn.com.edtechhub.workcollaborativeimages.manager.ai.GetOutPaintingTaskResponse;
import cn.com.edtechhub.workcollaborativeimages.manager.search.ImageSearchResult;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureAddRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureDeleteRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureUpdateRequest;
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

    /// 基础服务(添加/更新请求具有转化方法, 删除只依赖 id 进行删除, 查询依赖构造查询对象) ///

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

    /// 其他服务 ///

    /**
     * 根据标识查询单个图片服务
     */
    Picture pictureSearchById(Long id);

    /**
     * 审核图片服务
     */
    Boolean pictureReview(Long id, Integer reviewStatus, String reviewMessage);

    /**
     * 图片转化链接服务
     */
    Picture pictureUpload(Long pictureId, Long spaceId, String pictureCategory, String pictureName, String pictureIntroduction, String pictureTags, String pictureFileUrl, MultipartFile multipartFile);

    /**
     * 图片断开链接服务
     */
    Boolean pictureUnLink(Long pictureId);

    /**
     * 批量爬取图片服务
     */
    Integer pictureBatch(String searchText, Integer searchCount, String namePrefix, String category, String introduction, String tags);

    /**
     * 获取图片所属的空间服务
     */
    Space pictureGetSpace(Picture picture);

    /**
     * 获取图片的审核状态服务
     */
    PictureReviewStatusEnum pictureGetReviewStatus(Picture picture);

    /**
     * 获取图片种类服务
     */
    List<String> pictureGetCategorys();

    /**
     * 获取相似图片服务
     */
    List<ImageSearchResult> pictureGetSimilarPictureList(Long pictureId);

    /**
     * 获取同色图片服务
     */
    List<Picture> pictureGetSameColorPictureList(Long pictureId);

    /**
     * 创建图片绘制任务服务
     */
    CreateOutPaintingTaskResponse pictureCreateOutPaintingTask(Long pictureId, CreateOutPaintingTaskRequest.Parameters parameters);


    /**
     * 查看图片绘制任务服务
     */
    GetOutPaintingTaskResponse pictureGetOutPaintingTask(String taskId);

}