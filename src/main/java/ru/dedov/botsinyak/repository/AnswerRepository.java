package ru.dedov.botsinyak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dedov.botsinyak.model.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
