package com.jhchen.domain.modul;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String send;
    private String receive;
    private String amount;

    public Transaction(Transaction transaction) {
        this.send = transaction.getSend();
        this.amount = transaction.getAmount();
        this.receive = transaction.getReceive();
    }
}
