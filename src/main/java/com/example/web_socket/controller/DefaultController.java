package com.example.web_socket.controller;

import com.example.web_socket.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * @ClassName  DefaultController
 * @Description
 * @author  lixueyun
 * @Date  2019/2/28 18:49
 */
@Controller
public class DefaultController {

    @GetMapping("/")
    @ResponseBody
    public String helloWord() {
        return "helloWord";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
    //注入SimpMessagingTemplate 用于点对点消息发送
    @Autowired
   private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendPublicMessage") //这里是客户端发送消息对应的路径，等于configureMessageBroker中配置的setApplicationDestinationPrefixes + 这路径即 /app/sendPublicMessage
    @SendTo("/topic/public") //也可以使用 messagingTemplate.convertAndSend(); 推送
    public ChatMessage sendPublicMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }


    @MessageMapping("/sendPrivateMessage") //这里是客户端发送消息对应的路径，等于configureMessageBroker中配置的setApplicationDestinationPrefixes + 这路径即 /app/sendPrivateMessage
    public void sendPrivateMessage(@Payload  ChatMessage msg,Principal principal) {
        msg.setSender(principal.getName());
        //将消息推送到指定路径上
        messagingTemplate.convertAndSendToUser(msg.getReceiver(), "topic/chat", msg);
    }
}
