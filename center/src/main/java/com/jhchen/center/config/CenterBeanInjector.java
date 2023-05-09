package com.jhchen.center.config;

import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import io.swagger.models.auth.In;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.jhchen.framework.domain.modul.Block.genesisBlock;


@Component
public class CenterBeanInjector {
    @Bean("blockChain")
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
    @Bean("accountList")//全网节点
    private List<Account> accountList(){
        return new ArrayList<Account>();
    }
    @Bean("height")
    private Integer height(){return new Integer(1);}
    @Bean("idList")
    private List<String> idList(){
        List<String> idList = new ArrayList<>();
        idList.add("0");
        return idList;
    }

    @Bean("ackList")
    private List<Integer> ackList(){
        List<Integer> ack = new ArrayList<>();
        ack.add(1);
        return ack;
    }

}
