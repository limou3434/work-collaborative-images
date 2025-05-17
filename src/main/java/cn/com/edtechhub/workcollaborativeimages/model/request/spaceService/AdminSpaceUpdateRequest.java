package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.AdminPictureUpdateRequest;
import cn.com.edtechhub.workcollaborativeimages.model.request.pictureService.PictureEditRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class AdminSpaceUpdateRequest implements Serializable {

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
    public static AdminSpaceUpdateRequest copyProperties(SpaceEditRequest spaceEditRequest) {
        var adminSpaceUpdateRequest = new AdminSpaceUpdateRequest();
        BeanUtils.copyProperties(spaceEditRequest, adminSpaceUpdateRequest);
        return adminSpaceUpdateRequest;
    }

}
