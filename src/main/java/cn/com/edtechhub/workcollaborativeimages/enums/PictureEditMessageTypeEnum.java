package cn.com.edtechhub.workcollaborativeimages.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 图片协作编辑消息类型枚举
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Getter
public enum PictureEditMessageTypeEnum {

    INFO("发送通知", "INFO"),
    ENTER_EDIT("进入编辑状态", "ENTER_EDIT"),
    EXIT_EDIT("退出编辑状态", "EXIT_EDIT"),
    EDIT_ACTION("执行编辑操作", "EDIT_ACTION"),
    ERROR("发送错误", "ERROR"),
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
    PictureEditMessageTypeEnum(String description, String code) {
        this.description = description;
        this.code = code;
    }

    /**
     * 根据码值获取枚举
     */
    public static PictureEditMessageTypeEnum getEnums(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureEditMessageTypeEnum theEnum : PictureEditMessageTypeEnum.values()) {
            if (theEnum.code.equals(value)) {
                return theEnum;
            }
        }
        return null;
    }

}

