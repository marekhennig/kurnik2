package pl.hennig.kurnik.kurnik.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.hennig.kurnik.kurnik.model.Token;

import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token,Long> {
    Optional<Token> findTokenByToken(String token);
}

