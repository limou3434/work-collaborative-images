package cn.com.edtechhub.workcollaborativeimages.manager.ai;

import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * AI 管理器
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Slf4j
@Component
public class AIManager {

    /**
     * 引入配置文件依赖
     */
    @Resource
    AIConfig aiConfig;

    /**
     * 创建扩图任务地址
     */
    public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    /**
     * 查询扩图任务地址
     */
    public static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    /**
     * 创建扩图任务
     *
     * @param createOutPaintingTaskRequest 创建扩图任务请求
     * @return 创建扩图任务结果
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        // 检查参数
        ThrowUtils.throwIf(createOutPaintingTaskRequest == null, CodeBindMessageEnums.PARAMS_ERROR, "扩图参数为空");
        ThrowUtils.throwIf(createOutPaintingTaskRequest != null && createOutPaintingTaskRequest.getModel() == null, CodeBindMessageEnums.PARAMS_ERROR, "智能模型不能为空");
        ThrowUtils.throwIf(createOutPaintingTaskRequest != null && createOutPaintingTaskRequest.getInput() == null, CodeBindMessageEnums.PARAMS_ERROR, "图像输入不能为空");
        ThrowUtils.throwIf(createOutPaintingTaskRequest != null && createOutPaintingTaskRequest.getParameters() == null, CodeBindMessageEnums.PARAMS_ERROR, "处理参数不能为空");

        // 发送请求
        String apiKey = aiConfig.getApiKey(); // 获取 API 密钥
        try (
                HttpResponse httpResponse = HttpRequest
                        .post(CREATE_OUT_PAINTING_TASK_URL)
                        .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                        .header("X-DashScope-Async", "enable") // 必须设置为 enable 以开启异步处理
                        .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                        .body(JSONUtil.toJsonStr(createOutPaintingTaskRequest))
                        .execute() // 本次请求真正发送的时机
        ) {
            log.debug("请求创建扩图任务, 当前立刻返回的响应为 {}", httpResponse);
            ThrowUtils.throwIf(!httpResponse.isOk(), CodeBindMessageEnums.OPERATION_ERROR, "扩图失败, 创建任务的响应体状态码异常");
            CreateOutPaintingTaskResponse response = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);
            ThrowUtils.throwIf(StrUtil.isNotBlank(response.getCode()), CodeBindMessageEnums.OPERATION_ERROR, "扩图接口响应异常" + response.getMessage());
            return response;
        } catch (Exception e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.SYSTEM_ERROR, "调用创建扩图任务接口失败, 出现未知错误");
        }

        return null;
    }

    /**
     * 查询扩图任务
     *
     * @param taskId 需查询的任务标识
     * @return 查询扩图任务结果
     */
    public GetOutPaintingTaskResponse getOutPaintingTask(String taskId) {
        // 检查参数
        ThrowUtils.throwIf(StrUtil.isBlank(taskId), CodeBindMessageEnums.OPERATION_ERROR, "任务标识不能为空");

        // 发送请求
        String apiKey = aiConfig.getApiKey(); // 获取 API 密钥
        try (
                HttpResponse httpResponse = HttpRequest
                        .get(String.format(GET_OUT_PAINTING_TASK_URL, taskId))
                        .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                        .execute()
        ) {
            ThrowUtils.throwIf(!httpResponse.isOk(), CodeBindMessageEnums.OPERATION_ERROR, "扩图失败, 查询任务的响应体状态码异常");
            return JSONUtil.toBean(httpResponse.body(), GetOutPaintingTaskResponse.class);
        } catch (Exception e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.SYSTEM_ERROR, "调用查询扩图任务接口失败, 出现未知错误");
        }

        return null;
    }

}

