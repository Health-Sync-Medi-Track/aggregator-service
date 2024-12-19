package com.meditrack.aggregator_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymptomsBySpecialtyDto {
    private String specialty;
    private String symptom;
    private int occurrenceCount;
}
