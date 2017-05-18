package com.simplevat.bankaccount.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.simplevat.entity.bankaccount.BankAccount;
import com.simplevat.entity.bankaccount.TransactionCategory;
import com.simplevat.entity.bankaccount.TransactionType;

@Getter
@Setter
public class TransactionModel {
	
	public TransactionModel(){
		this.transactionId = 0;
	}
	
    private int transactionId;
    private Date transactionDate;
    private String transactionDescription;
    private BigDecimal transactionAmount;
    private TransactionType transactionType;
    private String receiptNumber;
    private Character debitCreditFlag;
    private Integer explainedProjectId;
    private TransactionCategory explainedTransactionCategory;
    private String explainedTransactionDescription;
    private String explainedTransactionAttachementDescription;
    private String explainedTransactionAttachementPath;
    private BankAccount bankAccount;
    private BigDecimal currentBalance = new BigDecimal(123);
    private Integer createdBy;
    private LocalDateTime createdDate;
    private Integer lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private Boolean deleteFlag =  false;
    private UploadedFile attachmentFile;
    private StreamedContent attachmentFileContent;
    private Integer versionNumber;

}