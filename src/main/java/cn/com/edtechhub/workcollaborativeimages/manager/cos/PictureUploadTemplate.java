package cn.com.edtechhub.workcollaborativeimages.manager.cos;

import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import cn.com.edtechhub.workcollaborativeimages.constant.ServerConstant;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图片上传模板(设计模式 - 模板方法模式)
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Slf4j
public abstract class PictureUploadTemplate {

    /**
     * 注入服务常量依赖
     */
    @Resource
    ServerConstant serverConstant;

    /**
     * 注入客户端监听配置依赖
     */
    @Resource
    private CosConfig cosConfig;

    /**
     * 上传图片资源
     *
     * @param uploadPathPrefix 上传路径前缀(推荐使用 "图片种类 + 用户标识" 来作为前缀)
     * @param inputSource      输入资源
     * @return 上传图片资源的结果
     */
    public UploadPictureResult uploadPicture(String uploadPathPrefix, Object inputSource) {
        // 校验输入资源
        this.validPicture(inputSource);

        // 设置图片上传路径
        String uuid = RandomUtil.randomString(16); // 生成一个 16 位随机字符串作为唯一标识防止上传文件名重复, 不过这也意味着用户如果重复上传一张图片想要达到修改的目的是无法直接在 cos 中实现的
        String originFilename = this.getOriginFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFilename)); // 构造上传后的新文件名, 2025-05-08_1111111111111111.code.png
        String uploadPath = String.format("%s/%s/%s", serverConstant.getProjectName(), uploadPathPrefix, uploadFilename); // 构造最终图片资源在 COS 对象存储服务上的完整路径, work-collaborative-images/public/38/2025-05-08_1111111111111111.code.png
        log.debug("本次上传的图片资源预计完整的路径为 {}", uploadPath);

        // 创建临时文件并且获取图片的元数据
        File file = null; // TODO: 使用临时文件的原因是 COS 接口只能使用 File 类型上传文件, 不过其实这可以被优化
        UploadPictureResult uploadPictureResult = new UploadPictureResult(); // 封装图片元数据的结果
        try {
            // 获取原始图元数据
            file = File.createTempFile(uploadPath, null); // 创建临时文件
            this.processFile(inputSource, file);
            PutObjectResult putObjectResult = this.putPicture(uploadPath, file); // 开始上传图片
            ThrowUtils.throwIf(putObjectResult == null, CodeBindMessageEnums.SYSTEM_ERROR, "上传后的响应结果为空");
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo(); // 从上传结果中获取图片的元信息, 这依赖于之前规则中设置的 IsPicInfo=1

            // 获取压缩图元数据
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            CIObject compressedCiObject = null;
            CIObject thumbnailCiObject = null;
            boolean res = CollUtil.isNotEmpty(objectList);
            if (res) {
                compressedCiObject = objectList.get(0);
                thumbnailCiObject = objectList.size() > 1 ? objectList.get(1) : compressedCiObject; // 如果没有缩略图, 缩略图就等于压缩图(主要是为了配置前面避免小图片文件压缩后更大的问题)
            }

            // 如果压缩成功就把压缩图返回, 否则就只返回原始图
            int picWidth = res ? compressedCiObject.getWidth() : imageInfo.getWidth();
            int picHeight = res ? compressedCiObject.getHeight() : imageInfo.getHeight();
            String ave = imageInfo.getAve();
            String url = cosConfig.getHost() + "/" + compressedCiObject.getKey();
            String thumbnailUrl = cosConfig.getHost() + "/" + thumbnailCiObject.getKey();
            String originalUrl = cosConfig.getHost() + "/" + uploadPath;
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicFormat(res ? compressedCiObject.getFormat() : imageInfo.getFormat());
            uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
            uploadPictureResult.setPicScale(NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue()); // 将浮点数数四舍五入后保留两位小数, 然后把高精度的类型转化为低精度类型
            uploadPictureResult.setPicSize(res ? compressedCiObject.getSize().longValue() : FileUtil.size(file));
            uploadPictureResult.setPicColor(ave);
            uploadPictureResult.setUrl(res ? url : originalUrl);
            uploadPictureResult.setThumbnailUrl(res ? thumbnailUrl : null);
            uploadPictureResult.setOriginalUrl(originalUrl); // 把原始图也保留下来
        } catch (Exception e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.SYSTEM_ERROR, "图片上传到对象存储失败发送未知的异常 " + e.getMessage());
        } finally {
            this.deleteTempFile(file); // 无论是哪一种结果最终都会把临时文件删除
        }
        log.debug("本次上传的图片最终返回结果为 {}", uploadPictureResult);
        return uploadPictureResult;
    }

    /**
     * 校验输入资源
     *
     * @param inputSource 输入资源
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取原始文件名
     *
     * @return 原始文件名
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 处理输入源并生成本地临时文件
     *
     * @param inputSource 输入源
     * @param tempFile    临时文件
     */
    protected abstract void processFile(Object inputSource, File tempFile) throws Exception;

    /**
     * 推送图片对象资源
     *
     * @param key 图片对象资源在 COS 存储桶中的完整路径
     * @param file 需要上传的图片文件
     * @return 推送结果
     */
    private PutObjectResult putPicture(String key, File file) {
        // 创建原生 cos 客户端
        COSCredentials cred = new BasicCOSCredentials(this.cosConfig.getSecretId(), this.cosConfig.getSecretKey()); // 初始化用户身份信息(secretId, secretKey)
        ClientConfig clientConfig = new ClientConfig(new Region(this.cosConfig.getRegion())); // 设置 bucket 的地域, clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法
        clientConfig.setHttpProtocol(HttpProtocol.https); // 从 5.6.54 版本开始，默认使用了 https
        var cosClient = new COSClient(cred, clientConfig); // 生成 cos 客户端

        // 构造上传对象资源请求
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, file);

        // 上传的同时要求 COS 对图片进行处理, 同时遵循响应的规则(注意需要开通数据万象并且绑定存储桶才能操作 COS 图片)
        PicOperations picOperations = new PicOperations(); // 该对象通常用于设置上传对象时的图片处理操作
        picOperations.setIsPicInfo(1); // 表示在处理图片时返回图片元数据信息
        picOperations.setRules(getOperationsRule(key, file)); // 添加规则

        // 把这个处理图片用的对象也添加到请求中
        putObjectRequest.setPicOperations(picOperations);

        // 提交请求
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 获取操作图片的规则列表
     *
     * @param key 图片对象资源在 COS 存储桶中的完整路径
     * @param file 需要上传的图片文件
     * @return 操作图片的规则列表
     */
    private List<PicOperations.Rule> getOperationsRule(String key, File file) {
        // 创建操作规则对象
        List<PicOperations.Rule> rules = new ArrayList<>();

        // 设置压缩图的规则
        PicOperations.Rule compressRule = new PicOperations.Rule();
        String webpKey = FileUtil.mainName(key) + ".webp";
        compressRule.setRule("imageMogr2/format/webp");
        compressRule.setBucket(cosConfig.getBucket());
        compressRule.setFileId(webpKey);
        rules.add(compressRule);

        // 设置缩略图的规则
        if (file.length() > 2 * PictureConstant.ONE_K) { // 只有在图片超过一定大小才做缩略图, 否则小图片反而在缩略后体积变大了
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosConfig.getBucket());
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128));
            rules.add(thumbnailRule);
        }

        // 返回规则
        log.debug("查看本次上传图片时的操作规则 {}", rules);
        return rules;
    }

    /**
     * 删除临时对象资源
     *
     * @param tempFile 临时文件
     */
    private void deleteTempFile(File tempFile) {
        // 检查参数
        ThrowUtils.throwIf(tempFile == null, CodeBindMessageEnums.PARAMS_ERROR, "错误使用删除临时对象资源的方法, 主要是因为要删除的文件为空");

        // 删除临时文件
        boolean deleteResult = tempFile.delete();
        ThrowUtils.throwIf(!deleteResult, CodeBindMessageEnums.SYSTEM_ERROR, "无法删除临时文件");
    }

}
