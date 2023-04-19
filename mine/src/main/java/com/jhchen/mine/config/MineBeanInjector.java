package com.jhchen.mine.config;

import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.SignedTransaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static com.jhchen.framework.domain.modul.Block.genesisBlock;


@Configuration
public class MineBeanInjector {
    @Value("${accountPath}$")
    String accountPath;

    @Bean("blockChain")
    public List<Block> blockChain(){
        List<Block> blockChain =  new ArrayList<Block>();
        Block block = genesisBlock();
        blockChain.add(block);
        return blockChain;
    }

    @Bean("transactionList")
    public List<SignedTransaction> transactionList(){
        return new ArrayList<SignedTransaction>();
    }

    @Bean("account")
    public Account userAccount(){
        return new Account();
    }

}
