package cn.com.edtechhub.workcollaborativeimages.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum PictureReviewStatusEnums {

    REVIEWING("待审", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2),
    NOTODO("无事", 3)
    ;

    /**
     * 审核状态描述
     */
    private final String description;

    /**
     * 审核状态代码
     */
    private final int code;

    /**
     * 内部枚举构造方法, 可以自定义其他的状态及其含义
     */
    PictureReviewStatusEnums(String description, int code) {
        this.description = description;
        this.code = code;
    }

    /**
     * 根据 value 获取枚举
     */
    public static PictureReviewStatusEnums getStatusDescription(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureReviewStatusEnums pictureReviewStatusEnums : PictureReviewStatusEnums.values()) {
            if (pictureReviewStatusEnums.code == value) {
                return pictureReviewStatusEnums;
            }
        }
        return null;
    }
}

