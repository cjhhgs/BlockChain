package com.jhchen.runner;

import com.jhchen.domain.modul.Block;
import com.jhchen.domain.modul.SignedTransaction;
import com.jhchen.domain.modul.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.jhchen.domain.modul.Block.genesisBlock;

@Component
public class BeanInjector {
    @Bean("blockChain")
    private List<Block> blockChain(){

        List<Block> blockChain = new ArrayList<Block>();
        Block block = genesisBlock();
        blockChain.add(block);
        return blockChain;
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
