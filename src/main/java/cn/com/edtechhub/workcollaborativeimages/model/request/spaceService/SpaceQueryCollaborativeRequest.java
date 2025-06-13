package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class SpaceQueryCollaborativeRequest implements Serializable {

    /**
     * 需要查看的协作空间对对应标识
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long spaceId;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
