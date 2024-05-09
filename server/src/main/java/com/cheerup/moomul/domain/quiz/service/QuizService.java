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
import com.cheerup.moomul.domain.quiz.entity.Rank;
import com.cheerup.moomul.domain.quiz.entity.Room;
import com.cheerup.moomul.domain.quiz.repository.PartyRepository;
import com.cheerup.moomul.domain.quiz.repository.QuizInfoRepository;
import com.cheerup.moomul.domain.quiz.repository.QuizRepository;
import com.cheerup.moomul.domain.quiz.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuizService {

	private final QuizRepository quizRepository;
	private final QuizInfoRepository quizInfoRepository;
	private final RoomRepository roomRepository;
	private final PartyRepository partyRepository;

	public List<Quiz> findRandomQuiz(int limit) {
		return quizRepository.findRandomQuiz(limit);
	}

	public QuizResponseDto findNextQuiz(String username) {
		QuizInfo quizInfo=quizInfoRepository.findById(username).get();

		int curQuizNum=quizInfo.getCurQuizNum()-1;
		Room curRoom=roomRepository.findById(username).get();
		int numOfQuiz=curRoom.getNumOfQuiz();
		Quiz curQuiz=quizInfo.getQuizList().get(curQuizNum);
		NextQuizDto nextQuiz=new NextQuizDto(curQuiz.getQuestion(), curQuiz.getOption1(), curQuiz.getOption2());

		quizInfoRepository.delete(quizInfo);
		quizInfoRepository.save(new QuizInfo(quizInfo.getUserId(), quizInfo.getCurQuizNum()+1,quizInfo.getQuizList()));

		return new QuizResponseDto("nextQuiz", nextQuiz, quizInfo.getCurQuizNum(), numOfQuiz, LocalDateTime.now().plusSeconds(30L));
	}

	public List<Rank> getResult(String username) {
		List<Participant> curParty=partyRepository.findById(username).get().getParticipants();

		curParty.sort((o1,o2)->Integer.compare(o2.getScore(),o1.getScore()));

		List<Rank> curRank=new ArrayList<>();
		int idx=1;
		for(Participant cur:curParty) {
			if(cur.getNickname().equals(username))
				continue;
			curRank.add(new Rank(idx++,cur.getNickname(),cur.getScore()));
		}

		return curRank;
	}
}
