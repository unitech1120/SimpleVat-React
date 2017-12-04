/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simplevat.web.bankaccount.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author admin
 */
@Data
public class TransactionViewModel implements Serializable {

    private int transactionId;
    private BigDecimal currentBalance;
    private Character debitCreditFlag;
    private Integer entryType;
    private Integer referenceId;
    private Integer referenceType;
    private BigDecimal transactionAmount;
    private Date transactionDate;
    private String transactionDescription;
    private Integer bankAccountId;
    private Integer parentTransaction;
    private String transactionCategoryName;
    private String transactionTypeName;
    private Integer explanationStatusCode;
    private String explanationStatusName;
    private String referenceName;
    private BigDecimal dueAmount;
    private String currencySymbol;
    private String contactName;
    private Date dueOn;
    private List<TransactionViewModel> childTransactionList = new ArrayList<>();
    private boolean expandIcon;
    private String suggestedTransactionString;

    public TransactionViewModel() {
    }

    public boolean isParent() {
        return parentTransaction == null;
    }

}