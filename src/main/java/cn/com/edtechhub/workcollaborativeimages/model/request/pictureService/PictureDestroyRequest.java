package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class PictureDestroyRequest implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
