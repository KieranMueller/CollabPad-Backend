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

    @MessageMapping("/send/{sessionId}")
//    @SendTo("/topic/messages")
    public void sendMessageToSession(@DestinationVariable String sessionId,
                                     @RequestBody Message<String> message) {
        log.info("Received message. SessionId ({}), Message ({}), ({})", sessionId, message.getPayload(), message);
        log.info("{}", message.getHeaders().get("nativeHeaders"));
        messagingTemplate.convertAndSend("/topic/messages/" + sessionId, message, message.getHeaders());
    }
}
