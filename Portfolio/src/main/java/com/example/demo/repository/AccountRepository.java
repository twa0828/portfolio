package com.example.demo.repository;

import java.util.Optional;

import com.example.demo.model.Account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository
        extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(
            String email);
}
