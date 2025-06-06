package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceEditRequest implements Serializable {

    /**
     * 空间类型
     */
    private Integer spaceType;

    /**
     * 空间名称
     */
    private String spaceName;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
