package com.jhchen.mine.service;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.*;
import com.jhchen.framework.service.TransactionService;
import com.jhchen.framework.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 挖矿节点处理交易信息
 * @createDate 2023/4/24 14:37
 * @since 1.0.0
 */
@Service
public class MineTransactionService {
    @Autowired
    TransactionService transactionService;
    @Autowired
    TransactionPool transactionPool;
    @Autowired
    Account account;
    @Autowired
    @Qualifier("accountList")
    List<Account> accountList;
    @Value("${centerAddr}")
    String centerAddr;

    /**
     * 创建一个新的交易，并广播
     * @param transaction
     * @return
     */
    public ResponseResult buildTransaction(Transaction transaction){
        System.out.println(transaction);
        //签名
        SignedTransaction signedTransaction = transactionService.signTransaction(transaction, account);
        //添加到未分配队列
        transactionPool.add(signedTransaction);
        //广播
        try {
            HttpUtil.broadcastMessage("/addTransaction",JSON.toJSONString(signedTransaction),accountList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseResult.okResult(signedTransaction);
    }

    /**
     * 接收一个未分配的新交易
     * @param transaction
     * @return
     */
    public ResponseResult addTransaction(SignedTransaction transaction){
        System.out.println(transaction);
        transactionPool.add(transaction);
        return ResponseResult.okResult();
    }

    /**
     * 接收中央分配交易的指令
     * @param transactionPoolItemList
     * @return
     */
    public ResponseResult alloc(List<TransactionPoolItem> transactionPoolItemList){
        //TODO:检查是否有中央账户的签名
        //逐个分配
        for (TransactionPoolItem transactionPoolItem : transactionPoolItemList) {
            transactionPool.allocate(transactionPoolItem.getTransaction(),transactionPoolItem.getMinerAddr());
        }
        return ResponseResult.okResult();
    }

    /**
     * 获取池中分配给自己的交易
     * @return
     */
    public Map<Integer,List<SignedTransaction>> getTrans(){
        Map<Integer,List<SignedTransaction>> map = new HashMap<>();
        List<TransactionPoolItem> res = new ArrayList<>();
        List<TransactionPoolItem> allocatedTransactionList = transactionPool.getAllocatedTransactionList();
        for (TransactionPoolItem transactionPoolItem : allocatedTransactionList) {
            if(transactionPoolItem.getMinerAddr().equals(account.getAddr())) {
                Integer height = transactionPoolItem.getHeight();
                if(map.containsKey(height)){
                    List<SignedTransaction> list = map.get(height);
                    list.add(transactionPoolItem.getTransaction());
                }
                else {
                    List<SignedTransaction> list = new LinkedList<>();
                    list.add(transactionPoolItem.getTransaction());
                    map.put(height,list);
                }
            }

        }
        return map;
    }
}
