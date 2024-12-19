package com.meditrack.aggregator_service.service;

import com.meditrack.aggregator_service.dto.*;
import com.meditrack.aggregator_service.util.ExternalApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for aggregating data from Appointment and Patient entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataAggregatorServiceImpl implements DataAggregatorService {

    private final JdbcTemplate jdbcTemplate;
    private final ExternalApiUtil externalApiUtil;

    @Override
    public void aggregateData() {
        log.info("Starting data aggregation...");

        List<AppointmentDto> appointments = externalApiUtil.getAllAppointments();
        List<PatientDto> patients = externalApiUtil.getAllPatients();

        // Aggregate and push appointments per doctor
        truncateTable("appointments_per_doctor");
        List<AppointmentsPerDoctorDto> appointmentsPerDoctor = aggregateAppointmentsPerDoctor(appointments);
        pushAppointmentsPerDoctorToRedshift(appointmentsPerDoctor);

        // Aggregate and push appointments over time
        truncateTable("appointments_over_time");
        List<AppointmentsOverTimeDto> appointmentsOverTime = aggregateAppointmentsOverTime(appointments);
        pushAppointmentsOverTimeToRedshift(appointmentsOverTime);

        // Aggregate and push symptoms by specialty
        truncateTable("symptoms_by_specialty");
        List<SymptomsBySpecialtyDto> symptomsBySpecialty = aggregateSymptomsBySpecialty(patients);
        pushSymptomsBySpecialtyToRedshift(symptomsBySpecialty);

        log.info("Data aggregation completed.");
    }

    /**
     * Truncate a table in Redshift.
     *
     * @param tableName Name of the table to truncate.
     */
    private void truncateTable(String tableName) {
        log.info("Truncating table: {}", tableName);
        String sql = String.format("TRUNCATE TABLE %s", tableName);
        jdbcTemplate.execute(sql);
        log.info("Truncated table: {}", tableName);
    }

    /**
     * Aggregates the number of appointments per doctor.
     */
    private List<AppointmentsPerDoctorDto> aggregateAppointmentsPerDoctor(List<AppointmentDto> appointmentDtos) {
        log.info("Aggregating appointments per doctor...");
        Map<String, Long> doctorAppointments = appointmentDtos.stream()
                .collect(Collectors.groupingBy(AppointmentDto::getDoctorId, Collectors.counting()));

        return doctorAppointments.entrySet().stream()
                .map(entry -> new AppointmentsPerDoctorDto(entry.getKey(), "Doctor_" + entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    private void pushAppointmentsPerDoctorToRedshift(List<AppointmentsPerDoctorDto> data) {
        log.info("Pushing appointments per doctor to Redshift...");
        String sql = "INSERT INTO appointments_per_doctor (doctor_id, doctor_name, appointment_count, updated_at) VALUES (?, ?, ?, ?)";
        data.forEach(record -> jdbcTemplate.update(sql, record.getDoctorId(), record.getDoctorName(), record.getAppointmentCount(), LocalDateTime.now()));
    }

    /**
     * Aggregates the frequency of appointments over time.
     */
    private List<AppointmentsOverTimeDto> aggregateAppointmentsOverTime(List<AppointmentDto> appointmentDtos) {
        log.info("Aggregating appointments over time...");
        Map<String, Long> appointmentsByDate = appointmentDtos.stream()
                .collect(Collectors.groupingBy(a -> a.getAppointmentDate().toLocalDate().toString(), Collectors.counting()));

        return appointmentsByDate.entrySet().stream()
                .map(entry -> new AppointmentsOverTimeDto(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    private void pushAppointmentsOverTimeToRedshift(List<AppointmentsOverTimeDto> data) {
        log.info("Pushing appointments over time to Redshift...");
        String sql = "INSERT INTO appointments_over_time (period, appointment_count, updated_at) VALUES (?, ?, ?)";
        data.forEach(record -> jdbcTemplate.update(sql, record.getPeriod(), record.getAppointmentCount(), LocalDateTime.now()));
    }

    /**
     * Aggregates the frequency of symptoms by specialty.
     */
    private List<SymptomsBySpecialtyDto> aggregateSymptomsBySpecialty(List<PatientDto> patientDtos) {
        log.info("Aggregating symptoms by specialty...");
        Map<String, Map<String, Long>> specialtySymptoms = patientDtos.stream()
                .flatMap(patientDto -> patientDto.getMedicalHistory().stream().map(symptom -> Map.entry(patientDto.getGender(), symptom)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.groupingBy(Map.Entry::getValue, Collectors.counting())));

        return specialtySymptoms.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .map(innerEntry -> new SymptomsBySpecialtyDto(entry.getKey(), innerEntry.getKey(), innerEntry.getValue().intValue())))
                .collect(Collectors.toList());
    }

    private void pushSymptomsBySpecialtyToRedshift(List<SymptomsBySpecialtyDto> data) {
        log.info("Pushing symptoms by specialty to Redshift...");
        String sql = "INSERT INTO symptoms_by_specialty (specialty, symptom, occurrence_count, updated_at) VALUES (?, ?, ?, ?)";
        data.forEach(record -> jdbcTemplate.update(sql, record.getSpecialty(), record.getSymptom(), record.getOccurrenceCount(), LocalDateTime.now()));
    }
}
