package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import cn.com.edtechhub.workcollaborativeimages.model.request.PageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
