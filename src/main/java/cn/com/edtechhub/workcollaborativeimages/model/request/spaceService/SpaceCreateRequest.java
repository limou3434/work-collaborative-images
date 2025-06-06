package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import cn.com.edtechhub.workcollaborativeimages.constant.SpaceConstant;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceCreateRequest implements Serializable {

    /**
     * 空间名称
     */
    private String spaceName = SpaceConstant.DEFAULT_NAME;

    /**
     * 空间类型
     */
    private Integer spaceType = SpaceConstant.DEFAULT_TYPE;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
