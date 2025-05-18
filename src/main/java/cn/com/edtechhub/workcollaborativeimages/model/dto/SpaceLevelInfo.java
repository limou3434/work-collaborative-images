package cn.com.edtechhub.workcollaborativeimages.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 空间等级元数据描述
 */
@Data
@AllArgsConstructor
public class SpaceLevelInfo {

    /**
     * 等级代码
     */
    private int value;

    /**
     * 等级描述
     */
    private String text;

    /**
     * 对应最大数量
     */
    private long maxCount;

    /**
     * 对应最大大小
     */
    private long maxSize;

}
