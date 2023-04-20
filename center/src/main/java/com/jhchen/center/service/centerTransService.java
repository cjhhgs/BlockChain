package com.jhchen.center.service;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import com.jhchen.framework.domain.modul.TransactionPoolItem;
import com.jhchen.framework.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 中央节点处理交易池
 * @createDate 2023/4/19 16:50
 * @since 1.0.0
 */
@Service
public class centerTransService {
    @Autowired
    TransactionPool transactionPool;
    @Autowired
    Queue<Account> waitList;
    @Autowired
    List<Account> accountList;

    //分配并广播消息
    public void allocate(){
        //获取队列中第一个账户
        Account account = waitList.poll();
        //取出未分配的交易，默认2条
        List<SignedTransaction> unallocated = transactionPool.getUnallocated(2);
        // 包装成已分配交易
        List<TransactionPoolItem> collect = unallocated.stream().map(i -> new TransactionPoolItem(i, account.getAddr())).collect(Collectors.toList());
        broadcastMessage("/alloc", JSON.toJSONString(collect));
        unallocated.stream().forEach(i -> transactionPool.allocate(i, account.getAddr()));
    }

    /**
     * 向所有结点广播消息
     * @param route
     * @param data
     */
    public void broadcastMessage(String route, String data){
        for (Account account : accountList) {
            String ip = account.getIp();
            if(ip!=null){
                try {
                    HttpUtil.post(ip+route,data);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
