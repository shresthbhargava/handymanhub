-- ══════════════════════════════════════════
--  HandymanHub — V7: seed sample data
-- ══════════════════════════════════════════

INSERT INTO skills (name, category, description) VALUES
                                                     ('Electrician',     'Electrical', 'Wiring, switchboard repair, circuit breaker fitting'),
                                                     ('AC Technician',   'Electrical', 'AC installation, servicing, gas refill and repair'),
                                                     ('Plumber',         'Plumbing',   'Pipe fitting, tap repair, drainage and tank cleaning'),
                                                     ('Waterproofing',   'Plumbing',   'Bathroom and terrace waterproofing treatment'),
                                                     ('Mason',           'Civil',      'Brick laying, plastering and concrete work'),
                                                     ('Tile Worker',     'Civil',      'Floor and wall tile fixing and grouting'),
                                                     ('Painter',         'Civil',      'Interior and exterior painting, texture finish'),
                                                     ('Carpenter',       'Civil',      'Furniture, door frames, false ceiling, wardrobes'),
                                                     ('Maid',            'Domestic',   'Housekeeping, sweeping, mopping, utensil washing'),
                                                     ('Cook',            'Domestic',   'Home cooking, tiffin service, bulk cooking');
INSERT INTO contractors (name, phone, email, pincode, company_name, verified) VALUES
                                                                                  ('Ramesh Kumar',   '9711001122', 'ramesh@buildwell.in',    '110001', 'BuildWell Constructions', 1),
                                                                                  ('Santosh Yadav',  '9722002233', 'santosh@yadavinfra.com', '201301', 'Yadav Infrastructure',   1),
                                                                                  ('Harpreet Singh', '9733003344', 'harpreet@singhbuilds.com','122010', 'Singh and Sons Builders', 1),
                                                                                  ('Mohan Lal',      '9744004455', NULL,                     '110044',  NULL,                     0);
INSERT INTO workers (name, phone, pincode, daily_rate, available, contractor_id) VALUES
                                                                                     ('Suresh Chauhan', '9611001122', '110001', 750.00, 1, 1),
                                                                                     ('Dinesh Rajput',  '9622002233', '110001', 700.00, 1, 1),
                                                                                     ('Raju Prasad',    '9633003344', '201301', 650.00, 1, 2),
                                                                                     ('Gurdeep Sandhu', '9644004455', '122010', 900.00, 1, 3),
                                                                                     ('Sunil Bijlee',   '9655005566', '110024', 1000.00, 1, NULL),
                                                                                     ('Karim Plumber',  '9666006677', '110016', 850.00,  1, NULL),
                                                                                     ('Geeta Bai',      '9677007788', '110070', 500.00,  1, NULL);
INSERT INTO customers (name, phone, email, address, pincode) VALUES
                                                                 ('Priya Sharma',  '9811223344', 'priya.sharma@gmail.com',  'B-12 Lajpat Nagar, New Delhi',      '110024'),
                                                                 ('Arjun Mehta',   '9822334455', 'arjun.mehta@gmail.com',   'A-45 Sector 18, Noida',             '201301'),
                                                                 ('Sunita Verma',  '9833445566', 'sunita.verma@gmail.com',  'Plot 7 DLF Phase 3, Gurugram',      '122010'),
                                                                 ('Rahul Bose',    '9844556677', 'rahul.bose@gmail.com',    'C-3 Vasant Kunj, New Delhi',        '110070'),
                                                                 ('Meera Iyer',    '9855667788', 'meera.iyer@gmail.com',    'Flat 502 Prestige Tower, Dwarka',   '110075');
INSERT INTO bookings
(customer_id, worker_id, contractor_id, skill_id, scheduled_date, duration_days, status, address, notes)
VALUES
    (1, 5, NULL, 1, '2026-07-01', 1, 'COMPLETED', 'B-12 Lajpat Nagar', 'Fix kitchen wiring and add 2 power points'),
    (2, 6, NULL, 3, '2026-07-05', 2, 'COMPLETED', 'A-45 Sector 18 Noida', 'Bathroom pipe leakage and tap replacement'),
    (3, NULL, 1,  5, '2026-07-10', 15,'COMPLETED', 'Plot 7 DLF Phase 3', 'Full bathroom renovation with tiling'),
    (4, 5, NULL, 2, '2026-08-01', 1, 'CONFIRMED', 'C-3 Vasant Kunj', 'AC service and gas top-up for 2 units'),
    (5, NULL, 2,  8, '2026-08-10', 20,'PENDING',  'Flat 502 Prestige Tower', 'Bedroom wardrobe and study table'),
    (1, 7, NULL, 9, '2026-08-15', 3, 'PENDING',   'B-12 Lajpat Nagar', 'Full flat painting before moving in');
/*
-- All available workers in pincode 110024
SELECT w.name, w.daily_rate, w.pincode
FROM workers w
WHERE w.available = 1 AND w.pincode = '110024';

-- All bookings for customer Priya Sharma
SELECT b.id, s.name AS skill, b.scheduled_date, b.status,
       COALESCE(w.name, c.name) AS assigned_to
FROM bookings b
JOIN skills s ON s.id = b.skill_id
LEFT JOIN workers w ON w.id = b.worker_id
LEFT JOIN contractors c ON c.id = b.contractor_id
WHERE b.customer_id = 1;

-- Booking count by status
SELECT status, COUNT(*) AS total
FROM bookings
GROUP BY status;

-- Revenue potential — total days booked per contractor
SELECT c.name, SUM(b.duration_days) AS total_days_booked
FROM bookings b
JOIN contractors c ON c.id = b.contractor_id
WHERE b.status != 'CANCELLED'
GROUP BY c.name;
*/