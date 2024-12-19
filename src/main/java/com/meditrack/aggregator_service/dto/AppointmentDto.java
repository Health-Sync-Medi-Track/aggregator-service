package com.meditrack.aggregator_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentDto {

    private String id;
    private String patientId;
    private String doctorId;
    private String doctorName;
    private LocalDateTime appointmentDate;
    private String status;

}
