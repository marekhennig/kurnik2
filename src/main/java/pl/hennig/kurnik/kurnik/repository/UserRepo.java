package pl.hennig.kurnik.kurnik.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.hennig.kurnik.kurnik.model.User;
import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
 }
