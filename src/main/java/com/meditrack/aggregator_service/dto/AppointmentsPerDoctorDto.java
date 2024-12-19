package com.meditrack.aggregator_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentsPerDoctorDto {
    private String doctorId;
    private String doctorName;
    private int appointmentCount;
}
