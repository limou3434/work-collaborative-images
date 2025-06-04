package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import cn.com.edtechhub.workcollaborativeimages.constant.SpaceConstant;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceQueryRequest implements Serializable {

    /**
     * 空间类型, 用来指定修改用户的哪一个专属空间
     */
    private Integer spaceType = SpaceConstant.DEFAULT_TYPE;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}