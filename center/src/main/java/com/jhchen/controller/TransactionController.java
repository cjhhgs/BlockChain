package com.jhchen.controller;

import com.jhchen.domain.ResponseResult;
import com.jhchen.domain.modul.SignedTransaction;
import com.jhchen.service.CenterService;
import com.jhchen.service.TransactionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "交易处理")
public class TransactionController {
    @Autowired
    CenterService centerService;
    @Autowired
    List<SignedTransaction> transactionList;

    @PostMapping("/addTransaction")
    public ResponseResult addTransaction(@RequestBody SignedTransaction transaction){
        return centerService.addTransaction(transaction);
    }

    @GetMapping("/showTransactions")
    public ResponseResult showTransactions(){
        return ResponseResult.okResult(transactionList);
    }

}
