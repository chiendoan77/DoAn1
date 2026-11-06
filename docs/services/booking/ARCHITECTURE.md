# Booking architecture

Spring Boot application on development port 8083. Currently owns booking records and their current status. Schedule allocation and status history are not implemented. Profile is accessed via an internal API/DTO for provider validation; Identity details are carried in JWT claims, and Payment integration is not implemented.

Slot-overlap and repeat-category policies are approved requirements but are not implemented; interval/timezone and quota semantics remain open. Booking does not yet reference Catalog packages or a price snapshot, and does not publish `BookingCompleted` events. Admin booking operations are also not implemented.
