package com.jhchen.runner;

import com.alibaba.fastjson.JSON;
import com.jhchen.domain.modul.Account;
import com.jhchen.domain.modul.Block;
import com.jhchen.service.MineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlockChainRunner implements CommandLineRunner {
    @Autowired
    private List<Block> blockChain;
    @Autowired
    MineService mineService;
    @Autowired
    Account account;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("加载本地账户：");
        mineService.loadAccount();
        System.out.println(JSON.toJSONString(account));
        System.out.println("获取交易列表");
        mineService.getTransactions();
        System.out.println("获取区块链");
        mineService.getBlockChainFromCenter();

    }
}
