package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class PictureAddRequest implements Serializable {

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name = PictureConstant.DEFAULT_NAME;

    /**
     * 简介
     */
    private String introduction = PictureConstant.DEFAULT_INTRODUCTION;

    /**
     * 分类
     */
    private String category = PictureConstant.DEFAULT_CATEGORT;

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

    /**
     * 空间 id(为空表示公共空间)
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long spaceId;

    /**
     * 状态: 0-待审; 1-通过; 2-拒绝
     */
    private Integer reviewStatus = PictureConstant.DEFAULT_REVIEW_STATUS;

    /**
     * 审核信息
     */
    private String reviewMessage = PictureConstant.DEFAULT_REVIEW_MESSAGE;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 请求转换为实体方法
     */
    public static Picture copyProperties2Entity(PictureAddRequest request) {
        var entity = new Picture();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }

    /**
     * 实体转化为请求方法
     */
    public static PictureAddRequest copyProperties2Request(Picture entity) {
        var request = new PictureAddRequest();
        BeanUtils.copyProperties(entity, request);
        return request;
    }

    /**
     * 转换方法
     */
    public static PictureAddRequest copyProperties(PictureCreateRequest pictureCreateRequest) {
        // TODO: 待删除
        var adminPictureAddRequest = new PictureAddRequest();
        BeanUtils.copyProperties(pictureCreateRequest, adminPictureAddRequest);
        return adminPictureAddRequest;
    }

}
