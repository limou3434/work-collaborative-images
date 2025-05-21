package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import cn.com.edtechhub.workcollaborativeimages.model.request.PageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true) // 实现链式调用
public class SpaceSearchRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 用户 id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long userId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 转换方法
     */
    public static SpaceSearchRequest copyProperties(SpaceQueryRequest spaceQueryRequest) {
        var adminSpaceSearchRequest = new SpaceSearchRequest();
        BeanUtils.copyProperties(spaceQueryRequest, adminSpaceSearchRequest);
        return adminSpaceSearchRequest;
    }

}
