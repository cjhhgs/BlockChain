package com.jhchen.mine.controller;


import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.service.AccountService;
import com.jhchen.mine.service.MineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "挖矿节点Api")
public class MineController {
    @Value("${centerAddr}")
    private String centerAddr;
    @Autowired
    MineService mineService;
    @Autowired
    AccountService accountService;
    @Autowired
    Account account;
    @Autowired
    List<Block> blockChain;

    @GetMapping("/showAccount")
    @ApiOperation(value = "显示本地账户")
    public ResponseResult showAccount(){
        if(account.getPublicKey()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.ACCOUNT_LOAD_ERROR);
        }
        return ResponseResult.okResult(account);
    }

    @GetMapping("/createAccount")
    @ApiOperation(value = "创建账户并存储在本地")
    public ResponseResult createAccount(){
        Account a = accountService.createAccount();
        account.setAddr(a.getAddr());
        account.setPrivateKey(a.getPrivateKey());
        account.setPublicKey(a.getPublicKey());
        mineService.saveAccount(account);
        return ResponseResult.okResult(account);

    }

    @GetMapping("/loadAccount")
    @ApiOperation(value = "从本地文件加载账户信息")
    public ResponseResult loadAccount(){
        try {
            mineService.loadAccount();
            return ResponseResult.okResult();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.ACCOUNT_LOAD_ERROR);
        }

    }

    @GetMapping("/register")
    @ApiOperation(value = "向中心节点注册")
    public ResponseResult register(){
        mineService.register();
        return ResponseResult.okResult();
    }

    @GetMapping("/showBlockChain")
    public ResponseResult showBlockChain(){
        return ResponseResult.okResult(blockChain);
    }

    @GetMapping("/getBlockChain")
    public ResponseResult getBlockChain(){
        return mineService.getBlockChainFromCenter();
    }

    @GetMapping("/mine")
    @ApiOperation(value = "挖矿")
    public ResponseResult mine(){
        return mineService.mine();
    }




}
