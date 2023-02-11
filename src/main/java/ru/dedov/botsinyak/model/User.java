package ru.dedov.botsinyak.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Answer> answers;
}
