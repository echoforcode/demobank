package com.echoforcode.demobank.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uid;
    private String firstname;
    private String lastname;
    @Column(nullable = false, unique = true)
    private String username;
    private Date dob;
    private long tel;
    private String tag;
    private String password;
    private String gender;
    private List<String> roles;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @OneToOne(mappedBy = "owner")//foreign key
    private Card card;
    @OneToMany(mappedBy = "owner",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;
    @OneToMany(mappedBy = "owner",
            cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
