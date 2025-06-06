package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class PictureUpdateRequest implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

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
    public static PictureUpdateRequest copyProperties(PictureEditRequest pictureEditRequest) {
        // TODO: 待删除
        var adminPictureUpdateRequest = new PictureUpdateRequest();
        BeanUtils.copyProperties(pictureEditRequest, adminPictureUpdateRequest);
        return adminPictureUpdateRequest;
    }

    /**
     * 请求转换为实体方法
     */
    public static Picture copyProperties2Entity(PictureUpdateRequest request) {
        var entity = new Picture();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }

    /**
     * 实体转化为请求方法
     */
    public static PictureUpdateRequest copyProperties2Request(Picture entity) {
        var request = new PictureUpdateRequest();
        BeanUtils.copyProperties(entity, request);
        return request;
    }

}
