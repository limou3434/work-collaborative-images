package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import cn.com.edtechhub.workcollaborativeimages.manager.ai.CreateOutPaintingTaskRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class PictureCreateOutPaintingTaskRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId; // TODO: 抽离判断是否有权限编辑图片的方法, 方便后续的拓展

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
