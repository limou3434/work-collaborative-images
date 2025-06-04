package cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceUserMoveOutRequest implements Serializable {

    /**
     * 用户唯一标识
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long userId;

}
