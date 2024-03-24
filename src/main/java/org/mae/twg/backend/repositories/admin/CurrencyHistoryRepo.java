package org.mae.twg.backend.repositories.admin;

import org.mae.twg.backend.CurrencyHistory;
import org.mae.twg.backend.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyHistoryRepo extends JpaRepository<CurrencyHistory, Long> {
    List<CurrencyHistory> findByCurrency(Currency currency);
    CurrencyHistory findFirstByCurrencyOrderByChangedAtDesc(Currency currency);
}
