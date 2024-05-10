package com.cheerup.moomul.domain.quiz.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheerup.moomul.domain.quiz.dto.NextQuizDto;
import com.cheerup.moomul.domain.quiz.dto.QuizResponseDto;
import com.cheerup.moomul.domain.quiz.entity.Participant;
import com.cheerup.moomul.domain.quiz.entity.Quiz;
import com.cheerup.moomul.domain.quiz.entity.QuizInfo;
import com.cheerup.moomul.domain.quiz.dto.RankDto;
import com.cheerup.moomul.domain.quiz.entity.Room;
import com.cheerup.moomul.domain.quiz.repository.QuizInfoRepository;
import com.cheerup.moomul.domain.quiz.repository.RoomRepository;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuizService {

	private final QuizInfoRepository quizInfoRepository;
	private final RoomRepository roomRepository;


	public QuizResponseDto findNextQuiz(String username) {
		QuizInfo quizInfo=quizInfoRepository.findById(username)
			.orElseThrow(()->new BaseException(ErrorCode.NO_QUIZ_ERROR));

		int curQuizNum=quizInfo.getCurQuizNum();
		Room curRoom = roomRepository.findById(username)
			.orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));

		int numOfQuiz=curRoom.getNumOfQuiz();
		if(curQuizNum>=numOfQuiz){
			throw new BaseException(ErrorCode.NO_MORE_QUIZ);
		}

		Quiz curQuiz=quizInfo.getQuizList().get(curQuizNum);
		NextQuizDto nextQuiz=new NextQuizDto(curQuiz.getQuestion(), curQuiz.getOption1(), curQuiz.getOption2());

		quizInfoRepository.save(
			new QuizInfo(quizInfo.getUsername(),
				quizInfo.getCurQuizNum()+1,
				quizInfo.getQuizList()));

		return new QuizResponseDto("nextQuiz", nextQuiz, quizInfo.getCurQuizNum()+1, numOfQuiz, LocalDateTime.now().plusSeconds(30L));
	}

	public List<RankDto> getResult(String username) {
		Room room = roomRepository.findById(username)
			.orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));
		List<Participant> curParty=room.getParticipants();

		curParty.sort((o1,o2)->Integer.compare(o2.getScore(),o1.getScore()));

		List<RankDto> curRankDto =new ArrayList<>();
		int idx=1;
		for(Participant cur:curParty) {
			if(cur.getNickname().equals(username))
				continue;
			curRankDto.add(new RankDto(idx++,cur.getNickname(),cur.getScore()));
		}

		return curRankDto;
	}
}
