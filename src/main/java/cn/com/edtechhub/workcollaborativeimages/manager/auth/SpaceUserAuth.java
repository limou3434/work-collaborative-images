package cn.com.edtechhub.workcollaborativeimages.manager.auth;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * SpaceUserAuth 角色权限具体配置, 读取配置文件中定义的用户列表和权限列表
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Slf4j
public class SpaceUserAuth implements Serializable {

    /**
     * 权限列表
     */
    private List<SpaceUserPermission> permissions;

    /**
     * 角色列表
     */
    private List<SpaceUserRole> roles;

    /// 序列化字段 ///
    @Serial
    private static final long serialVersionUID = 1L;

}
