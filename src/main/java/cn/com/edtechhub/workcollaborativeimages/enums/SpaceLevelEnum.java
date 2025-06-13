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
public enum SpaceLevelEnum {

    COMMON("普通版", 0, 100, 100L * PictureConstant.ONE_M),
    PROFESSIONAL("专业版", 1, 1000, 1000L * PictureConstant.ONE_M),
    FLAGSHIP("旗舰版", 2, 10000, 10000L * PictureConstant.ONE_M);

    /**
     * 描述
     */
    private final String description;

    /**
     * 码值
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
    SpaceLevelEnum(String description, int code, long maxCount, long maxSize) {
        this.code = code;
        this.description = description;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    /**
     * 根据码值获取枚举
     */
    public static SpaceLevelEnum getEnums(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceLevelEnum theEnum : SpaceLevelEnum.values()) {
            if (theEnum.code == value) {
                return theEnum;
            }
        }
        return null;
    }

}
