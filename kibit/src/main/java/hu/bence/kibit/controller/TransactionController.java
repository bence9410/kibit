package hu.bence.kibit.controller;

import hu.bence.kibit.entity.Transaction;
import hu.bence.kibit.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Send money from one account to another")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful transaction"),
            @ApiResponse(responseCode = "400", description = "Account not found or not enough money")})
    @PostMapping("/send")
    public void send(@RequestBody Transaction transaction) {
        transactionService.send(transaction);
    }

}
