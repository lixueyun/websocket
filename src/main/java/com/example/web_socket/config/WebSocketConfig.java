package com.example.web_socket.config;


import com.example.web_socket.model.MyPrincipal;
import com.example.web_socket.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * @ClassName  WebSocketConfig
 * @Description
 * @author  lixueyun
 * @Date  2019/2/28 18:48
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //允许客户端使用socketJs方式访问，访问点为ws，允许跨域
        registry.addEndpoint("/ws")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        ServletServerHttpRequest req = (ServletServerHttpRequest) request;
                        //获取token认证
                        String token = req.getServletRequest().getParameter("token");
                        System.out.println("token==>" + token);

                        //TODO 调用dp校验token的合法性
                        if(StringUtils.isEmpty(token)){
                            return false;
                        }
                        MyPrincipal myPrincipal;
                        if (token.equals("lixueyun")) {
                            User user = new User();
                            user.setUid(123456);
                            user.setName("李雪云");
                            myPrincipal = new MyPrincipal(user);
                        } else {
                            User user = new User();
                            user.setUid(456789);
                            user.setName("其他人");
                            myPrincipal = new MyPrincipal(user);
                        }
                        attributes.put(token,myPrincipal);
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

                    }
                })
                .setHandshakeHandler(new DefaultHandshakeHandler(){
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        ServletServerHttpRequest req = (ServletServerHttpRequest) request;
                        String token = req.getServletRequest().getParameter("token");
                        return (MyPrincipal) attributes.get(token);
                    }
                })
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {

        //订阅广播 Broker（消息代理）名称
        registry.enableSimpleBroker("/topic");
        //全局使用的订阅前缀（客户端订阅路径上会体现出来）
        registry.setApplicationDestinationPrefixes("/app/");
        //点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(final WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                        // 客户端与服务器端建立连接后，此处记录谁上线了
                        String username = session.getPrincipal().getName();
                        System.out.println("online: " + username);
                        super.afterConnectionEstablished(session);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        // 客户端与服务器端断开连接后，此处记录谁下线了
                        String username = session.getPrincipal().getName();
                        System.out.println("offline: " + username);
                        super.afterConnectionClosed(session, closeStatus);
                    }
                };
            }
        });

    }
}
