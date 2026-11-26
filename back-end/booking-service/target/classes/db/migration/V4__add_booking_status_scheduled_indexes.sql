CREATE INDEX idx_bookings_status_created ON bookings (status, created_at);
CREATE INDEX idx_bookings_scheduled_at ON bookings (scheduled_at);
