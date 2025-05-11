package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureReviewRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 状态: 0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
