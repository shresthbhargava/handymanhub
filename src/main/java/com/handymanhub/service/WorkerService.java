package com.handymanhub.service;

import com.handymanhub.dto.response.WorkerResponseDto;
import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.Contractor;
import com.handymanhub.model.Worker;
import com.handymanhub.repository.ContractorRepository;
import com.handymanhub.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WorkerService {

    private static final Logger log = LoggerFactory.getLogger(WorkerService.class);

    private final WorkerRepository workerRepository;
    private final ContractorRepository contractorRepository;

    public WorkerService(WorkerRepository workerRepository,
                         ContractorRepository contractorRepository) {
        this.workerRepository = workerRepository;
        this.contractorRepository = contractorRepository;
    }

    // ─── Single-resource operations ────────────────────────────

    @Transactional(readOnly = true)
    public Worker getById(Long id) {
        log.debug("Fetching worker id={}", id);
        return workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", id));
    }

    @Transactional
    public Worker create(String name, String phone, String pincode,
                         BigDecimal dailyRate, Long contractorId) {
        log.info("Creating worker phone={}", phone);

        if (workerRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone already registered: " + phone);
        }

        Contractor contractor = null;
        if (contractorId != null) {
            contractor = contractorRepository.findById(contractorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contractor", contractorId));
        }

        Worker worker = Worker.builder()
                .name(name)
                .phone(phone)
                .pincode(pincode)
                .dailyRate(dailyRate)
                .available(true)
                .contractor(contractor)
                .build();

        Worker saved = workerRepository.save(worker);
        log.info("Worker created id={} contractorId={}", saved.getId(), contractorId);
        return saved;
    }

    @Transactional
    public Worker update(Long id, String name, String phone,
                         String pincode, BigDecimal dailyRate, Long contractorId) {
        log.info("Updating worker id={}", id);
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", id));

        worker.setName(name);
        worker.setPhone(phone);
        worker.setPincode(pincode);
        worker.setDailyRate(dailyRate);

        if (contractorId != null) {
            Contractor contractor = contractorRepository.findById(contractorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contractor", contractorId));
            worker.setContractor(contractor);
        } else {
            worker.setContractor(null);
        }

        return workerRepository.save(worker);
    }

    @Transactional
    public Worker toggleAvailability(Long id) {
        log.info("Toggling availability for worker id={}", id);
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", id));

        worker.setAvailable(!worker.getAvailable());
        log.info("Worker id={} availability is now {}", id, worker.getAvailable());
        return workerRepository.save(worker);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting worker id={}", id);
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", id));
        workerRepository.delete(worker);
        log.info("Worker id={} deleted", id);
    }

    // ─── Paginated queries ─────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<Worker> getAllFiltered(Boolean available, String pincode, Pageable pageable) {
        return workerRepository.findAllFiltered(available, pincode, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Worker> getAvailableByPincodePaged(String pincode, Pageable pageable) {
        return workerRepository.findByPincodeAndAvailableTrue(pincode, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Worker> getByContractorPaged(Long contractorId, Pageable pageable) {
        return workerRepository.findByContractorId(contractorId, pageable);
    }
}