package cn.com.edtechhub.workcollaborativeimages.enums;

import cn.com.edtechhub.workcollaborativeimages.constant.PictureConstant;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 空间等级枚举
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Getter
public enum SpaceLevelEnums {

    /**
     * 普通版等级枚举实例
     */
    COMMON("普通版", 0, 100, 100L * PictureConstant.ONE_M),

    /**
     * 专业版等级枚举实例
     */
    PROFESSIONAL("专业版", 1, 1000, 1000L * PictureConstant.ONE_M),

    /**
     * 旗舰版等级枚举实例
     */
    FLAGSHIP("旗舰版", 2, 10000, 10000L * PictureConstant.ONE_M);

    /**
     * 等级描述
     */
    private final String description;

    /**
     * 等级码值
     */
    private final int code;

    /**
     * 最大图片数量
     */
    private final long maxCount;

    /**
     * 最大存储空间
     */
    private final long maxSize;

    /**
     * 内部等级构造方法
     */
    SpaceLevelEnums(String description, int code, long maxCount, long maxSize) {
        this.code = code;
        this.description = description;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    /**
     * 根据等级码值获取等级枚举
     */
    public static SpaceLevelEnums getEnums(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceLevelEnums level : SpaceLevelEnums.values()) {
            if (level.code == value) {
                return level;
            }
        }
        return null;
    }

}
