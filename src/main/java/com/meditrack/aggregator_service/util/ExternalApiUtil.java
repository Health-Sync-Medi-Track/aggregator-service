package com.meditrack.aggregator_service.util;

import com.meditrack.aggregator_service.dto.AppointmentDto;
import com.meditrack.aggregator_service.dto.PatientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for fetching data from external services like Appointment and Patient Management Services.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalApiUtil {

    private final RestTemplate restTemplate;

    @Value("${aggregator.services.base-url}")
    private String baseUrl;

    @Value("${aggregator.services.endpoints.appointment-service}")
    private String appointmentsEndpoint;

    @Value("${aggregator.services.endpoints.patient-service}")
    private String patientsEndpoint;

    /**
     * Fetch all appointments from the Appointment Management Service.
     *
     * @return List of AppointmentDto objects.
     */
    public List<AppointmentDto> getAllAppointments() {
        String url = baseUrl + appointmentsEndpoint;
        log.info("Fetching all appointments from {}", url);

        try {
            AppointmentDto[] appointmentsArray = restTemplate.getForObject(url, AppointmentDto[].class);

            if (appointmentsArray != null) {
                log.info("Successfully fetched {} appointments.", appointmentsArray.length);
                return Arrays.asList(appointmentsArray);
            } else {
                log.warn("No appointments found.");
                return List.of();
            }
        } catch (Exception ex) {
            log.error("Error fetching appointments from Appointment Management Service: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to fetch appointments", ex);
        }
    }

    /**
     * Fetch all patients from the Patient Management Service.
     *
     * @return List of PatientDto objects.
     */
    public List<PatientDto> getAllPatients() {
        String url = baseUrl + patientsEndpoint;
        log.info("Fetching all patients from {}", url);

        try {
            PatientDto[] patientsArray = restTemplate.getForObject(url, PatientDto[].class);

            if (patientsArray != null) {
                log.info("Successfully fetched {} patients.", patientsArray.length);
                return Arrays.asList(patientsArray);
            } else {
                log.warn("No patients found.");
                return List.of();
            }
        } catch (Exception ex) {
            log.error("Error fetching patients from Patient Management Service: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to fetch patients", ex);
        }
    }
}
