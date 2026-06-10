package com.handymanhub.repository;

import com.handymanhub.model.Booking;
import com.handymanhub.model.Booking.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByWorkerId(Long workerId);

    List<Booking> findByContractorId(Long contractorId);

    List<Booking> findByStatus(Status status);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.worker.id = :workerId " +
            "AND b.scheduledDate = :date " +
            "AND b.status != :cancelled")
    boolean isWorkerBookedOnDate(@Param("workerId") Long workerId,
                                 @Param("date") LocalDate date,
                                 @Param("cancelled") Status cancelled);
}