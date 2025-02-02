package hu.bence.kibit.service;

import hu.bence.kibit.entity.Account;
import hu.bence.kibit.entity.Transaction;
import hu.bence.kibit.exception.BadRequestException;
import hu.bence.kibit.repository.AccountRepository;
import hu.bence.kibit.repository.NotificationRepository;
import hu.bence.kibit.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository, NotificationRepository notificationRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.notificationRepository = notificationRepository;
    }

    public void send(Transaction transaction) {
        Account fromAccount = accountRepository.findByName(transaction.getFromName())
                .orElseThrow(() -> new BadRequestException("No account found for " + transaction.getFromName() + "!"));
        if (fromAccount.getAmount() < transaction.getAmount()) {
            throw new BadRequestException("Not enough money!");
        }
        Account toAccount = accountRepository.findByName(transaction.getToName())
                .orElseThrow(() -> new BadRequestException("No account found for " + transaction.getToName() + "!"));
        fromAccount.setAmount(fromAccount.getAmount() - transaction.getAmount());
        toAccount.setAmount(toAccount.getAmount() + transaction.getAmount());
        transactionRepository.save(transaction);
        notificationRepository.send(transaction);
    }

}
