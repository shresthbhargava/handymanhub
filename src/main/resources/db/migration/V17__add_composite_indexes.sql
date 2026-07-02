-- Composite index: bookings filtered by customer + status (covers customer-only lookups too via leftmost prefix)
CREATE INDEX idx_booking_customer_status ON bookings(customer_id, status);

-- Composite index: workers filtered by pincode + availability (covers pincode-only lookups too via leftmost prefix)
CREATE INDEX idx_worker_pincode_available ON workers(pincode, available);