package com.jhchen.mine.config;

import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
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

    @Bean("transactionPool")//交易池
    public TransactionPool transactionPool(){return new TransactionPool();}

    @Value("${server.port}")
    String port;

    @Bean("account")//本地账户
    public Account userAccount(){
        Account account = new Account();
        account.setIp("http://localhost:"+port);
        return account;

    }

    @Bean("accountList")//网络所有节点
    public List<Account> accountList(){return new ArrayList<>();}

}
