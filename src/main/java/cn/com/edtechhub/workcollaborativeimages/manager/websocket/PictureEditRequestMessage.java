package cn.com.edtechhub.workcollaborativeimages.manager.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片协作编辑请求消息
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditRequestMessage {

    /**
     * 消息类型, 例如 "INFO" - 发送通知, "ENTER_EDIT" - 进入编辑状态, "EXIT_EDIT" - 退出编辑状态, "EDIT_ACTION" - 执行编辑操作, "ERROR" - 发送错误
     */
    private String type;

    /**
     * 执行的编辑动作
     */
    private String editAction;

}