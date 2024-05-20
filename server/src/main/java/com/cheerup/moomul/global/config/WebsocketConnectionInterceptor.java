package com.cheerup.moomul.global.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebsocketConnectionInterceptor implements ChannelInterceptor {
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			log.info("CONNECTED");
		} else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
			log.info("DISCONNECTED");
		}
		return message;
	}
}
