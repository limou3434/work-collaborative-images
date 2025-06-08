package cn.com.edtechhub.workcollaborativeimages.manager.websocket;

import cn.com.edtechhub.workcollaborativeimages.interceptor.WebSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private WebSocketManager webSocketManager;

    @Resource
    private WebSocketInterceptor webSocketInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // websocket
        registry.addHandler(webSocketManager, "/ws/picture/edit")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }
}
