package cn.com.edtechhub.workcollaborativeimages.model.request.userService;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 封禁用户请求
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
public class UserDisableRequest implements Serializable {

    /**
     * 本用户唯一标识(业务层需要考虑使用雪花算法用户标识的唯一性)
     */
    @JsonSerialize(using = ToStringSerializer.class) // 避免 id 过大前端出错
    private Long id;

    /**
     * 封禁时间, 单位为秒, 86400 为一天, -1 代表永久封禁
     */
    private Long disableTime;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}
