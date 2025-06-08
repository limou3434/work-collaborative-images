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

    /**
     * 图片名字最大长度
     */
    Integer MAX_NAME_LENGTH = 30;

    /**
     * 默认图片名字
     */
    String DEFAULT_NAME = "默认图片";

    /**
     * 默认简介
     */
    String DEFAULT_INTRODUCTION = "默认简介";

    /**
     * 默认分类
     */
    String DEFAULT_CATEGORT = "默认分类";

    /**
     * 默认图片状态
     */
    Integer DEFAULT_REVIEW_STATUS = 0;

    /**
     * 审核信息
     */
    String DEFAULT_REVIEW_MESSAGE = "待管理员审核";

}
