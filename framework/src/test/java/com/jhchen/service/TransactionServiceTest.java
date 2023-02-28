package com.jhchen.service;

import com.jhchen.domain.modul.SignedTransaction;
import com.jhchen.domain.modul.Transaction;
import com.jhchen.domain.modul.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TransactionServiceTest {
    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountService accountService;

    @Test
    public void signTest(){
        Account account2 = accountService.createAccount();
        Account account1 = accountService.createAccount();
        Transaction transaction = new Transaction(account1.getAddr(), account2.getAddr(), "10");
        System.out.println(transaction.toString());

        SignedTransaction signedTransaction = transactionService.signTransaction(transaction, account1);
        System.out.println(signedTransaction.toString());

        System.out.println(transactionService.verify(signedTransaction));

    }
}
