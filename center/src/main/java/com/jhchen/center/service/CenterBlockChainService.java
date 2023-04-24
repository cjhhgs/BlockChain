package com.jhchen.center.service;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import com.jhchen.framework.service.Block.BlockVerifyService;
import com.jhchen.framework.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CenterBlockChainService {
    @Autowired
    private List<SignedTransaction> transactionList;
    @Autowired
    private TransactionPool transactionPool;
    @Autowired
    private  List<Account> accountList;
    @Autowired
    private List<Block> blockChain;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BlockVerifyService blockVerifyService;

    @Value("${targetBits}")
    private String targetBits;


    /**
     * 接受一个新的区块并验证
     * @param block
     * @return
     */
    public ResponseResult addBlock(Block block){
        ResponseResult responseResult = blockVerifyService.addBlock(block, targetBits, blockChain, transactionPool);
        return responseResult;
    }

}
