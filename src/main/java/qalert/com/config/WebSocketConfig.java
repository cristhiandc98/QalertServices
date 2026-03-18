package qalert.com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

import qalert.com.utils.consts.ApiConst;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //config.setApplicationDestinationPrefixes(ApiConst.WS_SERVER_PREFIX); 
        config.enableSimpleBroker(ApiConst.WS_CLIENT_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ApiConst.WS_ENDPOINT)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}