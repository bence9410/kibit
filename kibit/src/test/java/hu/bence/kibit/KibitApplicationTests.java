package hu.bence.kibit;

import hu.bence.kibit.entity.Transaction;
import hu.bence.kibit.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KibitApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TestRestTemplate testRestTemplate;

	private String receivedMessage;

	@KafkaListener(topics = "notification", groupId = "foo")
	public void listenGroupFoo(String message) {
		receivedMessage = message;
	}

	@Test
	@Transactional
	public void positiveTest() {
		Transaction transaction = new Transaction();
		transaction.setAmount(1000L);
		transaction.setFromName("Bence");
		transaction.setToName("Erzsébet");

		Assertions.assertEquals(HttpStatus.OK, testRestTemplate.postForEntity("http://localhost:" + port + "/send", transaction, Void.class).getStatusCode());
		Assertions.assertEquals(9000L, accountRepository.findByName(transaction.getFromName()).get().getAmount());
		Assertions.assertEquals(2000L, accountRepository.findByName(transaction.getToName()).get().getAmount());
		Assertions.assertEquals("{\"fromName\":\"Bence\",\"toName\":\"Erzsébet\",\"amount\":1000}", receivedMessage);
	}

	@Test
	public void negativeTest() {
		Transaction transaction = new Transaction();
		transaction.setAmount(1000L);
		transaction.setFromName("Bencewrong");
		transaction.setToName("Erzsébet");

		Assertions.assertEquals(HttpStatus.BAD_REQUEST, testRestTemplate.postForEntity("http://localhost:" + port + "/send", transaction, Void.class).getStatusCode());
	}

}
