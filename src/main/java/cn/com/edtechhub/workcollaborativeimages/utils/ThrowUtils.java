package cn.com.edtechhub.workcollaborativeimages.utils;

import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理工具类
 */
@Slf4j
public class ThrowUtils {

    /**
     * 条件成立则抛异常, 并且打印消息日志
     */
    public static void throwIf(boolean condition, CodeBindMessageEnums codeBindMessageEnums, String message) {
        if (condition) {
            log.warn(message);
            throw new BusinessException(codeBindMessageEnums, message);
        }
    }

}