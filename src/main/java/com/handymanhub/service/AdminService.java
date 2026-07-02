package com.handymanhub.service;

import com.handymanhub.dto.response.AdminStatsResponseDto;
import com.handymanhub.dto.response.UserResponseDto;
import com.handymanhub.model.Booking;
import com.handymanhub.model.User;
import com.handymanhub.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHY A SEPARATE ADMIN SERVICE INSTEAD OF ADDING TO BOOKINGSERVICE:
//
// Separation of concerns. BookingService handles booking business logic.
// AdminService handles platform-level aggregation queries.
// They operate at different levels:
//   BookingService: "Can this booking be cancelled?"
//   AdminService:   "How many bookings were completed this month?"
//
// INTERVIEW QUESTION: "How would you calculate platform revenue?"
// Answer: "Revenue = SUM(worker.dailyRate * booking.durationDays)
//          for all COMPLETED bookings. This is an estimate because
//          contractor bookings may have different pricing models
//          that aren't captured in the current schema."
//
// NOTE ON estimatedRevenue:
// This only counts bookings with a worker (not contractor).
// Contractor pricing is more complex and would need a separate
// pricing model. This is acceptable for an MVP.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Service
public class AdminService {

    private final BookingRepository bookingRepository;
    private final WorkerRepository workerRepository;
    private final CustomerRepository customerRepository;
    private final ContractorRepository contractorRepository;
    private final UserRepository userRepository;

    public AdminService(BookingRepository bookingRepository,
                        WorkerRepository workerRepository,
                        CustomerRepository customerRepository,
                        ContractorRepository contractorRepository,
                        UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.workerRepository = workerRepository;
        this.customerRepository = customerRepository;
        this.contractorRepository = contractorRepository;
        this.userRepository = userRepository;
    }

    // ── Platform Stats ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public AdminStatsResponseDto getPlatformStats() {
        return AdminStatsResponseDto.builder()
                .totalBookings(bookingRepository.count())
                .pendingBookings(bookingRepository.countByStatus(Booking.Status.PENDING))
                .inProgressBookings(bookingRepository.countByStatus(Booking.Status.IN_PROGRESS))
                .completedBookings(bookingRepository.countByStatus(Booking.Status.COMPLETED))
                .cancelledBookings(bookingRepository.countByStatus(Booking.Status.CANCELLED))
                .totalWorkers(workerRepository.count())
                .totalCustomers(customerRepository.count())
                .totalContractors(contractorRepository.count())
                .verifiedContractors(contractorRepository.countByVerifiedTrue())
                .estimatedRevenue(bookingRepository.calculateCompletedRevenue())
                .build();
    }

    // ── User Listing (admin) ────────────────────────────────────
    // Returns all registered users (from the auth/users table).
    // Password is NEVER included — that's why we map to UserResponseDto.
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toUserDto);
    }

    // ── Worker Bookings ─────────────────────────────────────────
    // A worker needs to see their own upcoming and past bookings.
    // This is resource-scoped access: worker can only see THEIR bookings.
    @Transactional(readOnly = true)
    public Page<com.handymanhub.dto.response.BookingResponseDto> getWorkerBookings(
            Long workerId, Pageable pageable) {
        return bookingRepository.findByWorkerId(workerId, pageable)
                .map(this::toBookingDto);
    }

    // ── DTO Mappers ─────────────────────────────────────────────
    private UserResponseDto toUserDto(User u) {
        return UserResponseDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .createdAt(u.getCreatedAt())
                .build();
    }

    private com.handymanhub.dto.response.BookingResponseDto toBookingDto(Booking b) {
        return com.handymanhub.dto.response.BookingResponseDto.builder()
                .id(b.getId())
                .customerId(b.getCustomer().getId())
                .customerName(b.getCustomer().getName())
                .workerId(b.getWorker() != null ? b.getWorker().getId() : null)
                .workerName(b.getWorker() != null ? b.getWorker().getName() : null)
                .contractorId(b.getContractor() != null ? b.getContractor().getId() : null)
                .contractorName(b.getContractor() != null ? b.getContractor().getName() : null)
                .skillId(b.getSkill().getId())
                .skillName(b.getSkill().getName())
                .scheduledDate(b.getScheduledDate())
                .durationDays(b.getDurationDays())
                .status(b.getStatus())
                .address(b.getAddress())
                .notes(b.getNotes())
                .createdAt(b.getCreatedAt())
                .build();
    }
}