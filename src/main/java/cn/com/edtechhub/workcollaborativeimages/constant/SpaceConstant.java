package cn.com.edtechhub.workcollaborativeimages.constant;

import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnums;

import java.util.Arrays;
import java.util.List;

/**
 * 空间常量
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public interface SpaceConstant {

    /**
     * 默认名字最大长度
     */
    Integer MAX_NAME_LENGTH = 10;

    /**
     * 默认名字
     */
    String DEFAULT_NAME = "默认空间";

    /**
     * 默认类型
     */
    Integer DEFAULT_TYPE = SpaceTypeEnums.SELF.getCode();

    /**
     * 默认等级
     */
    Integer DEFAULT_LEVEL = SpaceLevelEnums.COMMON.getCode();


}
