package com.handymanhub.service;

import com.handymanhub.dto.response.BookingResponseDto;
import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.Booking;
import com.handymanhub.model.Booking.Status;
import com.handymanhub.model.Contractor;
import com.handymanhub.model.Customer;
import com.handymanhub.model.Skill;
import com.handymanhub.model.Worker;
import com.handymanhub.repository.BookingRepository;
import com.handymanhub.repository.ContractorRepository;
import com.handymanhub.repository.CustomerRepository;
import com.handymanhub.repository.SkillRepository;
import com.handymanhub.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final WorkerRepository workerRepository;
    private final ContractorRepository contractorRepository;
    private final SkillRepository skillRepository;

    public BookingService(BookingRepository bookingRepository,
                          CustomerRepository customerRepository,
                          WorkerRepository workerRepository,
                          ContractorRepository contractorRepository,
                          SkillRepository skillRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.workerRepository = workerRepository;
        this.contractorRepository = contractorRepository;
        this.skillRepository = skillRepository;
    }

    // ─── Single-resource operations ────────────────────────────

    @Transactional(readOnly = true)
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    @Transactional
    public Booking create(Long customerId, Long workerId, Long contractorId,
                          Long skillId, LocalDate scheduledDate,
                          Integer durationDays, String address, String notes) {

        // Rule 1 — must have either worker or contractor, not neither
        if (workerId == null && contractorId == null) {
            throw new IllegalArgumentException(
                    "Booking must have either a worker or a contractor");
        }

        // Rule 2 — must not have both
        if (workerId != null && contractorId != null) {
            throw new IllegalArgumentException(
                    "Booking cannot have both a worker and a contractor");
        }

        log.info("Creating booking customerId={} workerId={} contractorId={}",
                customerId, workerId, contractorId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", skillId));

        Worker worker = null;
        if (workerId != null) {
            worker = workerRepository.findById(workerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Worker", workerId));

            if (!worker.getAvailable()) {
                throw new IllegalArgumentException(
                        "Worker id=" + workerId + " is not available");
            }

            boolean alreadyBooked = bookingRepository.isWorkerBookedOnDate(
                    workerId, scheduledDate, Status.CANCELLED);
            if (alreadyBooked) {
                throw new IllegalArgumentException(
                        "Worker id=" + workerId + " is already booked on " + scheduledDate);
            }
        }

        Contractor contractor = null;
        if (contractorId != null) {
            contractor = contractorRepository.findById(contractorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contractor", contractorId));

            if (!contractor.getVerified()) {
                throw new IllegalArgumentException(
                        "Contractor id=" + contractorId + " is not verified yet");
            }
        }

        Booking booking = Booking.builder()
                .customer(customer)
                .worker(worker)
                .contractor(contractor)
                .skill(skill)
                .scheduledDate(scheduledDate)
                .durationDays(durationDays != null ? durationDays : 1)
                .address(address)
                .notes(notes)
                .status(Status.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created id={} status=PENDING", saved.getId());
        return saved;
    }

    @Transactional
    public Booking updateStatus(Long id, Status newStatus) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));

        Status current = booking.getStatus();
        validateStatusTransition(current, newStatus);

        booking.setStatus(newStatus);
        Booking saved = bookingRepository.save(booking);
        log.info("Booking id={} status changed: {} -> {}", id, current, newStatus);
        return saved;
    }

    @Transactional
    public Booking cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));

        if (booking.getStatus() == Status.IN_PROGRESS ||
                booking.getStatus() == Status.COMPLETED) {
            throw new IllegalArgumentException(
                    "Cannot cancel a booking that is " + booking.getStatus());
        }

        if (booking.getStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        booking.setStatus(Status.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        log.info("Booking id={} cancelled", id);
        return saved;
    }

    // ─── Paginated queries ─────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<BookingResponseDto> getAllFiltered(Booking.Status status, Pageable pageable) {
        return bookingRepository.findAllFiltered(status, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponseDto> getByCustomerPaged(Long customerId, Pageable pageable) {
        return bookingRepository.findByCustomerIdPaged(customerId, pageable)
                .map(this::toDto);
    }

    // ─── DTO mapping ───────────────────────────────────────────

    private BookingResponseDto toDto(Booking b) {
        return BookingResponseDto.builder()
                .id(b.getId())
                .customerId(b.getCustomer() != null ? b.getCustomer().getId() : null)
                .customerName(b.getCustomer() != null ? b.getCustomer().getName() : null)
                .workerId(b.getWorker() != null ? b.getWorker().getId() : null)
                .workerName(b.getWorker() != null ? b.getWorker().getName() : null)
                .contractorId(b.getContractor() != null ? b.getContractor().getId() : null)
                .contractorName(b.getContractor() != null ? b.getContractor().getName() : null)
                .skillId(b.getSkill() != null ? b.getSkill().getId() : null)
                .skillName(b.getSkill() != null ? b.getSkill().getName() : null)
                .scheduledDate(b.getScheduledDate())
                .durationDays(b.getDurationDays())
                .address(b.getAddress())
                .notes(b.getNotes())
                .status(b.getStatus())
                .build();
    }

    // ─── Status machine ────────────────────────────────────────

    private void validateStatusTransition(Status current, Status next) {
        boolean valid = switch (current) {
            case PENDING     -> next == Status.CONFIRMED || next == Status.CANCELLED;
            case CONFIRMED   -> next == Status.IN_PROGRESS || next == Status.CANCELLED;
            case IN_PROGRESS  -> next == Status.COMPLETED;
            case COMPLETED    -> false;
            case CANCELLED    -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid status transition: " + current + " -> " + next);
        }
    }
}