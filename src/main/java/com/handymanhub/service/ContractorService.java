package com.handymanhub.service;

import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.Contractor;
import com.handymanhub.repository.ContractorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractorService {

    private static final Logger log = LoggerFactory.getLogger(ContractorService.class);

    private final ContractorRepository contractorRepository;

    public ContractorService(ContractorRepository contractorRepository) {
        this.contractorRepository = contractorRepository;
    }

    public List<Contractor> getAll() {
        log.debug("Fetching all contractors");
        return contractorRepository.findAll();
    }

    public Contractor getById(Long id) {
        log.debug("Fetching contractor id={}", id);
        return contractorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contractor", id));
    }

    public List<Contractor> getVerified() {
        log.debug("Fetching verified contractors");
        return contractorRepository.findByVerifiedTrue();
    }

    public List<Contractor> getByPincode(String pincode) {
        log.debug("Fetching contractors in pincode={}", pincode);
        return contractorRepository.findByPincode(pincode);
    }

    @Transactional
    public Contractor create(String name, String phone, String email,
                             String pincode, String companyName) {
        log.info("Creating contractor phone={}", phone);

        if (contractorRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone already registered: " + phone);
        }

        if (email != null && contractorRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        Contractor contractor = Contractor.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .pincode(pincode)
                .companyName(companyName)
                .verified(false)
                .build();

        Contractor saved = contractorRepository.save(contractor);
        log.info("Contractor created id={}", saved.getId());
        return saved;
    }

    @Transactional
    public Contractor update(Long id, String name, String phone,
                             String email, String pincode, String companyName) {
        log.info("Updating contractor id={}", id);
        Contractor contractor = contractorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contractor", id));

        contractor.setName(name);
        contractor.setPhone(phone);
        contractor.setEmail(email);
        contractor.setPincode(pincode);
        contractor.setCompanyName(companyName);

        return contractorRepository.save(contractor);
    }

    @Transactional
    public Contractor verify(Long id) {
        log.info("Verifying contractor id={}", id);
        Contractor contractor = contractorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contractor", id));

        contractor.setVerified(true);
        Contractor saved = contractorRepository.save(contractor);
        log.info("Contractor id={} verified successfully", id);
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting contractor id={}", id);
        Contractor contractor = contractorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contractor", id));
        contractorRepository.delete(contractor);
        log.info("Contractor id={} deleted", id);
    }
}