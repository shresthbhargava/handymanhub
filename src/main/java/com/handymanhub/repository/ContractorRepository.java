package com.handymanhub.repository;

import com.handymanhub.model.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContractorRepository extends JpaRepository<Contractor, Long> {
    @Query("SELECT COUNT(w) FROM Worker w WHERE w.contractor.id = :contractorId")
    long countWorkersByContractorId(Long contractorId);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    Optional<Contractor> findByPhone(String phone);

    List<Contractor> findByVerifiedTrue();

    List<Contractor> findByPincode(String pincode);

    long countByVerifiedTrue();
}