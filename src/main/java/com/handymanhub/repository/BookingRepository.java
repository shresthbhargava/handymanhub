package com.handymanhub.repository;

import com.handymanhub.model.Booking;
import com.handymanhub.model.Booking.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import com.handymanhub.model.Booking;
import com.handymanhub.model.Booking.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByWorkerId(Long workerId);

    List<Booking> findByContractorId(Long contractorId);

    List<Booking> findByStatus(Status status);
    @EntityGraph(attributePaths = {"customer", "skill", "worker", "contractor"})
    Page<Booking> findAll(Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.worker.id = :workerId " +
            "AND b.scheduledDate = :date " +
            "AND b.status != :cancelled")
    boolean isWorkerBookedOnDate(@Param("workerId") Long workerId,
                                 @Param("date") LocalDate date,
                                 @Param("cancelled") Status cancelled);
    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.customer " +
            "JOIN FETCH b.skill " +
            "LEFT JOIN FETCH b.worker w " +
            "LEFT JOIN FETCH w.contractor " +
            "LEFT JOIN FETCH b.contractor")
    List<Booking> findAllWithDetails();

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.customer " +
            "JOIN FETCH b.skill " +
            "LEFT JOIN FETCH b.worker w " +
            "LEFT JOIN FETCH w.contractor " +
            "LEFT JOIN FETCH b.contractor " +
            "WHERE b.customer.id = :customerId")
    List<Booking> findByCustomerIdWithDetails(@Param("customerId") Long customerId);


}