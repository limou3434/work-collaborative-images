package cn.com.edtechhub.workcollaborativeimages.manager.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 对象存储配置
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
@Slf4j
public class CosConfig {

    /**
     * 访问域名
     */
    private String host;

    /**
     * 密钥标识
     */
    private String secretId;

    /**
     * 密钥的值
     */
    private String secretKey;

    /**
     * 地域简称
     */
    private String region;

    /**
     * 存储桶名
     */
    private String bucket;

    /**
     * 获取对象存储客户端
     */
    @Bean
    public COSClient cosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey); // 初始化用户身份信息(secretId, secretKey)
        ClientConfig clientConfig = new ClientConfig(new Region(region)); // 设置 bucket 的地域, clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法
        clientConfig.setHttpProtocol(HttpProtocol.https); // 从 5.6.54 版本开始，默认使用了 https
        return new COSClient(cred, clientConfig); // 生成 cos 客户端
    }

    /**
     * 打印配置
     */
    @PostConstruct
    public void printConfig() {
        log.debug("当前项目 COS 访问域名 {}", host);
        log.debug("当前项目 COS 地域简称 {}", region);
        log.debug("当前项目 COS 存储桶名 {}", bucket);
    }

}