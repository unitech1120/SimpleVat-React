package com.simplevat.dao.impl;

import com.simplevat.dao.CurrencyDao;
import com.simplevat.entity.Currency;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by mohsin on 3/11/2017.
 */
@Repository
public class CurrencyDaoImpl implements CurrencyDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Currency> getCurrencies() {
        List<Currency> languages = entityManager.createNamedQuery("allCurrencies", Currency.class).getResultList();

        return languages;
    }

    @Override
    public Currency getDefaultCurrency() {
        List<Currency> currencies = getCurrencies();
        if (CollectionUtils.isNotEmpty(currencies)) {
            return currencies.get(0);
        }
        return null;

    }

    public Currency getCurrency(final int currencyCode) {
        return entityManager.find(Currency.class, currencyCode);
    }
}
