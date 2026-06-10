package com.handymanhub;

import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.*;
import com.handymanhub.model.Booking.Status;
import com.handymanhub.repository.*;
import com.handymanhub.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock CustomerRepository customerRepository;
    @Mock WorkerRepository workerRepository;
    @Mock ContractorRepository contractorRepository;
    @Mock SkillRepository skillRepository;

    @InjectMocks
    BookingService bookingService;

    // Test data — reused across tests
    private Customer customer;
    private Worker worker;
    private Contractor contractor;
    private Skill skill;
    private LocalDate futureDate;
    @BeforeEach
    void setUp() {
        futureDate = LocalDate.now().plusDays(7);

        customer = Customer.builder()
                .name("Priya Sharma")
                .phone("9811223344")
                .email("priya@gmail.com")
                .build();

        skill = Skill.builder()
                .name("Electrician")
                .category("Electrical")
                .build();

        worker = Worker.builder()
                .name("Sunil Bijlee")
                .phone("9655005566")
                .pincode("110024")
                .dailyRate(new BigDecimal("1000.00"))
                .available(true)
                .build();

        contractor = Contractor.builder()
                .name("Ramesh Kumar")
                .phone("9711001122")
                .verified(true)
                .build();
    }
    @Test
    @DisplayName("Should create booking successfully with a worker")
    void createBooking_withWorker_success() {
        // ARRANGE — set up what the mocks return
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(bookingRepository.isWorkerBookedOnDate(1L, futureDate, Status.CANCELLED))
                .thenReturn(false);
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT — call the method being tested
        Booking result = bookingService.create(
                1L, 1L, null, 1L, futureDate, 2, "B-12 Lajpat Nagar", "Fix wiring");

        // ASSERT — verify the result
        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());
        assertEquals(customer, result.getCustomer());
        assertEquals(worker, result.getWorker());
        assertNull(result.getContractor());
        assertEquals(2, result.getDurationDays());

        // verify save was actually called
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should create booking successfully with a contractor")
    void createBooking_withContractor_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(contractorRepository.findById(1L)).thenReturn(Optional.of(contractor));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.create(
                1L, null, 1L, 1L, futureDate, 10, "Plot 7 Gurugram", "Renovation");

        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());
        assertNull(result.getWorker());
        assertEquals(contractor, result.getContractor());

        verify(bookingRepository).save(any(Booking.class));
    }
    @Test
    @DisplayName("Should throw exception when both worker and contractor are null")
    void createBooking_noWorkerNoContractor_throwsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.create(
                        1L, null, null, 1L, futureDate, 1, "address", null)
        );

        assertEquals("Booking must have either a worker or a contractor", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when both worker and contractor are provided")
    void createBooking_bothWorkerAndContractor_throwsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.create(
                        1L, 1L, 1L, 1L, futureDate, 1, "address", null)
        );

        assertEquals("Booking cannot have both a worker and a contractor", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw 404 when customer not found")
    void createBooking_customerNotFound_throwsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.create(
                        99L, 1L, null, 1L, futureDate, 1, "address", null)
        );
    }

    @Test
    @DisplayName("Should throw exception when worker is not available")
    void createBooking_workerNotAvailable_throwsException() {
        worker.setAvailable(false);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.create(
                        1L, 1L, null, 1L, futureDate, 1, "address", null)
        );

        assertTrue(ex.getMessage().contains("is not available"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when worker already booked on that date")
    void createBooking_workerAlreadyBooked_throwsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(bookingRepository.isWorkerBookedOnDate(1L, futureDate, Status.CANCELLED))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.create(
                        1L, 1L, null, 1L, futureDate, 1, "address", null)
        );

        assertTrue(ex.getMessage().contains("already booked on"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when contractor is not verified")
    void createBooking_contractorNotVerified_throwsException() {
        contractor.setVerified(false);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(contractorRepository.findById(1L)).thenReturn(Optional.of(contractor));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.create(
                        1L, null, 1L, 1L, futureDate, 1, "address", null)
        );

        assertTrue(ex.getMessage().contains("not verified"));
        verify(bookingRepository, never()).save(any());
    }
    @Test
    @DisplayName("Should update status from PENDING to CONFIRMED")
    void updateStatus_pendingToConfirmed_success() {
        Booking booking = Booking.builder()
                .status(Status.PENDING)
                .customer(customer)
                .skill(skill)
                .scheduledDate(futureDate)
                .durationDays(1)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.updateStatus(1L, Status.CONFIRMED);

        assertEquals(Status.CONFIRMED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Should throw exception for invalid status transition")
    void updateStatus_invalidTransition_throwsException() {
        Booking booking = Booking.builder()
                .status(Status.PENDING)
                .customer(customer)
                .skill(skill)
                .scheduledDate(futureDate)
                .durationDays(1)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateStatus(1L, Status.COMPLETED)
        );

        assertTrue(ex.getMessage().contains("Invalid status transition"));
        assertTrue(ex.getMessage().contains("PENDING"));
        assertTrue(ex.getMessage().contains("COMPLETED"));
    }

    @Test
    @DisplayName("Should cancel a PENDING booking successfully")
    void cancel_pendingBooking_success() {
        Booking booking = Booking.builder()
                .status(Status.PENDING)
                .customer(customer)
                .skill(skill)
                .scheduledDate(futureDate)
                .durationDays(1)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.cancel(1L);

        assertEquals(Status.CANCELLED, result.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when cancelling a COMPLETED booking")
    void cancel_completedBooking_throwsException() {
        Booking booking = Booking.builder()
                .status(Status.COMPLETED)
                .customer(customer)
                .skill(skill)
                .scheduledDate(futureDate)
                .durationDays(1)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.cancel(1L)
        );

        assertTrue(ex.getMessage().contains("Cannot cancel a booking that is COMPLETED"));
    }
}