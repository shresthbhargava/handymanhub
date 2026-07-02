package com.handymanhub.repository;

import com.handymanhub.model.Booking;
import com.handymanhub.model.Booking.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    long countByStatus(Booking.Status status);

    // Old List methods
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByWorkerId(Long workerId);







    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.worker.id = :workerId " +
            "AND b.scheduledDate = :date " +
            "AND b.status != :cancelled")
    boolean isWorkerBookedOnDate(@Param("workerId") Long workerId,
                                 @Param("date") LocalDate date,
                                 @Param("cancelled") Status cancelled);

    @Query("SELECT COALESCE(SUM(w.dailyRate * b.durationDays), 0) " +
            "FROM Booking b JOIN b.worker w " +
            "WHERE b.status = 'COMPLETED'")
    Long calculateCompletedRevenue();

    // Paginated methods
    @EntityGraph(attributePaths = {"customer", "skill", "worker", "contractor"})
    Page<Booking> findAll(Pageable pageable);

    Page<Booking> findByWorkerId(Long workerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.customer " +
           "LEFT JOIN FETCH b.worker " +
           "LEFT JOIN FETCH b.contractor " +
           "LEFT JOIN FETCH b.skill " +
           "WHERE (:status IS NULL OR b.status = :status)")
    Page<Booking> findAllFiltered(@Param("status") Booking.Status status,
                                  Pageable pageable);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.customer " +
           "LEFT JOIN FETCH b.worker " +
           "LEFT JOIN FETCH b.contractor " +
           "LEFT JOIN FETCH b.skill " +
           "WHERE b.customer.id = :customerId")
    Page<Booking> findByCustomerIdPaged(@Param("customerId") Long customerId,
                                        Pageable pageable);

    Page<Booking> findByStatus(Booking.Status status, Pageable pageable);

    Page<Booking> findByCustomerId(Long customerId, Pageable pageable);
}