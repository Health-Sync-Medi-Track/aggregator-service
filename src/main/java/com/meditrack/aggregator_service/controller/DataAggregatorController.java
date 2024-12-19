package com.meditrack.aggregator_service.controller;

import com.meditrack.aggregator_service.service.DataAggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing Data Aggregator Service.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/aggregate")
public class DataAggregatorController {

    private final DataAggregatorService dataAggregatorService;

    /**
     * Endpoint to manually trigger the data aggregation process.
     *
     * @return ResponseEntity with a message indicating the result of the operation.
     */
    @GetMapping("/process/cron-job")
    public ResponseEntity<String> runAggregation() {
        try {
            log.info("Initiating data aggregation process.");
            dataAggregatorService.aggregateData();
            log.info("Data aggregation process completed successfully.");
            return ResponseEntity.ok("Data aggregation process initiated.");
        } catch (Exception e) {
            log.error("Error occurred during data aggregation: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to initiate data aggregation process.");
        }
    }
}
