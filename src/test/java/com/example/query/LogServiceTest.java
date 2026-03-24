package com.example.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.query.model.LogEvent;
import com.example.query.service.LogService;

@SpringBootTest
public class LogServiceTest {

    private LogService logService;

    @BeforeEach
    void setUp() {
        logService = new LogService();
    }

    @Test
    void testExactQueryAndMightContain() {
        String message = "Exact log message";
        logService.addLog(new LogEvent(System.currentTimeMillis(), message));

        assertTrue(logService.mightContain(message));

        List<LogEvent> exact = logService.queryExact(message);
        assertEquals(1, exact.size());
        assertEquals(message, exact.get(0).getMessage());
    }

    @Test
    void testSubstringQuery() {
        logService.addLog(new LogEvent(System.currentTimeMillis(), "Hello World"));
        logService.addLog(new LogEvent(System.currentTimeMillis(), "Hello Bloom"));

        List<LogEvent> substringResult = logService.querySubstring("Hello");
        assertEquals(2, substringResult.size());

        List<LogEvent> substringResult2 = logService.querySubstring("World");
        assertEquals(1, substringResult2.size());
        assertEquals("Hello World", substringResult2.get(0).getMessage());
    }

    @Test
    void testExactQueryWithTimeRange() {
        long now = System.currentTimeMillis();
        logService.addLog(new LogEvent(now - 10000, "Old log"));
        logService.addLog(new LogEvent(now, "Current log"));

        List<LogEvent> result = logService.queryExact("Current log", now - 5000, now + 5000);
        assertEquals(1, result.size());
        assertEquals("Current log", result.get(0).getMessage());
    }

    @Test
    void testSubstringQueryWithTimeRange() {
        long now = System.currentTimeMillis();
        logService.addLog(new LogEvent(now - 10000, "Old log"));
        logService.addLog(new LogEvent(now, "Current log"));

        List<LogEvent> result = logService.querySubstring("log", now - 5000, now + 5000);
        assertEquals(1, result.size());
        assertEquals("Current log", result.get(0).getMessage());
    }

    @Test
    void testCleanupOldLogsByTime() {
        long now = System.currentTimeMillis();
        LogEvent oldLog = new LogEvent(now - 8L * 24 * 60 * 60 * 1000, "Old log");
        LogEvent newLog = new LogEvent(now, "New log");

        logService.addLog(oldLog);
        logService.addLog(newLog);

        logService.cleanupOldLogs();

        List<LogEvent> allLogs = logService.getAllLogs();
        assertEquals(1, allLogs.size());
        assertEquals("New log", allLogs.get(0).getMessage());
    }
    
}
