package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import cn.com.edtechhub.workcollaborativeimages.model.vo.PictureVO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class AdminPictureAddRequest implements Serializable {

    /**
     * 图片 url
     */
    private String url;

    /**
     * 空间标识
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private String spaceId;

    /**
     * 图片名称
     */
    private String name = "默认名称";

    /**
     * 简介
     */
    private String introduction = "暂无介绍...";

    /**
     * 分类
     */
    private String category = "默认类别";

    /**
     * 标签(JSON 数组)
     */
    private String tags;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 创建用户 id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long userId;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 转换方法
     */
    public static AdminPictureAddRequest copyProperties(PictureCreateRequest pictureCreateRequest) {
        var adminPictureAddRequest = new AdminPictureAddRequest();
        BeanUtils.copyProperties(pictureCreateRequest, adminPictureAddRequest);
        return adminPictureAddRequest;
    }

}
