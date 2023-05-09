package com.jhchen.center.service;

import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 中央节点处理账户信息
 * @createDate 2023/4/24 14:09
 * @since 1.0.0
 */
@Service
public class CenterAccountService {
    @Autowired
    private Queue<Account> waitList;
    @Autowired
    @Qualifier("accountList")
    private List<Account> accountList;

    /**
     * 账户申请入队
     * @param account
     * @return
     */
    public ResponseResult queue(Account account){
        if(!ifAccountInQueue(account)){
            waitList.add(account);
            return ResponseResult.okResult();
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
    }

    public ResponseResult register(Account account){
        List<Account> collect = accountList.stream().filter(i -> i.getAddr().equals(account.getAddr())).collect(Collectors.toList());
        if(collect.isEmpty()){
            accountList.add(account);
            return ResponseResult.okResult();
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
    }

    private Boolean ifAccountInQueue(Account account){
        List<Account> collect = waitList.stream()
                .filter(a -> a.getAddr().equals(account.getAddr()))
                .collect(Collectors.toList());
        return !collect.isEmpty();
    }
}
