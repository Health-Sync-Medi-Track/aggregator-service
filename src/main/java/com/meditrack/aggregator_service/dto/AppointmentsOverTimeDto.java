package com.meditrack.aggregator_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentsOverTimeDto {
    private String period;
    private int appointmentCount;
}
