package com.meditrack.aggregator_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDto {

    private String id;
    private String patientIdentifier;
    private String name;
    private int age;
    private String gender;
    private String contactNumber;
    private List<String> medicalHistory;
    private List<String> prescriptions;
    private List<String> labResults;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
