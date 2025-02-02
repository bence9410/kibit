package hu.bence.kibit;

import hu.bence.kibit.entity.Account;
import hu.bence.kibit.entity.Transaction;
import hu.bence.kibit.exception.BadRequestException;
import hu.bence.kibit.repository.AccountRepository;
import hu.bence.kibit.repository.NotificationRepository;
import hu.bence.kibit.repository.TransactionRepository;
import hu.bence.kibit.service.TransactionService;
import jakarta.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class TransactionServiceUnitTests {

    private TransactionService transactionService;

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        notificationRepository = Mockito.mock(NotificationRepository.class);

        transactionService = new TransactionService(accountRepository, transactionRepository, notificationRepository);
    }

    @Test
    public void successfulTransactionTest() {
        Transaction transaction = new Transaction();
        transaction.setAmount(1000L);
        transaction.setFromName("Bence");
        transaction.setToName("Erzsébet");

        Account fromAccount = new Account();
        fromAccount.setName("Bence");
        fromAccount.setAmount(1000L);

        Account toAccount = new Account();
        toAccount.setName("Erzsébet");
        toAccount.setAmount(0L);

        Mockito.when(accountRepository.findByName(fromAccount.getName())).thenReturn(Optional.of(fromAccount));
        Mockito.when(accountRepository.findByName(toAccount.getName())).thenReturn(Optional.of(toAccount));

        transactionService.send(transaction);

        Mockito.verify(accountRepository).findByName(fromAccount.getName());
        Mockito.verify(accountRepository).findByName(toAccount.getName());
        Mockito.verify(transactionRepository).save(transaction);
        Mockito.verify(notificationRepository).send(transaction);
        Mockito.verifyNoMoreInteractions(accountRepository, transactionRepository, notificationRepository);
    }

    @Test
    public void fromAccountNotFound() {
        Transaction transaction = new Transaction();
        transaction.setAmount(1000L);
        transaction.setFromName("Bence");
        transaction.setToName("Erzsébet");

        Assertions.assertThatThrownBy(() -> transactionService.send(transaction))
                .isInstanceOf(BadRequestException.class).hasMessage("No account found for Bence!");

        Mockito.verify(accountRepository).findByName(transaction.getFromName());
        Mockito.verifyNoMoreInteractions(accountRepository, transactionRepository, notificationRepository);
    }

    @Test
    public void notEnoughMoney() {
        Transaction transaction = new Transaction();
        transaction.setAmount(1000L);
        transaction.setFromName("Bence");
        transaction.setToName("Erzsébet");

        Account fromAccount = new Account();
        fromAccount.setName("Bence");
        fromAccount.setAmount(0L);

        Mockito.when(accountRepository.findByName(fromAccount.getName())).thenReturn(Optional.of(fromAccount));

        Assertions.assertThatThrownBy(() -> transactionService.send(transaction))
                .isInstanceOf(BadRequestException.class).hasMessage("Not enough money!");

        Mockito.verify(accountRepository).findByName(transaction.getFromName());
        Mockito.verifyNoMoreInteractions(accountRepository, transactionRepository, notificationRepository);
    }

    @Test
    public void toAccountNotFound() {
        Transaction transaction = new Transaction();
        transaction.setAmount(1000L);
        transaction.setFromName("Bence");
        transaction.setToName("Erzsébet");

        Account fromAccount = new Account();
        fromAccount.setName("Bence");
        fromAccount.setAmount(1000L);

        Mockito.when(accountRepository.findByName(fromAccount.getName())).thenReturn(Optional.of(fromAccount));

        Assertions.assertThatThrownBy(() -> transactionService.send(transaction))
                .isInstanceOf(BadRequestException.class).hasMessage("No account found for Erzsébet!");

        Mockito.verify(accountRepository).findByName(transaction.getFromName());
        Mockito.verify(accountRepository).findByName(transaction.getToName());
        Mockito.verifyNoMoreInteractions(accountRepository, transactionRepository, notificationRepository);
    }
}
