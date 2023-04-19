package com.jhchen.center.controller;


import com.jhchen.center.service.CenterService;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.service.AccountService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    private CenterService centerService;


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

        return centerService.register(account);
    }
}
