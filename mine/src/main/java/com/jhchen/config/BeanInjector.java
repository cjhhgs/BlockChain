package com.jhchen.config;

import com.jhchen.domain.modul.*;
import com.jhchen.service.MineService;
import com.jhchen.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.jhchen.domain.modul.Block.genesisBlock;

@Configuration
public class BeanInjector {
    @Value("${accountPath}$")
    String accountPath;
    @Autowired
    MineService mineService;
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
