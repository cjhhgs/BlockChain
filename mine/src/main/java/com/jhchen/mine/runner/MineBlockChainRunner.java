package com.jhchen.mine.runner;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.mine.service.MineService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MineBlockChainRunner implements CommandLineRunner {
    @Autowired
    private List<Block> blockChain;
    @Autowired
    MineService mineService;
    @Autowired
    Account account;
    @Autowired
    @Qualifier("accountList")
    List<Account> accountList;
    @Value("${centerAddr}")
    private String centerAddr;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("加载本地账户：");
        mineService.loadAccount();
        System.out.println(JSON.toJSONString(account));
        System.out.println("获取交易列表");
        mineService.getTransactions();
        System.out.println("获取区块链");
        mineService.getBlockChainFromCenter();
        System.out.println("加载中央节点ip");
        accountList.add(new Account(null,null,null,centerAddr));
        System.out.println("finish init");

    }
}
