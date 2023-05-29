package com.jhchen.mine.controller;

import com.jhchen.framework.domain.modul.Transaction;
import lombok.Data;

@Data
public class CreateTransBatchVo {
    private Transaction transaction;
    private Integer count;
}
