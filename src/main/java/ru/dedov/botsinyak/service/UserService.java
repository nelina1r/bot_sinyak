package ru.dedov.botsinyak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dedov.botsinyak.model.User;
import ru.dedov.botsinyak.repository.UserRepository;

import java.util.HashSet;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User findUserById(Long id){
        return userRepository.getReferenceById(id);
    }

    @Transactional
    public void saveUser(User user){
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void createNewUserIfNotExists(Long id, String username) {
        if (userRepository.existsById(id))
            return;
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void deleteAllUserAnswers(Long id) {
        User user = userRepository.getReferenceById(id);
        user.setAnswers(new HashSet<>());
        userRepository.saveAndFlush(user);
    }
}
