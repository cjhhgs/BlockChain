package com.jhchen.center.controller;


import com.jhchen.center.service.CenterAccountService;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.service.AccountService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Queue;

@RestController
@Api(
        tags = "账户",
        consumes = "application/json",
        produces = "application/json",
        protocols = "http",
        value = "区块链账户注册"
)
public class CenterAccountController {
    @Autowired
    private AccountService registerService;
    @Autowired
    private CenterAccountService centerAccountService;
    @Autowired
    private Queue<Account> waitList;
    @Autowired
    @Qualifier("accountList")
    private List<Account> accountList;

    @GetMapping("/createUserAccount")
    @ApiOperation(value = "创建一个账户")

    public ResponseResult createAccount(){
        try {
            Account account = registerService.createAccount();
            return ResponseResult.okResult(account);
        }catch (Exception e){
            System.out.println(e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"创建账户错误");
        }
    }

    @PostMapping("/register")
    @ApiOperation(value = "账户注册为挖矿节点")
    public ResponseResult register(@RequestBody Account account){
        return centerAccountService.register(account);
    }

    @PostMapping("/queue")
    @ApiOperation(value = "账户进入排队队列")
    public ResponseResult queue(@RequestBody Account account){
        return centerAccountService.queue(account);
    }
    @GetMapping("/showAccount")
    @ApiOperation(value = "显示所有注册账户")
    public ResponseResult showAccount(){
        return ResponseResult.okResult(accountList);
    }

    @GetMapping("/showQueue")
    @ApiOperation(value = "显示等待队列")
    public ResponseResult showQueue(){
        return ResponseResult.okResult(waitList);
    }
}
