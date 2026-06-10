package com.handymanhub.repository;

import com.handymanhub.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    boolean existsByPhone(String phone);

    List<Worker> findByPincodeAndAvailableTrue(String pincode);

    List<Worker> findByContractorId(Long contractorId);

    List<Worker> findByAvailableTrue();
}