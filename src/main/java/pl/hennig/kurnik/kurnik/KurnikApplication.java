package pl.hennig.kurnik.kurnik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.hennig.kurnik.kurnik.model.ChatMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@SpringBootApplication(scanBasePackages = {"pl.hennig.kurnik.kurnik", "org.springframework.security.config.annotation.web.builders"})
public class KurnikApplication {
    public static void main(String[] args) {
        SpringApplication.run(KurnikApplication.class, args);
    }

    @Bean
    Flux<ChatMessage> messages(UnicastProcessor<ChatMessage> messageDistributor) {
        return messageDistributor.replay(50).autoConnect();
    }

    /**
     * @return UnicastProcessor to be used for distributing new chat messages to
     * all participants.
     */
    @Bean
    UnicastProcessor<ChatMessage> publisher() {
        return UnicastProcessor.create();
    }

}