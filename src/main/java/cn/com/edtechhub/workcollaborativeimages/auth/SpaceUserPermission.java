package cn.com.edtechhub.workcollaborativeimages.auth;

import lombok.Data;

import java.io.Serializable;

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
    private static final long serialVersionUID = 1L;

}
