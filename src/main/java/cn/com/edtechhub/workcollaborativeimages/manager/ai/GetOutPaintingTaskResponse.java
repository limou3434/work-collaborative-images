package cn.com.edtechhub.workcollaborativeimages.manager.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询阔图任务响应类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOutPaintingTaskResponse {

    /**
     * 请求标识
     */
    private String requestId;

    /**
     * 输出信息
     */
    private Output output;

    /**
     * 表示任务的输出信息
     */
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

        /**
         * 提交时间, 格式为 YYYY-MM-DD HH:mm:ss.SSS
         */
        private String submitTime;

        /**
         * 调度时间, 格式为 YYYY-MM-DD HH:mm:ss.SSS
         */
        private String scheduledTime;

        /**
         * 结束时间, 格式为 YYYY-MM-DD HH:mm:ss.SSS
         */
        private String endTime;

        /**
         * 输出图像的 URL
         */
        private String outputImageUrl;

        /**
         * 接口状态码值, 接口成功请求不会返回该参数
         */
        private String code;

        /**
         * 接口响应信息, 接口成功请求不会返回该参数
         */
        private String message;

        /**
         * 任务指标信息
         */
        private TaskMetrics taskMetrics;
    }

    /**
     * 表示任务的统计信息
     */
    @Data
    public static class TaskMetrics {

        /**
         * 总任务数
         */
        private Integer total;

        /**
         * 成功任务数
         */
        private Integer succeeded;

        /**
         * 失败任务数
         */
        private Integer failed;
    }

}
