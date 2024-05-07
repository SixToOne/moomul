package com.cheerup.moomul.domain.quiz;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
public class QuizController {

	private final SimpMessageSendingOperations sendingOperations;

	@MessageMapping("/hello")
	@SendTo("/sub/greetings")
	public Message greeting(Message message) throws Exception {
		return message;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	static class Message{
		String message;
	}
}
