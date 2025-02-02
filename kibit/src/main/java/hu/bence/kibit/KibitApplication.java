package hu.bence.kibit;

import hu.bence.kibit.entity.Account;
import hu.bence.kibit.entity.Transaction;
import hu.bence.kibit.repository.AccountRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class KibitApplication {

    public static void main(String[] args) {
        SpringApplication.run(KibitApplication.class, args);
    }

    @Bean
    NewTopic notification() {
        return new NewTopic("notification", 1, (short) 1);
    }

    @Bean
    ApplicationRunner applicationRunner(AccountRepository accountRepository) {
        return args -> {
            if (accountRepository.count() == 0L) {
                Account bence = new Account();
                bence.setName("Bence");
                bence.setAmount(10000L);

                Account erzsebet = new Account();
                erzsebet.setName("Erzsébet");
                erzsebet.setAmount(1000L);

                accountRepository.save(bence);
                accountRepository.save(erzsebet);
            }


        };
    }
}
