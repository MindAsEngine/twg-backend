package org.mae.twg.backend.services.admin;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.CurrencyHistory;
import org.mae.twg.backend.models.Currency;
import org.mae.twg.backend.repositories.admin.CurrencyHistoryRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyHistoryRepo currencyHistoryRepo;

    public List<CurrencyHistory> getAllCurrencyHistory() {
        return currencyHistoryRepo.findAll();
    }

    public List<CurrencyHistory> getAllCurrencyHistoryByCurrency(Currency currency) {
        return currencyHistoryRepo.findByCurrency(currency);
    }

    public void putCurrency(ConfigBusinessEnum config, String value) {
        Currency currency;
        switch (config) {
            case USD_TO_RUB -> {currency = Currency.RUB;}
            case USD_TO_UZS -> {currency = Currency.UZS;}
            default -> {currency = Currency.USD;}
        }
        CurrencyHistory currencyHistory = new CurrencyHistory(Double.valueOf(value), currency);
        currencyHistoryRepo.save(currencyHistory);
    }

}

