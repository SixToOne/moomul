package com.cheerup.moomul.domain.quiz.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.quiz.dto.CancelRequestDto;
import com.cheerup.moomul.domain.quiz.dto.CancelResponseDto;
import com.cheerup.moomul.domain.quiz.dto.CreateRequestDto;
import com.cheerup.moomul.domain.quiz.dto.WaitingResponse;
import com.cheerup.moomul.domain.quiz.dto.JoinRequestDto;
import com.cheerup.moomul.domain.quiz.dto.QuizResponseDto;
import com.cheerup.moomul.domain.quiz.dto.ResultRequestDto;
import com.cheerup.moomul.domain.quiz.dto.ResultResponseDto;
import com.cheerup.moomul.domain.quiz.dto.SubmitRequestDto;
import com.cheerup.moomul.domain.quiz.entity.Participant;
import com.cheerup.moomul.domain.quiz.entity.Party;
import com.cheerup.moomul.domain.quiz.entity.QuizInfo;
import com.cheerup.moomul.domain.quiz.entity.Room;
import com.cheerup.moomul.domain.quiz.repository.PartyRepository;
import com.cheerup.moomul.domain.quiz.repository.QuizInfoRepository;
import com.cheerup.moomul.domain.quiz.repository.QuizRepository;
import com.cheerup.moomul.domain.quiz.repository.RoomRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class QuizController {

	private final SimpMessageSendingOperations sendingOperations;
	private final QuizRepository quizRepository;
	private final RoomRepository roomRepository;
	private final PartyRepository partyRepository;
	private final QuizInfoRepository quizInfoRepository;

	@MessageMapping("/quiz/{userId}/create")
	public void create(@DestinationVariable Long userId, CreateRequestDto createRequestDto) {
		quizInfoRepository.save(new QuizInfo(userId, 0, quizRepository.findRandomQuiz(createRequestDto.numOfQuiz())));
		Room room = roomRepository.save(new Room(userId, createRequestDto.numOfPeople(), createRequestDto.numOfPeople(), false,
				createRequestDto.nickname()));
		List<Participant> participants = new ArrayList<>();
		participants.add(new Participant(createRequestDto.nickname(), 0));

		partyRepository.save(new Party(userId, participants));
		WaitingResponse waitingResponse = new WaitingResponse("waiting", createRequestDto.nickname(),
			participants.size() - 1, room.getNumOfPeople(), LocalDateTime.now());

		sendingOperations.convertAndSend("/sub/quiz/" + userId + "/"+createRequestDto.nickname(), waitingResponse);
	}

	@MessageMapping("/quiz/{userId}/join")
	public void join(@DestinationVariable Long userId, JoinRequestDto joinRequestDto) {
		Room room = roomRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));

		Party party = partyRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("파티가 존재하지 않습니다."));
		party.join(new Participant(joinRequestDto.nickname(), 0));
		partyRepository.save(party);

		WaitingResponse waitingResponse = new WaitingResponse("waiting", room.getNickname(),
			party.getParticipants().size() - 1, room.getNumOfPeople(), LocalDateTime.now());

		party.getParticipants().forEach(participant -> {
			sendingOperations.convertAndSend("/sub/quiz/" + userId + "/"+participant.getNickname(), waitingResponse);
		});
	}

	@MessageMapping("/quiz/{userId}/start")
	public void start(@DestinationVariable Long userId, JoinRequestDto joinRequestDto) {

	}

	@MessageMapping("/quiz/{userId}/cancel")
	public void participantCancel(@DestinationVariable Long userId, CancelRequestDto cancelRequestDto) {
		Room room = roomRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));
		Party party = partyRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("파티가 존재하지 않습니다."));
		if (room.getNickname().equals(cancelRequestDto.nickname())) {
			roomRepository.delete(room);
			party.getParticipants().forEach(participant -> {
				sendingOperations.convertAndSend("/sub/quiz/" + userId + "/"+participant.getNickname(), new CancelResponseDto("cancel", "방이 취소되었습니다."));
			});
			partyRepository.delete(party);
			quizInfoRepository.deleteById(userId);

		} else {
			party.cancel(cancelRequestDto.nickname());
			partyRepository.save(party);
			party.getParticipants().forEach(participant -> {
				sendingOperations.convertAndSend("/sub/quiz/" + userId + "/"+participant.getNickname(), new WaitingResponse("waiting", room.getNickname(), party.getParticipants().size()-1, room.getNumOfPeople(), LocalDateTime.now()));
			});
		}
	}
	@MessageMapping("/quiz/{userId}/submit")
	public void submit(@DestinationVariable Long userId, SubmitRequestDto submitRequestDto) {

	}

	@MessageMapping("/quiz/{userId}/grade")
	public void grade(@DestinationVariable Long userId) {

	}

	@MessageMapping("/quiz/{userId}/next")
	public void next(@DestinationVariable Long userId) {

		QuizResponseDto cur=quizService.findNextQuiz(userId);

		Party curParty=partyRepository.findById(userId).get();

		for(Participant toSender:curParty.getParticipants()){
			sendingOperations.convertAndSend("/sub/quiz/"+userId+"/"+toSender.getNickname(),cur);
		}

	}

	@MessageMapping("/quiz/{userId}/result")
	public void result(@DestinationVariable Long userId, ResultRequestDto resultRequestDto) {
		List<Rank> result=quizService.getResult(userId,resultRequestDto.nickname());
		Rank myRank=null;
		for(Rank cur: result){
			if(cur.getNickname().equals(resultRequestDto.nickname())){
				myRank=cur;
			}
		}
		ResultResponseDto resultResponseDto=new ResultResponseDto("result",myRank,result);

		sendingOperations.convertAndSend("/sub/quiz/"+userId+"/"+resultRequestDto.nickname(),resultResponseDto);

	}


}
