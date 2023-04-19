package com.jhchen.center.controller;

import com.jhchen.center.service.CenterService;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "交易处理")
public class CenterTransactionController {
    @Autowired
    CenterService centerService;
    @Autowired
    List<SignedTransaction> transactionList;
    @Autowired
    TransactionPool transactionPool;

    @PostMapping("/addTransaction")
    public ResponseResult addTransaction(@RequestBody SignedTransaction transaction){
        return centerService.addTransaction(transaction);
    }

    @GetMapping("/showTransactions")
    public ResponseResult showTransactions(){
        return ResponseResult.okResult(transactionPool);
    }

}