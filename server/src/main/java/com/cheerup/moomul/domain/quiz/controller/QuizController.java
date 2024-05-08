package com.cheerup.moomul.domain.quiz.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.quiz.dto.CancelRequestDto;
import com.cheerup.moomul.domain.quiz.dto.CreateRequestDto;
import com.cheerup.moomul.domain.quiz.dto.JoinRequestDto;
import com.cheerup.moomul.domain.quiz.dto.QuizResponseDto;
import com.cheerup.moomul.domain.quiz.dto.ResultRequestDto;
import com.cheerup.moomul.domain.quiz.dto.ResultResponseDto;
import com.cheerup.moomul.domain.quiz.dto.SubmitRequestDto;
import com.cheerup.moomul.domain.quiz.entity.Participant;
import com.cheerup.moomul.domain.quiz.entity.Party;
import com.cheerup.moomul.domain.quiz.entity.Quiz;
import com.cheerup.moomul.domain.quiz.entity.QuizInfo;
import com.cheerup.moomul.domain.quiz.entity.Rank;
import com.cheerup.moomul.domain.quiz.entity.Room;
import com.cheerup.moomul.domain.quiz.repository.PartyRepository;
import com.cheerup.moomul.domain.quiz.repository.QuizInfoRepository;
import com.cheerup.moomul.domain.quiz.repository.RoomRepository;
import com.cheerup.moomul.domain.quiz.service.QuizService;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class QuizController {

	private final SimpMessageSendingOperations sendingOperations;
	private final QuizInfoRepository quizInfoRepository;
	private final QuizService quizService;
	private final RoomRepository roomRepository;
	private final PartyRepository partyRepository;
	private final UserRepository userRepository;

	@PostConstruct
	public void init(){
		//퀴즈 세팅
		List<Quiz> list=new ArrayList<>();
		list.add(new Quiz(1L,"1번","보기1","보기2"));
		list.add(new Quiz(2L,"2번","보기1","보기2"));
		list.add(new Quiz(3L,"3번","보기1","보기2"));
		list.add(new Quiz(4L,"4번","보기1","보기2"));
		list.add(new Quiz(5L,"5번","보기1","보기2"));
		list.add(new Quiz(6L,"6번","보기1","보기2"));
		list.add(new Quiz(7L,"7번","보기1","보기2"));
		quizInfoRepository.save(new QuizInfo(1L,1,list));

		//방정보 세팅
		roomRepository.save(new Room(1L,6,7,false,"나호균"));

		//참여자 세팅
		List<Participant> partyList=new ArrayList<>();
		partyList.add(new Participant("나호균",0));
		partyList.add(new Participant("2user",10));
		partyList.add(new Participant("3user",20));
		partyList.add(new Participant("4user",30));
		partyList.add(new Participant("5user",40));
		partyList.add(new Participant("6user",50));
		partyRepository.save(new Party(1L,partyList));
	}
	
	@MessageMapping("/quiz/{userId}/create")
	public CreateRequestDto create(@DestinationVariable Long userId, CreateRequestDto createRequestDto) {
		return createRequestDto;
	}

	@MessageMapping("/quiz/{userId}/join")
	public void join(@DestinationVariable Long userId, JoinRequestDto joinRequestDto) {

	}

	@MessageMapping("/quiz/{userId}/start")
	public void start(@DestinationVariable Long userId, JoinRequestDto joinRequestDto) {

	}

	@MessageMapping("/quiz/{userId}/hostCancel")
	public void hostCancel(@DestinationVariable Long userId, CancelRequestDto cancelRequestDto) {

	}

	@MessageMapping("/quiz/{userId}/participantCancel")
	public void participantCancel(@DestinationVariable Long userId, CancelRequestDto cancelRequestDto) {

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
