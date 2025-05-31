package cn.com.edtechhub.workcollaborativeimages.manager;


import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.model.dto.ImageSearchResult;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 以图搜图管理器
 * 使用的是百度的 API, 不过保证长期有效
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Slf4j
public class SearchManager {

    /**
     * 获取和给定图片地址类似的图片列表
     */
    public static List<ImageSearchResult> getSimilarPictureList(String pictureUrl) {
        String pageUrl = SearchManager.getImagePageUrl(pictureUrl);
        log.debug("获取到的搜索页面地址为 {}", pageUrl);
        String imageFirstUrlOfImagePageUrl = SearchManager.getImageFirstUrlOfImagePageUrl(pageUrl);
        log.debug("获取到的脚本搜索结果为 {}", imageFirstUrlOfImagePageUrl);
        List<ImageSearchResult> responseImageList = getResponseImageList(imageFirstUrlOfImagePageUrl);
        log.debug("获取到的相似图片列表为 {}", responseImageList);
        return responseImageList;
    }

    /**
     * 获取以图搜图后对应页面的地址
     */
    private static String getImagePageUrl(String imageUrl) {
        // 检查参数
        ThrowUtils.throwIf(StringUtils.isBlank(imageUrl), CodeBindMessageEnums.PARAMS_ERROR, "需要搜索的图片不能为空");

        // 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");

        // 构造请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + System.currentTimeMillis();

        // 发送请求获取页面
        try {
            // 发送 POST 请求到百度接口
            HttpResponse response = HttpRequest
                    .post(url)
                    .header("acs-token", RandomUtil.randomString(1)) // 这一个必需要加上否则无法成功
                    .form(formData)
                    .timeout(5000)
                    .execute();

            ThrowUtils.throwIf(HttpStatus.HTTP_OK != response.getStatus(), CodeBindMessageEnums.OPERATION_ERROR, "接口调用失败, 可能该搜索 API 已经失效"); // 判断响应状态

            // 解析响应
            String responseBody = response.body();
            Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);

            // 处理响应结果
            ThrowUtils.throwIf(result == null || !Integer.valueOf(0).equals(result.get("status")), CodeBindMessageEnums.OPERATION_ERROR, "接口调用失败");
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            String rawUrl = (String) data.get("url");
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8); // 对结果中返回的 URL 进行解码
            ThrowUtils.throwIf(searchResultUrl == null, CodeBindMessageEnums.OPERATION_ERROR, "返回结果中的 URL 解析后为空");
            return searchResultUrl;
        } catch (Exception e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "搜索失败出现未知错误");
        }
        return null;
    }

    /**
     * 获取搜图结果页面中的相似图片地址列表的 JSON 数组结果所对应的地址
     */
    private static String getImageFirstUrlOfImagePageUrl(String url) {
        // 检查参数
        ThrowUtils.throwIf(StringUtils.isBlank(url), CodeBindMessageEnums.PARAMS_ERROR, "需要解析的搜图页面结果地址不能为空");

        try {
            // 使用 Jsoup 获取 HTML 内容
            Document document = Jsoup
                    .connect(url)
                    .timeout(5000)
                    .get();

            // 获取所有 <script> 标签
            Elements scriptElements = document.getElementsByTag("script");

            // 遍历找到包含 `firstUrl` 的脚本内容
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                if (scriptContent.contains("\"firstUrl\"")) {
                    Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*\"(.*?)\""); // 正则表达式提取 firstUrl 的值
                    Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        String firstUrl = matcher.group(1);
                        firstUrl = firstUrl.replace("\\/", "/"); // 处理转义字符
                        return firstUrl;
                    }
                }
            }
            ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "未找到 firstUrl");
        } catch (Exception e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "搜索失败");
        }
        return null;
    }

    /**
     * 把响应 JSON 结果地址中结果转化为图片列表
     */
    private static List<ImageSearchResult> getResponseImageList(String url) {
        try {
            // 发起 GET 请求
            HttpResponse response = HttpUtil
                    .createGet(url)
                    .execute();

            // 获取响应内容
            int statusCode = response.getStatus();
            String body = response.body();
            ThrowUtils.throwIf(statusCode != 200, CodeBindMessageEnums.OPERATION_ERROR, "接口调用失败");

            // 处理响应
            return SearchManager.processResponse(body);
        } catch (Exception e) {
            ThrowUtils.throwIf(true, CodeBindMessageEnums.OPERATION_ERROR, "获取图片列表失败");
        }
        return null;
    }

    /**
     * 处理接口响应内容
     */
    private static List<ImageSearchResult> processResponse(String responseBody) {
        // 解析响应对象
        JSONObject jsonObject = new JSONObject(responseBody);

        ThrowUtils.throwIf(!jsonObject.containsKey("data"), CodeBindMessageEnums.OPERATION_ERROR, "未获取到搜索结果地址中的 data 结果");
        JSONObject data = jsonObject.getJSONObject("data");

        ThrowUtils.throwIf(!data.containsKey("list"), CodeBindMessageEnums.OPERATION_ERROR, "未获取到搜索结果地址中的 data 所包含的 list 结果");
        JSONArray list = data.getJSONArray("list");

        return JSONUtil.toList(list, ImageSearchResult.class);
    }

    public static void main(String[] args) {
        System.out.println(getSimilarPictureList("https://wci-1318277926.cos.ap-guangzhou.myqcloud.com/work-collaborative-images/public/38/2025-05-30_0quces77rnzcfjlu.webp"));
    }

}