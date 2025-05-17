package cn.com.edtechhub.workcollaborativeimages.model.request.spaceService;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@Accessors(chain = true) // 实现链式调用
public class AdminSpaceAddRequest implements Serializable {

    /**
     * 空间名称
     */
    private String spaceName = "默认名称";

    /**
     * 空间级别: 0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /// 序列化字段 ///
    private static final long serialVersionUID = 1L;

    /**
     * 转换方法
     */
    public static AdminSpaceAddRequest copyProperties(SpaceCreateRequest spaceCreateRequest) {
        var adminSpaceAddRequest = new AdminSpaceAddRequest();
        BeanUtils.copyProperties(spaceCreateRequest, adminSpaceAddRequest);
        return adminSpaceAddRequest;
    }

}
