package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminSpaceAddRequest implements Serializable {

    /**
     * 空间名称
     */
    private String spaceName = "默认名称";

    /**
     * 空间级别: 0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
