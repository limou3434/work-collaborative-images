package cn.com.edtechhub.workcollaborativeimages.manager;

import cn.com.edtechhub.workcollaborativeimages.config.AIConfig;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.model.dto.CreateOutPaintingTaskRequest;
import cn.com.edtechhub.workcollaborativeimages.model.dto.CreateOutPaintingTaskResponse;
import cn.com.edtechhub.workcollaborativeimages.model.dto.GetOutPaintingTaskResponse;
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

    // 读取配置文件
    @Resource
    AIConfig aiConfig;

    // 创建扩图任务地址
    public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    // 查询扩图任务地址
    public static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    /**
     * 创建扩图任务
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        ThrowUtils.throwIf(createOutPaintingTaskRequest == null, CodeBindMessageEnums.OPERATION_ERROR, "扩图参数为空");

        // 发送请求
        String apiKey = aiConfig.getApiKey();
        try (
                HttpResponse httpResponse = HttpRequest
                        .post(CREATE_OUT_PAINTING_TASK_URL)
                        .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                        .header("X-DashScope-Async", "enable") // 必须设置为 enable 开启异步处理
                        .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                        .body(JSONUtil.toJsonStr(createOutPaintingTaskRequest))
                        .execute()
        ) {
            ThrowUtils.throwIf(!httpResponse.isOk(), CodeBindMessageEnums.OPERATION_ERROR, "扩图失败");
            CreateOutPaintingTaskResponse response = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);
            String errorCode = response.getCode();
            ThrowUtils.throwIf(StrUtil.isNotBlank(errorCode), CodeBindMessageEnums.OPERATION_ERROR, "扩图接口响应异常" + response.getMessage());
            return response;
        } catch (Exception e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.SYSTEM_ERROR, "调用创建扩图接口失败, 出现未知错误");
        }

        return null;
    }

    /**
     * 查询扩图任务
     */
    public GetOutPaintingTaskResponse getOutPaintingTask(String taskId) {
        ThrowUtils.throwIf(StrUtil.isBlank(taskId), CodeBindMessageEnums.OPERATION_ERROR, "任务标识不能为空");

        // 发送请求
        String apiKey = aiConfig.getApiKey();
        try (
                HttpResponse httpResponse = HttpRequest
                        .get(String.format(GET_OUT_PAINTING_TASK_URL, taskId))
                        .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                        .execute()
        ) {
            ThrowUtils.throwIf(!httpResponse.isOk(), CodeBindMessageEnums.OPERATION_ERROR, "获取任务失败");
            return JSONUtil.toBean(httpResponse.body(), GetOutPaintingTaskResponse.class);
        } catch (Exception e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.SYSTEM_ERROR, "调用查询扩图接口失败, 出现未知错误");
        }

        return null;
    }

}

