package cn.com.edtechhub.workcollaborativeimages.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 图片协作编辑动作类型枚举
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Getter
public enum PictureEditActionEnum {

    ZOOM_IN("放大操作", "ZOOM_IN"),
    ZOOM_OUT("缩小操作", "ZOOM_OUT"),
    ROTATE_LEFT("左旋操作", "ROTATE_LEFT"),
    ROTATE_RIGHT("右旋操作", "ROTATE_RIGHT"),
    ;

    /**
     * 描述
     */
    private final String description;

    /**
     * 码值
     */
    private final String code;

    /**
     * 内部等级构造方法
     */
    PictureEditActionEnum(String description, String code) {
        this.description = description;
        this.code = code;
    }

    /**
     * 根据码值获取枚举
     */
    public static PictureEditActionEnum getEnum(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureEditActionEnum theEnum : PictureEditActionEnum.values()) {
            if (theEnum.code.equals(value)) {
                return theEnum;
            }
        }
        return null;
    }

}
