package com.simplevat.dao;

import com.simplevat.entity.Currency;
import com.simplevat.entity.CurrencyConversion;

import java.util.List;

/**
 * Created by mohsin on 3/11/2017.
 */
public interface CurrencyDao extends Dao<Integer, Currency> {

    List<Currency> getCurrencies();
    
    Currency getCurrency(final int currencyCode);

    Currency getDefaultCurrency();

    public CurrencyConversion getCurrencyRateFromCurrencyConversion(int currencyCode);
}
