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
import com.cheerup.moomul.domain.quiz.dto.GradeResponseDto;
import com.cheerup.moomul.domain.quiz.dto.QuizDto;
import com.cheerup.moomul.domain.quiz.dto.SubmitResponseDto;
import com.cheerup.moomul.domain.quiz.dto.WaitingResponse;
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
import com.cheerup.moomul.domain.quiz.entity.SubmitInfo;
import com.cheerup.moomul.domain.quiz.repository.PartyRepository;
import com.cheerup.moomul.domain.quiz.repository.QuizInfoRepository;
import com.cheerup.moomul.domain.quiz.repository.QuizRepository;
import com.cheerup.moomul.domain.quiz.repository.RoomRepository;
import com.cheerup.moomul.domain.quiz.repository.SubmitInfoRepository;
import com.cheerup.moomul.domain.quiz.service.QuizService;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class QuizController {

	private final SimpMessageSendingOperations sendingOperations;
	private final QuizRepository quizRepository;
	private final RoomRepository roomRepository;
	private final PartyRepository partyRepository;
	private final QuizInfoRepository quizInfoRepository;
	private final QuizService quizService;
	private final UserRepository userRepository;
	private final SubmitInfoRepository submitInfoRepository;


	@MessageMapping("/quiz/{username}/create")
	public void create(@DestinationVariable String username, CreateRequestDto createRequestDto) {
		if(username.equals(createRequestDto.nickname()))
			throw new BaseException(ErrorCode.NO_AUTHORITY);

		userRepository.findByUsername(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));


		quizInfoRepository.save(new QuizInfo(username, 0, quizRepository.findRandomQuiz(createRequestDto.numOfQuiz())));
		Room room = roomRepository.save(new Room(username, createRequestDto.numOfPeople(), createRequestDto.numOfPeople(), false,
				createRequestDto.nickname()));
		List<Participant> participants = new ArrayList<>();
		participants.add(new Participant(createRequestDto.nickname(), 0));

		partyRepository.save(new Party(username, participants));
		WaitingResponse waitingResponse = new WaitingResponse("waiting", createRequestDto.nickname(),
			participants.size() - 1, room.getNumOfPeople(), LocalDateTime.now());

		sendingOperations.convertAndSend("/sub/quiz/" + username + "/"+createRequestDto.nickname(), waitingResponse);
	}

	@MessageMapping("/quiz/{username}/join")
	public void join(@DestinationVariable String username, JoinRequestDto joinRequestDto) {
		Room room = roomRepository.findById(username).orElseThrow(() -> new BaseException(ErrorCode.NO_QUIZ_ERROR));

		Party party = partyRepository.findById(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_QUIZ_ERROR));
		if (party.getParticipants().size()-1 >= room.getNumOfPeople())
			throw new BaseException(ErrorCode.FULL_ROOM);
		party.join(new Participant(joinRequestDto.nickname(), 0));
		partyRepository.save(party);

		WaitingResponse waitingResponse = new WaitingResponse("waiting", room.getNickname(),
			party.getParticipants().size() - 1, room.getNumOfPeople(), LocalDateTime.now());

		party.getParticipants().forEach(participant -> {
			sendingOperations.convertAndSend("/sub/quiz/" + username + "/"+participant.getNickname(), waitingResponse);
		});
	}


	@MessageMapping("/quiz/{username}/cancel")
	public void participantCancel(@DestinationVariable String username, CancelRequestDto cancelRequestDto) {
		Room room = roomRepository.findById(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_QUIZ_ERROR));
		Party party = partyRepository.findById(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_QUIZ_ERROR));
		if (room.getNickname().equals(cancelRequestDto.nickname())) {
			roomRepository.delete(room);
			party.getParticipants().forEach(participant -> {
				sendingOperations.convertAndSend("/sub/quiz/" + username + "/"+participant.getNickname(), new CancelResponseDto("cancel", "방이 취소되었습니다."));
			});
			partyRepository.delete(party);
			quizInfoRepository.deleteById(username);
		} else {
			if (party.getParticipants().stream().noneMatch(participant -> participant.getNickname().equals(cancelRequestDto.nickname())))
				throw new BaseException(ErrorCode.NO_USER_ERROR);
			party.cancel(cancelRequestDto.nickname());
			partyRepository.save(party);
			party.getParticipants().forEach(participant -> {
				sendingOperations.convertAndSend("/sub/quiz/" + username + "/"+participant.getNickname(), new WaitingResponse("waiting", room.getNickname(), party.getParticipants().size()-1, room.getNumOfPeople(), LocalDateTime.now()));
			});
		}
	}
	@MessageMapping("/quiz/{username}/submit")
	public void submit(@DestinationVariable String username, SubmitRequestDto submitRequestDto) {
		Room curRoom=roomRepository.findById(username)
			.orElseThrow(()->new IllegalArgumentException("방이 존재하지 않습니다."));

		List<SubmitRequestDto> curSubmitInfo=new ArrayList<>();
		if(submitInfoRepository.findById(username).isPresent()){
			curSubmitInfo=submitInfoRepository.findById(username).get().getSubmits();
		}

		List<SubmitRequestDto> submitList=new ArrayList<>();
		submitList.add(submitRequestDto);

		for(SubmitRequestDto cur:curSubmitInfo){
			if(cur.nickname().equals(submitRequestDto.nickname())){
				continue;
			}
			submitList.add(cur);
		}

		submitInfoRepository.save(new SubmitInfo(username,submitList));


		SubmitResponseDto submitResponseDto=new SubmitResponseDto("submit",submitList.size(),curRoom.getNumOfPeople());

		partyRepository.findById(username).get().getParticipants().forEach(participant -> {
			sendingOperations.convertAndSend("/sub/quiz/"+username+"/"+participant.getNickname(),submitResponseDto);
		});

	}

	@MessageMapping("/quiz/{username}/grade")
	public void grade(@DestinationVariable String username) {
		Room room = roomRepository.findById(username)
			.orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));
		Party curParty = partyRepository.findById(username)
			.orElseThrow(() -> new IllegalArgumentException("파티가 존재하지 않습니다."));

		//제출목록 불러오기
		SubmitInfo submitInfo=new SubmitInfo(username,new ArrayList<>());
		if(submitInfoRepository.findById(username).isPresent())
			submitInfo=submitInfoRepository.findById(username).get();
		//현재 퀴즈 정보 불러오기
		QuizInfo quizInfo=quizInfoRepository.findById(username).get();
		List<Quiz> quizList=quizInfo.getQuizList();
		int curQuizNum=quizInfo.getCurQuizNum();
		Quiz curQuiz=quizList.get(curQuizNum-1);

		//옵션 선택수 카운팅, hostAnswer 탐색
		int option1Count=0;
		int option2Count=0;
		String hostAnswer=null;
		for(SubmitRequestDto submitRequestDto:submitInfo.getSubmits()){
			if(submitRequestDto.nickname().equals(room.getNickname())) {
				hostAnswer = submitRequestDto.answer();
				continue;
			}
			if(submitRequestDto.answer().equals(curQuiz.getOption1()))
				option1Count++;
			if(submitRequestDto.answer().equals(curQuiz.getOption2()))
				option2Count++;

		}

		//점수 갱신 후 재저장
		List<Participant> participants=curParty.getParticipants();
		List<Participant> newParticipants=new ArrayList<>();
		for(Participant cur:participants){
			String curName=cur.getNickname();
			int curScore=cur.getScore();
			for(SubmitRequestDto submitRequestDto: submitInfo.getSubmits()){
				if(submitRequestDto.nickname().equals(curName)&&submitRequestDto.answer().equals(hostAnswer))
					curScore+=100;
			}
			newParticipants.add(new Participant(curName,curScore));
		}
		partyRepository.delete(curParty);
		partyRepository.save(new Party(username,newParticipants));

		//rank 가져오기
		List<Rank> scoreResult=quizService.getResult(username);

		for(Participant toSender:curParty.getParticipants()) {
			//마지막 문제일경우
			if (curQuizNum == room.getNumOfQuiz()) {
				Rank myRank = null;
				for (Rank cur : scoreResult) {
					if (cur.getNickname().equals(toSender.getNickname())) {
						myRank = cur;
					}
				}
				ResultResponseDto resultResponseDto = new ResultResponseDto("result", myRank, scoreResult);

				sendingOperations.convertAndSend("/sub/quiz/" + username + "/" + toSender.getNickname(),
					resultResponseDto);

			} else {//마지막 문제 아닐경우
				String myAnswer = null;
				for (SubmitRequestDto submitRequestDto : submitInfo.getSubmits()) {
					if (submitRequestDto.nickname().equals(toSender.getNickname())) {
						myAnswer = submitRequestDto.answer();
						break;
					}
				}

				//참가자별 response객체 설정후 전송
				GradeResponseDto gradeResponseDto = new GradeResponseDto("grade",
					new QuizDto(curQuiz.getQuestion(),
						curQuiz.getOption1(),
						curQuiz.getOption2(),
						option1Count,
						option2Count
					),
					curQuizNum,
					room.getNumOfQuiz(),
					LocalDateTime.now().plusSeconds(30),
					hostAnswer,
					myAnswer,
					scoreResult
				);

				sendingOperations.convertAndSend("/sub/quiz/" + username + "/" + toSender.getNickname(),
					gradeResponseDto);
			}
		}


		submitInfoRepository.delete(submitInfo);
	}

	@MessageMapping("/quiz/{username}/next")
	public void next(@DestinationVariable String username, JoinRequestDto joinRequestDto) {
		
		//유저 권한 체크
		User hostUser = userRepository.findByUsername(joinRequestDto.nickname())
			.orElseThrow(()->new BaseException(ErrorCode.NO_USER_ERROR));
		if(!hostUser.getUsername().equals(username))
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		
		//방, 참여자 존재 체크
		Party curParty=partyRepository.findById(username)
			.orElseThrow(() -> new IllegalArgumentException("파티가 존재하지 않습니다."));
		Room room=roomRepository.findById(username)
			.orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));

		roomRepository.delete(room);
		roomRepository.save(new Room(room.getUserId(),curParty.getParticipants().size(), room.getNumOfQuiz(),true,room.getNickname()));

		QuizResponseDto cur=quizService.findNextQuiz(username);
		for(Participant toSender:curParty.getParticipants()){
			sendingOperations.convertAndSend("/sub/quiz/"+username+"/"+toSender.getNickname(),cur);
		}

	}


}
