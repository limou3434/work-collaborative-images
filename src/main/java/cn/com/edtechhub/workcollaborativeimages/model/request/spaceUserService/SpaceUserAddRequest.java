package cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService;

import cn.com.edtechhub.workcollaborativeimages.constant.SpaceUserConstant;
import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 添加空间用户关联请求
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Accessors(chain = true) // 实现链式调用
public class SpaceUserAddRequest implements Serializable {

    /**
     * 空间 ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long spaceId;

    /**
     * 用户 ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long userId;

    /**
     * 空间角色: 0-viewer 1-editor 2-manger
     */
    private Integer spaceRole = SpaceUserConstant.DEFAULT_ROLE;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 请求转换为实体方法
     */
    public static SpaceUser copyProperties2Entity(SpaceUserAddRequest request) {
        var entity = new SpaceUser();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }

    /**
     * 实体转化为请求方法
     */
    public static SpaceUserAddRequest copyProperties2Request(User entity) {
        var request = new SpaceUserAddRequest();
        BeanUtils.copyProperties(entity, request);
        return request;
    }

}

