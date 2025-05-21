package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureUpdateRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class SpaceAddRequest implements Serializable {

    /**
     * 空间名称
     */
    private String spaceName = "默认名称";

    /**
     * 空间类型: 0-公有 1-私有 2-协作
     */
    private Integer type;
    
    /**
     * 空间级别: 0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

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
    public static SpaceAddRequest copyProperties(SpaceCreateRequest spaceCreateRequest) {
        // TODO: 等待删除
        var adminSpaceAddRequest = new SpaceAddRequest();
        BeanUtils.copyProperties(spaceCreateRequest, adminSpaceAddRequest);
        return adminSpaceAddRequest;
    }

    /**
     * 请求转换为实体方法
     */
    public static Space copyProperties2Entity(SpaceAddRequest request) {
        var entity = new Space();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }

    /**
     * 实体转化为请求方法
     */
    public static SpaceAddRequest copyProperties2Request(Space entity) {
        var request = new SpaceAddRequest();
        BeanUtils.copyProperties(entity, request);
        return request;
    }

}
