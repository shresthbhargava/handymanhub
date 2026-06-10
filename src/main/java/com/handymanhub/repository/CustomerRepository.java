package com.handymanhub.repository;

import com.handymanhub.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    Optional<Customer> findByEmail(String email);
}