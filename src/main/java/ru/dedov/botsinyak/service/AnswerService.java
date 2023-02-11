package ru.dedov.botsinyak.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dedov.botsinyak.model.Answer;
import ru.dedov.botsinyak.repository.AnswerRepository;

@Slf4j
@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Transactional
    public void saveAnswer(Answer answer){
        answerRepository.saveAndFlush(answer);
    }
}
