package cn.com.edtechhub.workcollaborativeimages.manager.auth;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 描述权限内容
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
public class SpaceUserPermission implements Serializable {

    /**
     * 权限键值
     */
    private String key;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限描述
     */
    private String description;

    /// 序列化字段 ///
    @Serial
    private static final long serialVersionUID = 1L;

}
