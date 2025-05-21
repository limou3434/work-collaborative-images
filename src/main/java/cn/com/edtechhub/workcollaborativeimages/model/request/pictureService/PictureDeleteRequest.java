package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class PictureDeleteRequest implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 空间 id(为空表示公共空间)
     */
    private Long spaceId;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 转换方法
     */
    public static PictureDeleteRequest copyProperties(PictureDestroyRequest pictureDestroyRequest) {
        // TODO: 待删除
        var adminPictureDeleteRequest = new PictureDeleteRequest();
        BeanUtils.copyProperties(pictureDestroyRequest, adminPictureDeleteRequest);
        return adminPictureDeleteRequest;
    }

}
