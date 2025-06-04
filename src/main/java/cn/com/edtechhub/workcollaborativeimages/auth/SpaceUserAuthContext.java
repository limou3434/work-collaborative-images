package cn.com.edtechhub.workcollaborativeimages.auth;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于表示用户在特定空间内的授权上下文信息
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Slf4j
public class SpaceUserAuthContext {

    /**
     * 空间类型
     */
    private Integer spaceType;

}
