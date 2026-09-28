package com.echoforcode.demobank.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

//
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank_user")
public class User implements UserDetails{
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) return List.of();
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
}
