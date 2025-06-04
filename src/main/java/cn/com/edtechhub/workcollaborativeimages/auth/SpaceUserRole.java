package cn.com.edtechhub.workcollaborativeimages.auth;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpaceUserRole implements Serializable {

    /**
     * 角色键值
     */
    private String key;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 权限键列表
     */
    private List<String> permissions;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

}

