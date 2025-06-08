package cn.com.edtechhub.workcollaborativeimages.manager.search;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Search 配置类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Data
@Slf4j
public class SearchConfig {

    /**
     * 构造请求地址
     */
    private String url = "https://graph.baidu.com/upload?uptime=";

    /**
     * 打印配置
     */
    @PostConstruct
    public void printConfig() {
        log.debug("[SearchConfig] 当前项目 Search 请求的 URL 为: {}", this.url);
    }

}