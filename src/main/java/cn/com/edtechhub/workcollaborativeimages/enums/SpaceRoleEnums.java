package cn.com.edtechhub.workcollaborativeimages.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 空间角色枚举
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Getter
public enum SpaceRoleEnums { // 由于效率问题, 这里手动缓存了数据库中的用户权限, 数据库中的用户等级只是用来限制插入和前端查询的

    /**
     * 封号角色枚举实例
     */
    BAN_ROLE(0, "viewer"),

    /**
     * 用户角色枚举实例
     */
    USER_ROLE(1, "editor"),

    /**
     * 管理角色枚举实例
     */
    ADMIN_ROLE(2, "manger");

    /**
     * 角色码值
     */
    private final int code;

    /**
     * 角色描述
     */
    private final String description;

    /**
     * 内部角色构造方法
     */
    SpaceRoleEnums(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据角色码值获取角色枚举
     */
    public static SpaceRoleEnums getEnums(int code) { // TODO: 等待改正放回值为枚举
        if (ObjUtil.isEmpty(code)) {
            return null;
        }
        for (SpaceRoleEnums role : SpaceRoleEnums.values()) {
            if (role.getCode() == code) {
                return role;
            }
        }
        return null;
    }

}
