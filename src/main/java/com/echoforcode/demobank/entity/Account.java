package com.echoforcode.demobank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "account", uniqueConstraints = @UniqueConstraint(
        name = "uk_account_owner_code", columnNames = {"owner_id", "code"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String accountId;
    private double balance;
    private String accountName;
    @Column(nullable = false, unique = true)
    private long accountNumber;
    private String currency;
    @Column(nullable = false, length = 3)
    private String code;
    private String label;
    private char symbol;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id",nullable = false)//foreign key
    private User owner;
    @OneToMany(mappedBy = "account",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
