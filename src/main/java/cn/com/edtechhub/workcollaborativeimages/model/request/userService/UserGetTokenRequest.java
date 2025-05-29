package cn.com.edtechhub.workcollaborativeimages.model.request.userService;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 添加用户请求
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Accessors(chain = true) // 实现链式调用
public class UserGetTokenRequest {

    /**
     * 指定的用户 id
     */
    Long userId;

}
