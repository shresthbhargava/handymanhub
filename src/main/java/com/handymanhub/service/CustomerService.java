package com.handymanhub.service;

import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.Customer;
import com.handymanhub.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    @Transactional
    public Customer create(String name, String phone,
                           String email, String address, String pincode) {
        if (customerRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone already registered: " + phone);
        }
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        Customer customer = Customer.builder()
                .name(name).phone(phone).email(email)
                .address(address).pincode(pincode)
                .build();
        Customer saved = customerRepository.save(customer);
        log.info("Customer created id={}", saved.getId());
        return saved;
    }

    @Transactional
    public Customer update(Long id, String name, String phone,
                           String email, String address, String pincode) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setPincode(pincode);
        return customerRepository.save(customer);
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        customerRepository.delete(customer);
        log.info("Customer id={} deleted", id);
    }
}