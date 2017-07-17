package com.simplevat.dao.bankaccount;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.simplevat.dao.AbstractDao;
import com.simplevat.entity.bankaccount.TransactionCategory;

@Repository(value = "transactionCategoryDao")
public class TransactionCategoryNewDaoImpl extends AbstractDao<Integer, TransactionCategory> implements TransactionCategoryDaoNew{


	@Override
	public TransactionCategory getDefaultTransactionCategory() {
		List<TransactionCategory> transactionCategories = findAllTransactionCategory();
		
		if (CollectionUtils.isNotEmpty(transactionCategories)) {
            return transactionCategories.get(0);
        }
		return null;
	}


	@Override
	public List<TransactionCategory> findAllTransactionCategory() {
		return this.executeNamedQuery("findAllTransactionCategory");
	}


	@Override
	public TransactionCategory updateOrCreateTransaction(TransactionCategory transactionCategory) {
		return this.update(transactionCategory);
	}


}