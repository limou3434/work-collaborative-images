package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class SpaceUpdateRequest implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别: 0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 转换方法
     */
    public static SpaceUpdateRequest copyProperties(SpaceEditRequest spaceEditRequest) {
        // TODO: 等待删除
        var adminSpaceUpdateRequest = new SpaceUpdateRequest();
        BeanUtils.copyProperties(spaceEditRequest, adminSpaceUpdateRequest);
        return adminSpaceUpdateRequest;
    }

    /**
     * 请求转换为实体方法
     */
    public static Space copyProperties2Entity(SpaceUpdateRequest request) {
        var entity = new Space();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }

    /**
     * 实体转化为请求方法
     */
    public static SpaceUpdateRequest copyProperties2Request(Space entity) {
        var request = new SpaceUpdateRequest();
        BeanUtils.copyProperties(entity, request);
        return request;
    }

}
