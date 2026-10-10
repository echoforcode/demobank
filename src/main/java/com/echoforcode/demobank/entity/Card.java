package com.echoforcode.demobank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cardId;
    @Column(nullable = false, unique = true)
    private long cardNumber;
    private String cardHolder;
    //change to big decimal
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @CreationTimestamp
    private LocalDate iss;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime exp;
    private String cvv;
    private String pin;
    private String billingAddress;
    @OneToOne
    @JoinColumn(name = "owner_id",nullable = false, unique = true)
    private User owner;
    @OneToMany(mappedBy = "card",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

}
