package cn.com.edtechhub.workcollaborativeimages.model.request.pictureService;

import cn.com.edtechhub.workcollaborativeimages.model.request.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class PictureSearchRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
