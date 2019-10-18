package com.simplevat.dao.impl;

import java.util.List;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simplevat.dao.AbstractDao;
import com.simplevat.dao.PurchaseDao;
import com.simplevat.entity.Purchase;
import com.simplevat.entity.invoice.Invoice;
import java.math.BigDecimal;
import javax.persistence.TypedQuery;

@Repository
@Transactional
public class PurchaseDaoImpl extends AbstractDao<Integer, Purchase> implements PurchaseDao {

	
    @Override
	public List<Purchase> getAllPurchase() {
		List<Purchase> purchases = this.executeNamedQuery("allPurchase");
		return purchases;
	}

    @Override
    public Purchase getClosestDuePurchaseByContactId(Integer contactId) {
        TypedQuery<Purchase> query = getEntityManager().createQuery("Select p from Purchase p where p.deleteFlag = false and p.purchaseDueAmount !=:dueAmount and p.purchaseContact.contactId =:contactId ORDER BY p.purchaseDueDate ASC", Purchase.class);
        query.setParameter("contactId", contactId);
        query.setParameter("dueAmount", new BigDecimal(0));
        List<Purchase> purchaseList = query.getResultList();
        if (purchaseList != null && !purchaseList.isEmpty()) {
            return purchaseList.get(0);
        }
        return null;
    }

    @Override
    public List<Purchase> getPurchaseListByDueAmount() {
        TypedQuery<Purchase> query = getEntityManager().createQuery("Select p from Purchase p where p.deleteFlag = false and p.purchaseDueAmount !=:dueAmount", Purchase.class);
        query.setParameter("dueAmount", new BigDecimal(0.00));
        List<Purchase> purchaseList = query.getResultList();
        if (purchaseList != null && !purchaseList.isEmpty()) {
            return purchaseList;
        }
        return null;
    }

}