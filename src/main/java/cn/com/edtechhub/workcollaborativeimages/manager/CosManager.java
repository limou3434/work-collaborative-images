package cn.com.edtechhub.workcollaborativeimages.manager;

import cn.com.edtechhub.workcollaborativeimages.config.CosClientConfig;
import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import cn.com.edtechhub.workcollaborativeimages.constant.ServerConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import cn.com.edtechhub.workcollaborativeimages.model.dto.UploadPictureResult;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import cn.hutool.http.HttpResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * COS 管理器
 * 主要存放一些快速对 COS 做操作的方法, 注意需要授权数据万象 https://console.cloud.tencent.com/ci/bucket?path=%2Ftest%2F&bucket=wci-1318277926&region=ap-guangzhou
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Slf4j
public class CosManager {

    /**
     * 注入客户端监听配置依赖
     */
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 注入客户端依赖
     */
    @Resource
    private COSClient cosClient;

    /**
     * 注入服务常量依赖
     */
    @Resource
    ServerConstant serverConstant;

    /**
     * 校验图片对象资源(本地校验)
     */
    public void validPicture(MultipartFile multipartFile) {
        // 检查参数
        ThrowUtils.throwIf(multipartFile == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "文件不能为空"));

        // 检查文件限制
        long fileSize = multipartFile.getSize(); // 获取文件大小
        ThrowUtils.throwIf(fileSize > 2 * PictureConstant.ONE_M, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "文件大小不能超过 2M"));

        // 检查文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!PictureConstant.ALLOW_FORMAT_LIST.contains(fileSuffix), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "该文件的类型不允许上传"));
    }

    /**
     * 校验图片对象资源(远程校验)
     */
    private void validPicture(String fileUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "文件地址不能为空"));

        // 验证 URL 格式
        try {
            new URL(fileUrl); // 验证是否是合法的 URL
        } catch (MalformedURLException e) {
            ThrowUtils.throwIf(true, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "文件地址格式不正确"));
        }

        // 校验 URL 协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "仅支持 HTTP 或 HTTPS 协议的文件地址"));

        // 发送 HEAD 请求以验证文件是否存在
        HttpResponse response = null;
        try {
            response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
            // 未正常返回，无需执行其他判断
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 校验文件类型
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp"); // 允许的图片类型
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()), new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "文件类型错误"));
            }
            // 校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    ThrowUtils.throwIf(contentLength > PictureConstant.ONE_M * 2, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "文件大小不能超过 2M"));
                } catch (NumberFormatException e) {
                    ThrowUtils.throwIf(true, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "文件大小格式化错误"));
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


    /**
     * 推送图片对象资源
     */
    public PutObjectResult putPicture(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file); // 构造上传对象资源请求

        // 上传的同时要求 COS 对图片进行处理, 注意需要开通数据万象并且绑定存储桶
        PicOperations picOperations = new PicOperations(); // 该对象通常用于设置上传对象时的图片处理操作
        picOperations.setIsPicInfo(1); // 表示在处理图片时返回图片元数据信息
        putObjectRequest.setPicOperations(picOperations); // 把这个处理图片用的对象也添加到请求中

        return cosClient.putObject(putObjectRequest); // 提交请求
    }

    /**
     * 上传图片对象资源(本地上传)
     */
    public UploadPictureResult uploadPicture(String uploadPathPrefix, MultipartFile multipartFile) {
        this.validPicture(multipartFile);

        // 执行具体的图片上传逻辑
        String uuid = RandomUtil.randomString(16); // 生成一个16位随机字符串作为唯一标识防止上传文件名重复, 不过这也意味着用户如果重复上传一张图片想要达到修改的目的是无法直接在 cos 中实现的
        String originFilename = multipartFile.getOriginalFilename(); // 获取原始上传文件的文件名
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFilename)); // 构造上传后的新文件名, 2025-05-08_1111111111111111.code.png
        String uploadPath = String.format("%s/%s/%s", serverConstant.getProjectName(), uploadPathPrefix, uploadFilename); // 构造最终图片资源在 COS 对象存储服务上的完整路径, work-collaborative-images/public/38/2025-05-08_1111111111111111.code.png
        log.debug("本次上传的图片预计完整路径为 {}", uploadPath);

        // 创建临时文件并且获取图片的元数据(使用临时文件的原因是 COS 接口只能使用 File 类型上传文件, 不过其实这可以被优化)
        File file = null;
        UploadPictureResult uploadPictureResult = new UploadPictureResult(); // 封装图片元数据的结果
        try {
            file = File.createTempFile(uploadPath, null); // 创建临时文件
            multipartFile.transferTo(file); // 然后把文件内容写入到临时文件中避免用户直接上传图片
            PutObjectResult putObjectResult = this.putPicture(uploadPath, file); // 开始上传图片
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo(); // 从上传结果中获取图片的元信息, 这依赖于之前设置的 IsPicInfo=1

            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
            uploadPictureResult.setPicScale(NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue()); // 将浮点数数四舍五入后保留两位小数, 然后把高精度的类型转化为低精度类型
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        } catch (Exception e) {
            log.debug("图片上传到对象存储失败 {}", e.getMessage());
            ThrowUtils.throwIf(true, new BusinessException(CodeBindMessageEnums.SYSTEM_ERROR, "上传失败"));
        } finally {
            this.deleteTempFile(file); // 无论是哪一种结果最终都会把临时文件删除
        }
        log.debug("本次上传的图片最终网络地址为 {}", uploadPictureResult.getUrl());
        return uploadPictureResult;
    }

    /**
     * 上传图片对象资源(远端上传)
     */
    public UploadPictureResult uploadPicture(String uploadPathPrefix, String fileUrl) {
        this.validPicture(fileUrl);

        // 执行具体的图片上传逻辑
        String uuid = RandomUtil.randomString(16);
        // String originFilename = multipartFile.getOriginalFilename();
        String originFilename = FileUtil.mainName(fileUrl);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("%s/%s/%s", serverConstant.getProjectName(), uploadPathPrefix, uploadFilename);
        log.debug("本次上传的图片预计完整路径为 {}", uploadPath);

        // 创建临时文件并且获取图片的元数据(使用临时文件的原因是 COS 接口只能使用 File 类型上传文件, 不过其实这可以被优化)
        File file = null;
        UploadPictureResult uploadPictureResult = new UploadPictureResult(); // 封装图片元数据的结果
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            HttpUtil.downloadFile(fileUrl, file); // multipartFile.transferTo(file);
            PutObjectResult putObjectResult = this.putPicture(uploadPath, file); // 开始上传图片
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo(); // 从上传结果中获取图片的元信息, 这依赖于之前设置的 IsPicInfo=1

            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
            uploadPictureResult.setPicScale(NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue()); // 将浮点数数四舍五入后保留两位小数, 然后把高精度的类型转化为低精度类型
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        } catch (Exception e) {
            log.debug("图片上传到对象存储失败 {}", e.getMessage());
            ThrowUtils.throwIf(true, new BusinessException(CodeBindMessageEnums.SYSTEM_ERROR, "上传失败"));
        } finally {
            this.deleteTempFile(file);
        }
        log.debug("本次上传的图片最终网络地址为 {}", uploadPictureResult.getUrl());
        return uploadPictureResult;
    }

    /**
     * 删除临时对象资源
     */
    public void deleteTempFile(File file) {
        ThrowUtils.throwIf(file == null, new BusinessException(CodeBindMessageEnums.PARAMS_ERROR, "错误使用删除临时对象资源的方法, 主要是因为要删除的文件为空"));

        // 删除临时文件
        boolean deleteResult = file.delete();
        ThrowUtils.throwIf(!deleteResult, new BusinessException(CodeBindMessageEnums.SYSTEM_ERROR, "无法删除临时文件"));
    }

}
