package cn.com.edtechhub.workcollaborativeimages.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建扩图任务响应类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOutPaintingTaskResponse {

    /**
     * 接口状态码值: 接口成功请求不会返回该参数
     */
    private String code;

    /**
     * 接口响应信息: 接口成功请求不会返回该参数
     */
    private String message;

    /**
     * 请求唯一标识: 用来请求明细溯源和问题排查
     */
    private String requestId;

    /**
     * 表示任务的输出信息
     */
    private Output output;

    @Data
    public static class Output {

        /**
         * 任务标识
         */
        private String taskId;

        /**
         * 任务状态
         * PENDING: 排队中
         * RUNNING: 处理中
         * SUSPENDED: 挂起
         * SUCCEEDED: 执行成功
         * FAILED: 执行失败
         * UNKNOWN: 任务不存在或状态未知
         */
        private String taskStatus;
    }

}
