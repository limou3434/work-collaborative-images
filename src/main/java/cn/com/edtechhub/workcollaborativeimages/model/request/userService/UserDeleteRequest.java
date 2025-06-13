package cn.com.edtechhub.workcollaborativeimages.model.request.userService;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 删除用户请求
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Accessors(chain = true) // 实现链式调用
public class UserDeleteRequest implements Serializable {

    /**
     * 本用户唯一标识(业务层需要考虑使用雪花算法用户标识的唯一性)
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
