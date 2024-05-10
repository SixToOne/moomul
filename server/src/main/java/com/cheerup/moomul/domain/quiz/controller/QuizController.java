package com.cheerup.moomul.domain.quiz.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

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
import com.cheerup.moomul.domain.quiz.dto.ResultResponseDto;
import com.cheerup.moomul.domain.quiz.dto.SubmitRequestDto;
import com.cheerup.moomul.domain.quiz.entity.Participant;
import com.cheerup.moomul.domain.quiz.entity.Quiz;
import com.cheerup.moomul.domain.quiz.entity.QuizInfo;
import com.cheerup.moomul.domain.quiz.dto.RankDto;
import com.cheerup.moomul.domain.quiz.entity.Room;
import com.cheerup.moomul.domain.quiz.entity.SubmitInfo;
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
	private final QuizInfoRepository quizInfoRepository;
	private final QuizService quizService;
	private final UserRepository userRepository;
	private final SubmitInfoRepository submitInfoRepository;

	@MessageMapping("/quiz/{username}/create")
	public void create(@DestinationVariable String username, CreateRequestDto createRequestDto) {

		//db 유저 체크
		userRepository.findByUsername(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		//유저 권한 체크
		if (!username.equals(createRequestDto.nickname()))
			throw new BaseException(ErrorCode.NO_AUTHORITY);

		//방 생성
		Room room = Room.builder()
			.numOfPeople(createRequestDto.numOfPeople())
			.numOfQuiz(createRequestDto.numOfQuiz())
			.username(username)
			.build();
		roomRepository.save(room);

		//퀴즈 정보 생성
		QuizInfo quizInfo = QuizInfo.builder()
			.username(username)
			.quizList(quizRepository.findRandomQuiz(createRequestDto.numOfQuiz()))
			.build();
		quizInfoRepository.save(quizInfo);

		//응답
		WaitingResponse waitingResponse = new WaitingResponse("waiting", room.getUsername(),
			room.getNumOfParticipants(), room.getNumOfPeople(), LocalDateTime.now().plusMinutes(10L));

		//메시지 전송
		sendMessageToAll(room, waitingResponse);
	}

	@MessageMapping("/quiz/{username}/join")
	public void join(@DestinationVariable String username, JoinRequestDto joinRequestDto) {

		//방 존재 체크
		Room room = roomRepository.findById(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_QUIZ_ERROR));

		//방 시작 체크
		if (room.isStarted()) {
			throw new BaseException(ErrorCode.GAME_STARTED);
		}

		//방 만원 체크
		if (room.getNumOfPeople() == room.getNumOfParticipants()) {
			throw new BaseException(ErrorCode.FULL_ROOM);
		}

		//중복 닉네임 체크
		if (room.duplicatedNickname(joinRequestDto.nickname()))
			throw new BaseException(ErrorCode.DUPLICATED_NICKNAME);

		//참여
		room.join(new Participant(joinRequestDto.nickname(), 0));
		roomRepository.save(room);

		//응답
		WaitingResponse waitingResponse = new WaitingResponse("waiting", room.getUsername(),
			room.getNumOfParticipants(), room.getNumOfPeople(), LocalDateTime.now());

		sendMessageToAll(room, waitingResponse);
	}

	@MessageMapping("/quiz/{username}/cancel")
	public void participantCancel(@DestinationVariable String username, CancelRequestDto cancelRequestDto) {

		//방 존재 체크
		Room room = roomRepository.findById(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_QUIZ_ERROR));

		//방 host 분기
		if (room.isHost(cancelRequestDto.nickname())) {
			//삭제
			roomRepository.delete(room);

			//응답
			CancelResponseDto cancel = new CancelResponseDto("cancel", "방이 취소되었습니다.");
			sendMessageToAll(room, cancel);
			quizInfoRepository.deleteById(username);
		} else {
			//삭제
			if (room.deleteParticipant(cancelRequestDto.nickname())) {
				throw new BaseException(ErrorCode.NO_USER_ERROR);
			}
			roomRepository.save(room);

			//응답
			WaitingResponse waitingResponse = new WaitingResponse("waiting", room.getUsername(),
				room.getNumOfParticipants(),
				room.getNumOfPeople(), LocalDateTime.now());
			sendMessageToAll(room, waitingResponse);
		}
	}

	@MessageMapping("/quiz/{username}/submit")
	public void submit(@DestinationVariable String username, SubmitRequestDto submitRequestDto) {

		//방 존재 체크
		Room room = roomRepository.findById(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_QUIZ_ERROR));

		//제출 저장 체크
		SubmitInfo submitInfo = submitInfoRepository.findById(username)
			.orElse(SubmitInfo.builder().username(username).hostSubmit(new SubmitRequestDto(username, null)).build());
		submitInfoRepository.save(submitInfo);

		//제출
		if(room.isHost(submitRequestDto.nickname())) {
			submitInfo.setHostSubmit(submitRequestDto);
		}else{
			submitInfo.addSubmit(submitRequestDto);
			SubmitResponseDto submitResponseDto = new SubmitResponseDto("submit", submitInfo.getSubmitSize(),
				room.getNumOfPeople());

			sendMessageToAll(room, submitResponseDto);
		}
		submitInfoRepository.save(submitInfo);

	}

	@MessageMapping("/quiz/{username}/grade")
	public void grade(@DestinationVariable String username) {

		Room room = roomRepository.findById(username)
			.orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));

		List<Participant> curParty = room.getParticipants();

		//제출목록 불러오기
		SubmitInfo submitInfo = submitInfoRepository.findById(username)
			.orElse(SubmitInfo.builder()
				.username(username)
				.submits(new ArrayList<>())
				.hostSubmit(new SubmitRequestDto(username,null))
				.build());

		//현재 퀴즈 정보 불러오기
		QuizInfo quizInfo = quizInfoRepository.findById(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_QUIZ_ERROR));
		List<Quiz> quizList = quizInfo.getQuizList();
		int curQuizNum = quizInfo.getCurQuizNum();
		Quiz curQuiz = quizList.get(curQuizNum - 1);

		//옵션 선택수 카운팅, hostAnswer 탐색
		int option1Count = 0;
		int option2Count = 0;
		for (SubmitRequestDto submitRequestDto : submitInfo.getSubmits()) {
			if (submitRequestDto.answer().equals(curQuiz.getOption1()))
				option1Count++;
			if (submitRequestDto.answer().equals(curQuiz.getOption2()))
				option2Count++;
		}

		String hostAnswer = submitInfo.getHostSubmit().answer();

		//점수 갱신 후 재저장
		List<Participant> newParticipants = new ArrayList<>();
		for (Participant cur : curParty) {
			String curName = cur.getNickname();
			int curScore = cur.getScore();
			for (SubmitRequestDto submitRequestDto : submitInfo.getSubmits()) {
				if (submitRequestDto.equalNickname(curName) && submitRequestDto.equalAnswer(hostAnswer))
					curScore += 100;
			}
			newParticipants.add(new Participant(curName, curScore));
		}

		roomRepository.save(
			new Room(room.getUsername(),
				room.getNumOfPeople(),
				room.getNumOfQuiz(),
				room.isStarted(),
				newParticipants));

		//rank 가져오기
		List<RankDto> scoreResult = quizService.getResult(username);

		for (Participant toSender : curParty) {

			//마지막 문제일경우
			if (curQuizNum == room.getNumOfQuiz()) {
				RankDto myRankDto = null;
				for (RankDto cur : scoreResult) {
					if (cur.getNickname().equals(toSender.getNickname())) {
						myRankDto = cur;
					}
				}
				ResultResponseDto resultResponseDto = new ResultResponseDto("result", myRankDto, scoreResult);

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
		sendingOperations.convertAndSend("/sub/quiz/" + username + "/" + username,
			new GradeResponseDto("grade",
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
				hostAnswer,
				scoreResult
			));

		submitInfoRepository.delete(submitInfo);
	}

	@MessageMapping("/quiz/{username}/next")
	public void next(@DestinationVariable String username, JoinRequestDto joinRequestDto) {

		//유저 권한 체크
		Room room = roomRepository.findById(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));
		if (!room.isHost(joinRequestDto.nickname()))
			throw new BaseException(ErrorCode.NO_AUTHORITY);

		QuizResponseDto cur = quizService.findNextQuiz(username);
		sendMessageToAll(room, cur);


	}

	private void sendMessageToAll(Room room, Object message) {
		//host한테 메세지
		sendingOperations.convertAndSend("/sub/quiz/" + room.getUsername() + "/" + room.getUsername(), message);

		//전체한테 메세지
		room.getParticipants()
			.forEach(participant -> sendingOperations.convertAndSend("/sub/quiz/" + room.getUsername() + "/" + participant.getNickname(), message));
	}

}
