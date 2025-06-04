package cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService;

import cn.com.edtechhub.workcollaborativeimages.model.request.PageRequest;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 查询空间用户关联请求
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true) // 实现链式调用
public class SpaceUserSearchRequest extends PageRequest implements Serializable {

    /**
     * 本空间用户关联唯一标识(业务层需要考虑使用雪花算法用户标识的唯一性)
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 空间唯一标识
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long spaceId;

    /**
     * 用户唯一标识
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long userId;

}
