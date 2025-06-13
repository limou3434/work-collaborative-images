package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import cn.com.edtechhub.workcollaborativeimages.model.request.PageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true) // 实现链式调用
public class PictureSearchPictureRequest extends PageRequest implements Serializable {

    /**
     * 图片 id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long pictureId;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
