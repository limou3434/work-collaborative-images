package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceCreateSelfRequest implements Serializable {

    /**
     * 空间名称
     */
    private String name = "默认名称";

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
