package com.jhchen.center.config;

import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.jhchen.framework.domain.modul.Block.genesisBlock;


@Component
public class CenterBeanInjector {
    @Bean("centerBlockChain")
    private List<Block> blockChain(){

        List<Block> blockChain = new ArrayList<Block>();
        Block block = genesisBlock();
        blockChain.add(block);
        return blockChain;
    }
    @Bean("transactionPool")
    private TransactionPool transactionPool1(){
        return new TransactionPool();
    }

    @Bean("waitList")//等待队列
    private Queue<Account> waitList(){
        return new LinkedList<>();
    }

    @Bean("transactionList")
    private List<SignedTransaction> transactionList(){
        return new ArrayList<SignedTransaction>();
    }
    @Bean("AccountList")
    private List<Account> accountList(){
        return new ArrayList<Account>();
    }

}
