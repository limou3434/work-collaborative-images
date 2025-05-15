package cn.com.edtechhub.workcollaborativeimages.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 文件常量
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public interface PictureConstant {

    /**
     * 定义 1 KB(1024 byte) 的具体大小
     */
    Long ONE_K = 1024L;

    /**
     * 定义 1 MB(1024 * 1024 byte) 的具体大小
     */
    Long ONE_M = 1024 * 1024L;

    /**
     * 设置允许上传的图片类型
     */
    List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp", "svg", "ico");


}
