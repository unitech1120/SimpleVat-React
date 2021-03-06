package com.simplevat.dao.impl.bankaccount;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simplevat.dao.AbstractDao;
import com.simplevat.dao.bankaccount.BankAccountDao;
import com.simplevat.entity.bankaccount.BankAccount;
import javax.persistence.TypedQuery;

@Repository
@Transactional
public class BankAccountDaoImpl extends AbstractDao<Integer, BankAccount> implements BankAccountDao {

    @Override
    public List<BankAccount> getBankAccounts() {
        return this.executeNamedQuery("allBankAccounts");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BankAccount> getBankAccountByUser(int userId) {
        List<BankAccount> resultList = null;
        try {
            String hql = "from BankAccount where createdBy = :userId";
            Query query = getEntityManager().createQuery(hql);
            query.setParameter("userId", userId);
            resultList = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;

    }

    @Override
    public BankAccount getBankAccountById(int id) {
        TypedQuery<BankAccount> query = getEntityManager().createQuery("SELECT b FROM BankAccount b WHERE b.bankAccountId =:id", BankAccount.class);
        query.setParameter("id", id);
        List<BankAccount> bankAccountList = query.getResultList();
        if (bankAccountList != null && !bankAccountList.isEmpty()) {
            return bankAccountList.get(0);
        }
        return null;
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            for (Integer id : ids) {
                BankAccount bankAccount = findByPK(id);
                bankAccount.setDeleteFlag(Boolean.TRUE);
                update(bankAccount);
            }
        }
    }

}
