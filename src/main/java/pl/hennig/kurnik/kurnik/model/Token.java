package pl.hennig.kurnik.kurnik.model;

import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
public class Token {
    public enum Purpose {
        REGISTRATION, FORGOT
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @ManyToOne
    private User user;


    private String token;
    private LocalDateTime creationTime;
    private LocalDateTime expTime;
    private Purpose purpose;

    public Token(User user, LocalDateTime creationTime, LocalDateTime expTime, Purpose purpose) {
        this.user = user;
        this.creationTime = creationTime;
        this.expTime = expTime;
        this.purpose = purpose;
    }

    public Token() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getExpTime() {
        return expTime;
    }

    public void setExpTime(LocalDateTime expTime) {
        this.expTime = expTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", user=" + user +
                ", token='" + token + '\'' +
                ", creationTime=" + creationTime +
                ", expTime=" + expTime +
                '}';
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public void generateToken(){
        int length = 32;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        setToken(generatedString);
    }
}
