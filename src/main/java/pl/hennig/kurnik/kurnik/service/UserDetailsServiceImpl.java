package pl.hennig.kurnik.kurnik.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.hennig.kurnik.kurnik.model.User;
import pl.hennig.kurnik.kurnik.repository.UserRepo;

import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepo userRepo;


    public UserDetailsServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> username = userRepo.findUserByUsername(s);
        if (username.isPresent()) {
            return username.get();
        }
        Optional<User> email = userRepo.findUserByEmail(s);
        if (email.isPresent()) {
            return email.get();
        }
        throw new UsernameNotFoundException("User not found");
    }


}
