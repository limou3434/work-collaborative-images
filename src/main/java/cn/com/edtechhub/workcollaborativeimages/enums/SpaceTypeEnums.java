package cn.com.edtechhub.workcollaborativeimages.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum SpaceTypeEnums {

    /**
     * 公有图库枚举实例
     */
    PUBLIC("公有图库", 0),

    /**
     * 私有空间枚举实例
     */
    SELF("私有空间", 1),

    /**
     * 团队空间枚举实例
     */
    COLLABORATIVE("团队空间", 2);

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 类型码值
     */
    private final int code;

    /**
     * 内部等级构造方法
     */
    SpaceTypeEnums(String description, int code) {
        this.description = description;
        this.code = code;
    }

    /**
     * 根据类型码值获取类型枚举
     */
    public static SpaceTypeEnums getTypeDescription(Integer code) {
        if (ObjUtil.isEmpty(code)) {
            return null;
        }
        for (SpaceTypeEnums type : SpaceTypeEnums.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
