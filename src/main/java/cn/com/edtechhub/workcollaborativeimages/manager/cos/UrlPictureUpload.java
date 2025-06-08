package cn.com.edtechhub.workcollaborativeimages.manager.cos;

import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 实现上传模板设计模式的远端图片链接上传类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class UrlPictureUpload extends PictureUploadTemplate {

    @Override
    protected void validPicture(Object inputSource) {
        // 类型转换
        String fileUrl = (String) inputSource; // TODO: 可以做一些安全措施

        // 检查参数
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), CodeBindMessageEnums.PARAMS_ERROR, "文件地址不能为空");

        // 验证 URL 格式
        try {
            new URL(fileUrl); // 验证是否是合法的 URL
        } catch (MalformedURLException e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.PARAMS_ERROR, "文件地址格式不正确");
        }

        // 校验 URL 协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")), CodeBindMessageEnums.PARAMS_ERROR, "仅支持 HTTP 或 HTTPS 协议的文件地址");

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
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()), CodeBindMessageEnums.PARAMS_ERROR, "文件类型错误");
            }
            // 校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    ThrowUtils.throwIf(contentLength > PictureConstant.ONE_M * 2, CodeBindMessageEnums.PARAMS_ERROR, "文件大小不能超过 2M");
                } catch (NumberFormatException e) {
                    ThrowUtils.throwIf(true, CodeBindMessageEnums.PARAMS_ERROR, "文件大小格式化错误");
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        // 类型转换
        String fileUrl = (String) inputSource; // TODO: 可以做一些安全措施

        // 返回文件名
        return FileUtil.mainName(fileUrl);
    }

    @Override
    protected void processFile(Object inputSource, File tempFile) throws Exception {
        // 类型转化
        String fileUrl = (String) inputSource; // TODO: 可以做一些安全措施

        // 然后把文件内容写入到临时文件中
        HttpUtil.downloadFile(fileUrl, tempFile); // multipartFile.transferTo(file);
    }

}
