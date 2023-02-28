package com.jhchen.service;

import com.jhchen.domain.modul.Block;
import com.jhchen.domain.modul.SignedTransaction;
import com.jhchen.domain.modul.Transaction;
import com.jhchen.domain.modul.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlockChainServiceTest {
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    BlockChainService blockChainService;
    @Test
    public void signTest(){
        Account account2 = accountService.createAccount();
        Account account1 = accountService.createAccount();
        Transaction transaction = new Transaction(account1.getAddr(), account2.getAddr(), "10");

        SignedTransaction signedTransaction = transactionService.signTransaction(transaction, account1);

        List<SignedTransaction> list = new ArrayList<>();
        list.add(signedTransaction);

        String id = blockChainService.generateHash(list);
        System.out.println("hash["+id.length()+"]:"+id);

        Block block = new Block();
        block.setBody(list);block.setId(id);block.setPreID("7d3ecf4c1f5b53564c666d4e0101a124927750afc8c5aef8cd148945f8d3082f");
        Integer i = 8;
        block.setTargetBits(24);

        Integer n =blockChainService.calculateNonce(block);
        System.out.println(n);

        Boolean b = blockChainService.verifyBlock(block);
        System.out.println(b);


    }
}
