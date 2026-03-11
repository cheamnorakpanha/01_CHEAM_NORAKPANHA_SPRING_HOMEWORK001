package org.example._1_cheam_norakpanha_spring_homework001.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Schema(hidden = true)
    private Long ticketId;
    private String passengerName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "2026-03-11")
    private LocalDate travelDate;
    private String sourceStation;
    private String destinationStation;

    @Schema(example = "0")
    private double price;
    private boolean paymentStatus;

    @Schema(example = "BOOKED")
    private Status ticketStatus;
    private String seatNumber;
}