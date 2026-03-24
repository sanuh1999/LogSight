package com.example.query.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.query.model.LogEvent;
import com.example.query.service.LogService;

@RestController
@RequestMapping("/logs")
public class LogController {
   
    @Autowired
    private LogService logService;

    // Ingest a log
    @PostMapping("/ingest")
    public String ingestLog(@RequestParam String message) {
        LogEvent logEvent = new LogEvent(System.currentTimeMillis(), message);
        logService.addLog(logEvent);
        return "Log ingested";
    }

    /**
     * Query logs.
     *
     * @param message   message string (exact or substring)
     * @param exact     true = exact match, false = substring match
     * @param startTime optional start timestamp in millis
     * @param endTime   optional end timestamp in millis
     * @return list of logs
     */
    @GetMapping("/query")
    public List<LogEvent> queryLogs(@RequestParam String message,
                                    @RequestParam(defaultValue = "true") boolean exact,
                                    @RequestParam(required = false) Long startTime,
                                    @RequestParam(required = false) Long endTime) {

        if (exact) {
            if (startTime != null && endTime != null) {
                return logService.queryExact(message, startTime, endTime);
            } else {
                return logService.queryExact(message);
            }
        } else {
            if (startTime != null && endTime != null) {
                return logService.querySubstring(message, startTime, endTime);
            } else {
                return logService.querySubstring(message);
            }
        }
    }

    // Check if exact log might exist (Bloom filter)
    @GetMapping("/mightContain")
    public boolean mightContain(@RequestParam String message) {
        return logService.mightContain(message);
    }

    // Get all logs
    @GetMapping("/all")
    public List<LogEvent> getAllLogs() {
        return logService.getAllLogs();
    }
}
