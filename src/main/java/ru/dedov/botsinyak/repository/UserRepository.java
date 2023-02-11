package ru.dedov.botsinyak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dedov.botsinyak.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
