package com.jhchen.mine.controller;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.Transaction;
import com.jhchen.framework.service.AccountService;
import com.jhchen.framework.service.TransactionService;
import com.jhchen.framework.utils.HttpUtil;
import com.jhchen.mine.service.MineService;
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
    @Value("${centerAddr}")
    String centerAddr;
    @Autowired
    MineService mineService;
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    Account account;
    @Autowired
    List<SignedTransaction> transactionList;

    @GetMapping("/showTransactions")
    @ApiOperation(value = "查看目前交易列表")
    public ResponseResult transactions(){
        for (SignedTransaction transaction : transactionList) {
            System.out.println(transaction);
        }
        return ResponseResult.okResult(transactionList);
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

        System.out.println(transaction);
        SignedTransaction signedTransaction = transactionService.signTransaction(transaction, account);

        transactionList.add(signedTransaction);

        String s = JSON.toJSONString(signedTransaction);
        try {
            HttpUtil.post(centerAddr+"/addTransaction",s);
        } catch (IOException e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
        }
        System.out.println(s);
        return ResponseResult.okResult(signedTransaction);
    }
}
