package cn.com.edtechhub.workcollaborativeimages.manager.ai;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建绘画任务请求类
 * 注意某些字段打上了 Hutool 工具类的 @Alias 注解, 这个注解仅对 Hutool 的 JSON 转换生效, 对 SpringMVC 的 JSON 转换没有任何影响, 我们需要处理一些
 * 并且这里有一个巨坑的地方, 经过测试发现, 如果前端传递参数名 xScale, 是无法赋值给 xScale 字段的
 * 但是传递参数名 xscale 就可以赋值, 这是因为 SpringMVC 对于第二个字母是大写的参数无法映射(和参数类别无关), 参考 https://blog.csdn.net/JokerHH/article/details/88729590
 * 解决方案是给这些字段增加 @JsonProperty 注解
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
public class CreateOutPaintingTaskRequest implements Serializable {

    /**
     * 指定模型
     */
    private String model = "image-out-painting";

    /**
     * 图像信息
     */
    private Input input;

    /**
     * 处理参数
     */
    private Parameters parameters;

    /// 序列化字段 ///
    @Serial
    private static final long serialVersionUID = 1L;

    @Data
    public static class Input implements Serializable {

        /**
         * (必选)图像 URL
         */
        @Alias("image_url")
        private String imageUrl;

        /// 序列化字段 ///
        @Serial
        private static final long serialVersionUID = 1L;

    }

    @Data
    public static class Parameters implements Serializable {

        /**
         * (可选)逆时针旋转角度, 默认值 0, 取值范围 [0, 359]
         */
        private Integer angle = 0;

        /**
         * (可选)输出图像的宽高比, 默认空字符串, 不设置宽高比
         * 可选值：["", "1:1", "3:4", "4:3", "9:16", "16:9"]
         */
        @Alias("output_ratio")
        private String outputRatio = "";

        /**
         * (可选)图像居中, 在水平方向上按比例扩展, 默认值 1.0, 范围 [1.0, 3.0]
         */
        @Alias("x_scale")
        @JsonProperty("xScale")
        private Float xScale = 1.0f;

        /**
         * (可选)图像居中, 在垂直方向上按比例扩展, 默认值 1.0, 范围 [1.0, 3.0]
         */
        @Alias("y_scale")
        @JsonProperty("yScale")
        private Float yScale = 1.0f;

        /**
         * (可选)在图像上方添加像素, 默认值 200
         */
        @Alias("top_offset")
        private Integer topOffset = 200;

        /**
         * (可选)在图像下方添加像素, 默认值 200
         */
        @Alias("bottom_offset")
        private Integer bottomOffset = 200;

        /**
         * (可选)在图像左侧添加像素, 默认值 200
         */
        @Alias("left_offset")
        private Integer leftOffset = 200;

        /**
         * (可选)在图像右侧添加像素, 默认值 200
         */
        @Alias("right_offset")
        private Integer rightOffset = 200;

        /**
         * (可选)开启图像最佳质量模式, 默认值 false, 若为 true, 耗时会成倍增加
         */
        @Alias("best_quality")
        private Boolean bestQuality = false;

        /**
         * (可选)限制模型生成的图像文件大小, 默认值 true
         * - 单边长度 <= 10000：输出图像文件大小限制为 5MB 以下
         * - 单边长度 > 10000：输出图像文件大小限制为 10MB 以下
         */
        @Alias("limit_image_size")
        private Boolean limitImageSize = true;

        /**
         * (可选)添加 "Generated by AI" 水印, 默认值 false
         */
        @Alias("add_watermark")
        private Boolean addWatermark = false;

        /// 序列化字段 ///
        @Serial
        private static final long serialVersionUID = 1L;

    }

}
