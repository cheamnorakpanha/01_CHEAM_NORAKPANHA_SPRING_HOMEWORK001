package org.example._1_cheam_norakpanha_spring_homework001.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example._1_cheam_norakpanha_spring_homework001.model.Status;
import org.example._1_cheam_norakpanha_spring_homework001.model.Ticket;
import org.example._1_cheam_norakpanha_spring_homework001.request.ApiResponse;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1")
public class TicketController {
    private final ResourceLoader resourceLoader;
    List<Ticket> ticketList = new ArrayList<>();
    AtomicLong idGen = new AtomicLong(1);

    public long autoId() {
        return idGen.getAndIncrement();
    }

    TicketController(GenericResponseService responseBuilder, ResourceLoader resourceLoader) {
        ticketList.add(new Ticket(autoId(), "John Smith", LocalDate.parse("2026-03-15"), "Phnom Penh", "Siem Reap", 15.50, true, Status.BOOKED, "12"));
        ticketList.add(new Ticket(autoId(), "Anna Lee", LocalDate.parse("2026-03-16"), "Phnom Penh", "Battambang", 18.00, true, Status.COMPLETED, "8"));
        ticketList.add(new Ticket(autoId(), "Sok Dara", LocalDate.parse("2026-03-17"), "Siem Reap", "Phnom Penh", 15.50, true, Status.BOOKED, "5"));
        ticketList.add(new Ticket(autoId(), "Sok Dara", LocalDate.parse("2026-01-20"), "Siem Reap", "Phnom Penh", 30.50, false, Status.CANCELLED, "2"));
        this.resourceLoader = resourceLoader;
    }

    // 1. Create a Ticket
    @Operation(summary = "Create a new ticket")
    @PostMapping("/tickets")
    public ResponseEntity<ApiResponse<List<Ticket>>> createNewTicket(@RequestBody Ticket ticketRequest) {
        ticketRequest.setTicketId(autoId());
        ticketList.add(ticketRequest);
        ApiResponse<List<Ticket>> Response = new ApiResponse<>(true, "Ticket created successfully.", HttpStatus.CREATED, ticketList, LocalDateTime.now());
        return ResponseEntity.ok(Response);
    }

    // 2. Retrieve all tickets
    @Operation(summary = "Get all tickets")
    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<Ticket>>> getAllTickets() {
        if (ticketList.isEmpty()) {
            ApiResponse<List<Ticket>> error = new ApiResponse<>(false, "No tickets found.", HttpStatus.NOT_FOUND, null, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        ApiResponse<List<Ticket>> response = new ApiResponse<>(true, "Tickets retrieved successfully.", HttpStatus.OK, ticketList, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    // 3. Retrieve a tickets by ID
    @Operation(summary = "Get a ticket by ID")
    @GetMapping("/tickets/{ticket-id}")
    public ResponseEntity<ApiResponse<Ticket>> getTicketById(@PathVariable("ticket-id") Long ticketId) {
        for (Ticket ticket : ticketList) {
            if (ticket.getTicketId().equals(ticketId)) {
                ApiResponse<Ticket> response = new ApiResponse<>(true, "Ticket fetched successfully.", HttpStatus.OK, ticket, LocalDateTime.now());
                return ResponseEntity.ok(response);
            }
        }
        ApiResponse<Ticket> Error = new ApiResponse<>(false, "No tickets found with the given ID.", HttpStatus.NOT_FOUND, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Error);
    }

    // 4. Search Ticket(s) by Passenger Name
    @Operation(summary = "Search for ticket(s) by passenger name")
    @GetMapping("/tickets/search")
    public ResponseEntity<ApiResponse<List<Ticket>>> searchTicketByName(@RequestParam(value = "passengerName") String passengerName) {

        List<Ticket> searchResult = new ArrayList<>();

        for (Ticket ticket : ticketList) {
            if (ticket.getPassengerName().equals(passengerName)) {
                searchResult.add(ticket);
            }
        }

        if (!searchResult.isEmpty()) {
            ApiResponse<List<Ticket>> Response = new ApiResponse<>(true, "Ticket fetched successfully.", HttpStatus.OK, searchResult, LocalDateTime.now());
            return ResponseEntity.ok(Response);
        }
        ApiResponse<List<Ticket>> Error = new ApiResponse<>(false, "Failed to fetch tickets.", HttpStatus.NOT_FOUND, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Error);
    }

    // 5. Filter Tickets by Ticket Status and Travel Date
    @Operation(summary = "Filter tickets by status and travel date")
    @GetMapping("/tickets/filter")
    public ResponseEntity<ApiResponse<List<Ticket>>> filteredTicketByStatusDate(@RequestParam Status status, @RequestParam LocalDate date) {

        List<Ticket> filteredTicket = new ArrayList<>();

        for (Ticket ticket : ticketList) {
            if (ticket.getTicketStatus() == status && ticket.getTravelDate().equals(date)) {
                filteredTicket.add(ticket);
            }
        }

        String message;

        if (filteredTicket.isEmpty()) {
            message = "No tickets found with given filters";
        } else {
            message = "Tickets filtered successfully.";
        }

        ApiResponse<List<Ticket>> Response = new ApiResponse<>(true, message, HttpStatus.OK, filteredTicket, LocalDateTime.now());
        return ResponseEntity.ok(Response);
    }

    // 6. Update a Ticket by ID
    @Operation(summary = "Update a ticket by ID")
    @PutMapping("/tickets/{id}")
    public ResponseEntity<ApiResponse<Ticket>> updateTicketById(@PathVariable("id") Long ticketId, @RequestBody Ticket ticket) {
        for (Ticket tickets : ticketList) {
            if (tickets.getTicketId().equals(ticketId)) {
                tickets.setPassengerName(ticket.getPassengerName());
                tickets.setTravelDate(tickets.getTravelDate());
                tickets.setSourceStation(tickets.getSourceStation());
                tickets.setDestinationStation(tickets.getDestinationStation());
                tickets.setPrice(ticket.getPrice());
                tickets.setPaymentStatus(ticket.isPaymentStatus());
                tickets.setTicketStatus(ticket.getTicketStatus());
                tickets.setSeatNumber(ticket.getSeatNumber());

                ApiResponse<Ticket> response = new ApiResponse<>(true, "Ticket updated successfully.", HttpStatus.OK, tickets, LocalDateTime.now());
                return ResponseEntity.ok(response);
            }
        }
        ApiResponse<Ticket> Error = new ApiResponse<>(false, "No tickets found with the given ID.", HttpStatus.OK, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Error);
    }

    // 7. Delete a ticket by ID
    @Operation(summary = "Delete a ticket using ID")
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTicketById(@PathVariable("id") Long ticketId) {
        boolean deleteTicket = ticketList.removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        if (deleteTicket) {
            ApiResponse<String> Response = new ApiResponse<>(true, "Ticket deleted successfully.", HttpStatus.OK, null, LocalDateTime.now());
            return ResponseEntity.ok(Response);
        }
        ApiResponse<String> Error = new ApiResponse<>(false, "Ticket not found", HttpStatus.NOT_FOUND, null, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Error);
    }
}
