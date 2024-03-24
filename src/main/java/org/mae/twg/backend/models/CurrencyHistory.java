package org.mae.twg.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.mae.twg.backend.models.Currency;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "currency_history")
public class CurrencyHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    @Column(name = "changed_at")
    private LocalDate changedAt;

    private Double value;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    public CurrencyHistory(Double value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }
}
