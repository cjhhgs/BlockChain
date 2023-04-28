package com.jhchen.center.service;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import com.jhchen.framework.domain.modul.TransactionPoolItem;
import com.jhchen.framework.service.TransactionService;
import com.jhchen.framework.utils.HttpUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class CenterTransService {
    @Autowired
    TransactionPool transactionPool;
    @Autowired
    @Qualifier("accountList")
    List<Account> accountList;
    @Autowired
    @Qualifier("waitList")
    Queue<Account> waitList;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private Integer height;

    /**
     * 添加新的交易记录
     * @param transaction
     * @return
     */
    public ResponseResult addTransaction(SignedTransaction transaction){
        if(transactionService.verify(transaction).equals(false)){
            return ResponseResult.errorResult(AppHttpCodeEnum.SIGN_NOT_VERIFY);
        }
        transactionPool.add(transaction);
        try {
            HttpUtil.broadcastMessage("/addTransaction",JSON.toJSONString(transaction),accountList);
        } catch (IOException e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
        }
        return ResponseResult.okResult();
    }

    /**
     * 分配并广播消息
     */
    public ResponseResult allocate(){
        //获取队列中第一个账户
        Account tarAccount = waitList.poll();
        //取出未分配的交易，默认2条
        List<SignedTransaction> unallocated = transactionPool.getUnallocated(2);
        // 包装成已分配交易
        List<TransactionPoolItem> collect = unallocated.stream().map(i -> {
            TransactionPoolItem transactionPoolItem = new TransactionPoolItem(i, tarAccount.getAddr());
            //设置层数
            transactionPoolItem.setHeight(height);
            return transactionPoolItem;
        }).collect(Collectors.toList());
        height++;
        try {
            HttpUtil.broadcastMessage("/alloc", JSON.toJSONString(collect),accountList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        unallocated.stream().forEach(i -> transactionPool.allocate(i, tarAccount.getAddr()));
        return ResponseResult.okResult();
    }


}
