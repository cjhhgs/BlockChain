package com.jhchen.center.runner;

import com.jhchen.center.service.CenterBlockChainService;
import com.jhchen.center.service.CenterTransService;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.TransactionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description
 * @createDate 2023/5/26 10:52
 * @since 1.0.0
 */
@Component
public class Task {
    @Autowired
    @Qualifier("waitList")
    private Queue<Account> waitList;
    @Autowired
    @Qualifier("transactionPool")
    TransactionPool transactionPool;
    @Autowired
    @Qualifier("blockChain")
    private List<Block> blockChain;
    @Autowired
    @Qualifier("ackList")
    private List<Integer> ackList;
    @Autowired
    @Qualifier("accountList")
    private List<Account> accountList;
    @Autowired
    private CenterBlockChainService centerBlockChainService;
    @Autowired
    CenterTransService centerTransService;

    @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次
    public void execute_time(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置日期格式
        System.out.println("检测区块ack情况 " + df.format(new Date()));

        for (Block block : blockChain) {

            if(block==null||block.getHeight()==0){
                continue;
            }

            if(ackList.get(block.getHeight())==null){
                System.out.println("区块"+block.getHeight()+"未收到ack");
            } else if (ackList.get(block.getHeight()) < accountList.size()/2) {
                System.out.println("区块"+block.getHeight()+"收到ack数不足");

            } else {
            }
        }
    }

    public void execute(Block block){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置日期格式

        if(ackList.get(block.getHeight())==null){
            System.out.println("区块"+block.getHeight()+"未收到ack");
        } else if (ackList.get(block.getHeight()) < accountList.size()/2-1) {
        } else {
            System.out.println("区块"+block.getHeight()+"收到ack数足够"+df.format(new Date()));
            centerBlockChainService.finishBlock(block,new Date());
        }

    }

    @Async
    public void executeAlloc(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置日期格式
        int i = 0;
        while(true){
            if(!waitList.isEmpty() && !transactionPool.transactionList.isEmpty()){
                i++;
                centerTransService.allocate();
                System.out.println("allocate "+ i + " " + df.format(new Date()));
            }

        }
    }
}
