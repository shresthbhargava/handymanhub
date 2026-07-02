package com.handymanhub.repository;

import com.handymanhub.model.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    boolean existsByPhone(String phone);

    // Old List methods (kept for old service methods)


    // Paginated versions (different param count = valid overloading)
    @Query("SELECT w FROM Worker w WHERE " +
           "(:available IS NULL OR w.available = :available) AND " +
           "(:pincode IS NULL OR w.pincode = :pincode)")
    Page<Worker> findAllFiltered(@Param("available") Boolean available,
                                 @Param("pincode") String pincode,
                                 Pageable pageable);

    Page<Worker> findByPincodeAndAvailableTrue(String pincode, Pageable pageable);

    Page<Worker> findByContractorId(Long contractorId, Pageable pageable);

    Page<Worker> findByAvailableTrue(Pageable pageable);
}