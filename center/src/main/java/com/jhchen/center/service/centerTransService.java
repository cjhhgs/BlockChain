package com.jhchen.center.service;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import com.jhchen.framework.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

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
        Account account = waitList.poll();
        List<SignedTransaction> unallocated = transactionPool.getUnallocated(100);
        broadcastMessage("/alloc", JSON.toJSONString(unallocated));
    }

    //广播消息
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
