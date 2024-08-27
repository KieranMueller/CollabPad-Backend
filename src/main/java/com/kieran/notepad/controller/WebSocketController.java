package com.kieran.notepad.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send/{userId}")
    public void sendMessageToSession(@DestinationVariable String userId,
                                     @RequestBody Message<String> message) {
        log.info("Received message. UserId ({}), Message ({}), ({})", userId,
                message.getPayload(), message);
        log.info("{}", message.getHeaders().get("nativeHeaders"));
        messagingTemplate.convertAndSend("/topic/messages/" + userId, message, message.getHeaders());
    }
}
