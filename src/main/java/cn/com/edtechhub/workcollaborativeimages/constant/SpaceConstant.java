package cn.com.edtechhub.workcollaborativeimages.constant;

import cn.com.edtechhub.workcollaborativeimages.enums.SpaceLevelEnum;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnum;

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
    Integer DEFAULT_TYPE = SpaceTypeEnum.SELF.getCode();

    /**
     * 默认等级
     */
    Integer DEFAULT_LEVEL = SpaceLevelEnum.COMMON.getCode();


}
