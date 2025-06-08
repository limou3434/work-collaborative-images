package cn.com.edtechhub.workcollaborativeimages.manager.cos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * COS 管理器
 * 主要存放一些快速对 COS 做操作的方法, 注意需要授权数据万象 <a href="https://console.cloud.tencent.com/ci/bucket?path=%2Ftest%2F&bucket=wci-1318277926&region=ap-guangzhou">数据万象文档</a>
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Slf4j
public class CosManager {

    /**
     * 注入本地图片文件上传实现类依赖
     */
    @Resource
    FilePictureUpload filePictureUpload;

    /**
     * 注入远端图片链接上传实现类依赖
     */
    @Resource
    UrlPictureUpload urlPictureUpload;

    /**
     * 上传图片
     *
     * @param uploadPathPrefix 上传前缀(推荐类型 + 用户标识)
     * @param inputSource 上传图源
     * @return 图片上传成功后的返回结果
     */
    public UploadPictureResult uploadPicture(String uploadPathPrefix, Object inputSource) {
        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        return pictureUploadTemplate.uploadPicture(uploadPathPrefix, inputSource);
    }

}
