package com.jhchen.mine.controller;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.*;
import com.jhchen.framework.service.AccountService;
import com.jhchen.framework.service.TransactionService;
import com.jhchen.framework.utils.HttpUtil;
import com.jhchen.mine.service.MineService;
import com.jhchen.mine.service.MineTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@Api(tags = "交易相关api")
public class MineTransactionController {
    @Autowired
    MineService mineService;
    @Autowired
    MineTransactionService mineTransactionService;
    @Autowired
    Account account;
    @Autowired
    TransactionPool transactionPool;

    @GetMapping("/showTransactions")
    @ApiOperation(value = "查看目前交易列表")
    public ResponseResult transactions(){
        return ResponseResult.okResult(transactionPool);
    }

    @PostMapping("/updateTrans")
    @ApiOperation("更新交易池")
    public ResponseResult updateTrans(@RequestBody TransactionPool transactionPoolUpdate){
        transactionPool = transactionPoolUpdate;
        return ResponseResult.okResult(transactionPool);
    }
    @GetMapping("/getTransactions")
    @ApiOperation(value = "从中心获取交易列表")
    public ResponseResult getTransactions(){
        return mineService.getTransactions();
    }

    @PostMapping("/createTransaction")
    @ApiOperation(value = "创建交易")
    @ApiParam(name = "transaction")
    public ResponseResult createTransaction(@RequestBody Transaction transaction){
        return mineTransactionService.buildTransaction(transaction);
    }

    @PostMapping("/addTransaction")
    @ApiOperation(value = "接受一个新交易")
    @ApiParam(name = "transaction")
    public ResponseResult addTransaction(@RequestBody SignedTransaction signedTransaction){
        return mineTransactionService.addTransaction(signedTransaction);
    }

    @PostMapping("/alloc")
    @ApiOperation(value = "交易分配指令")
    @ApiParam(name = "transactionPoolItemList")
    public ResponseResult alloc(@RequestBody List<TransactionPoolItem> transactionPoolItemList){
        return mineTransactionService.alloc(transactionPoolItemList);
    }
}
