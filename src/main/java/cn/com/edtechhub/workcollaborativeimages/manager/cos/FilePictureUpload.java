package cn.com.edtechhub.workcollaborativeimages.manager.cos;

import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 实现上传模板设计模式的本地图片文件上传类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class FilePictureUpload extends PictureUploadTemplate {

    @Override
    protected void validPicture(Object inputSource) {
        // 类型转化
        MultipartFile multipartFile = (MultipartFile) inputSource; // TODO: 可以做一些安全措施

        // 检查参数
        ThrowUtils.throwIf(multipartFile == null, CodeBindMessageEnums.PARAMS_ERROR, "文件不能为空");

        // 检查文件限制
        long fileSize = multipartFile.getSize(); // 获取文件大小
        ThrowUtils.throwIf(fileSize > 2 * PictureConstant.ONE_M, CodeBindMessageEnums.PARAMS_ERROR, "文件大小不能超过 2M");

        // 检查文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!PictureConstant.ALLOW_FORMAT_LIST.contains(fileSuffix), CodeBindMessageEnums.PARAMS_ERROR, "该文件的类型不允许上传");
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        // 类型转化
        MultipartFile multipartFile = (MultipartFile) inputSource; // TODO: 可以做一些安全措施

        // 返回文件名
        return multipartFile.getOriginalFilename(); // 获取原始上传文件的文件名
    }

    @Override
    protected void processFile(Object inputSource, File tempFile) throws Exception {
        // 类型转化
        MultipartFile multipartFile = (MultipartFile) inputSource; // TODO: 可以做一些安全措施

        // 然后把文件内容写入到临时文件中
        multipartFile.transferTo(tempFile);
    }

}
