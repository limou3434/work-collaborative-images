package cn.com.edtechhub.workcollaborativeimages.manager.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * 引入 WebSocket 握手拦截器依赖
     */
    @Resource
    private WsHandshakeInterceptor webSocketInterceptor;

    /**
     * 引入 WebSocket 图片编辑管理者依赖
     */
    @Resource
    private PictureEditHandler pictureEditHandler;

    /**
     *
     * @param registry WebSocket 管理器
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(pictureEditHandler, "/ws/picture/edit")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }

    /**
     * 打印配置
     */
    @PostConstruct
    public void printConfig() {
        log.debug("[WebSocketConfig]");
    }


}
