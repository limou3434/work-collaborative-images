package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceQueryByIdRequest implements Serializable {

    /**
     * 指定的空间标识
     */
    private Long spaceId;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}