package com.jhchen.framework.service;


import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    ECCService eccService;

    public SignedTransaction signTransaction(Transaction transaction, Account account){
        String sign = eccService.eccSign(account.getPrivateKey(), transaction.toString());
        return new SignedTransaction(account.getPublicKey(), transaction,sign);
    }



    public Boolean verify(SignedTransaction signedTransaction){
        String publicKey = signedTransaction.getPublicKey();
        Transaction transaction = signedTransaction.getTransaction();
        String sign = signedTransaction.getSign();
        //验证交易合法性
        return eccService.eccVerify(publicKey,transaction.toString(),sign);

    }
}
