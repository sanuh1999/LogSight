package com.example.query.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.query.model.LogEvent;
import com.example.query.util.HashUtil;
import com.google.common.hash.BloomFilter;

@Service
public class LogService {
  private final ConcurrentMap<String, List<LogEvent>> logStore = new ConcurrentHashMap<>();
    private final BloomFilter<String> bloomFilter;

    private final long maxRetentionMillis = 7L * 24 * 60 * 60 * 1000; // 7 days
    private final int maxTotalLogs = 100_000;

    public LogService() {
        this.bloomFilter = BloomFilterFactory.createLogBloomFilter(100_000, 0.01);
    }

    // Ingest log
    public void addLog(LogEvent logEvent) {
        String hash = HashUtil.sha256(logEvent.getMessage());
        bloomFilter.put(hash);
        logStore.computeIfAbsent(hash, k -> new ArrayList<>()).add(logEvent);
    }

    // Fast exact-match check
    public boolean mightContain(String message) {
        String hash = HashUtil.sha256(message);
        return bloomFilter.mightContain(hash);
    }

    // Exact match query (uses Bloom filter)
    public List<LogEvent> queryExact(String message) {
        String hash = HashUtil.sha256(message);
        List<LogEvent> result = new ArrayList<>();
        if (!bloomFilter.mightContain(hash)) return result;

        List<LogEvent> logs = logStore.get(hash);
        if (logs != null) {
            for (LogEvent log : logs) {
                if (log.getMessage().equals(message)) result.add(log);
            }
        }
        return result;
    }

    // Exact match + time range
    public List<LogEvent> queryExact(String message, long startTime, long endTime) {
        String hash = HashUtil.sha256(message);
        List<LogEvent> result = new ArrayList<>();
        if (!bloomFilter.mightContain(hash)) return result;

        List<LogEvent> logs = logStore.get(hash);
        if (logs != null) {
            for (LogEvent log : logs) {
                if (log.getMessage().equals(message)
                        && log.getTimestamp() >= startTime
                        && log.getTimestamp() <= endTime) {
                    result.add(log);
                }
            }
        }
        return result;
    }

    // Substring search (full scan)
    public List<LogEvent> querySubstring(String substring) {
        List<LogEvent> result = new ArrayList<>();
        for (List<LogEvent> logs : logStore.values()) {
            for (LogEvent log : logs) {
                if (log.getMessage().contains(substring)) {
                    result.add(log);
                }
            }
        }
        return result;
    }

    // Substring search + time range
    public List<LogEvent> querySubstring(String substring, long startTime, long endTime) {
        List<LogEvent> result = new ArrayList<>();
        for (List<LogEvent> logs : logStore.values()) {
            for (LogEvent log : logs) {
                if (log.getMessage().contains(substring)
                        && log.getTimestamp() >= startTime
                        && log.getTimestamp() <= endTime) {
                    result.add(log);
                }
            }
        }
        return result;
    }

    public List<LogEvent> getAllLogs() {
        List<LogEvent> allLogs = new ArrayList<>();
        logStore.values().forEach(allLogs::addAll);
        return allLogs;
    }

    // Cleanup runs daily
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void cleanupOldLogs() {
        long cutoffTime = System.currentTimeMillis() - maxRetentionMillis;

        for (Map.Entry<String, List<LogEvent>> entry : logStore.entrySet()) {
            entry.getValue().removeIf(log -> log.getTimestamp() < cutoffTime);
            if (entry.getValue().isEmpty()) {
                logStore.remove(entry.getKey());
            }
        }

        // Trim to maxTotalLogs
        List<LogEvent> allLogs = getAllLogs();
        if (allLogs.size() > maxTotalLogs) {
            allLogs.sort(Comparator.comparingLong(LogEvent::getTimestamp));
            int removeCount = allLogs.size() - maxTotalLogs;

            for (int i = 0; i < removeCount; i++) {
                LogEvent log = allLogs.get(i);
                String hash = HashUtil.sha256(log.getMessage());
                List<LogEvent> list = logStore.get(hash);
                if (list != null) {
                    list.remove(log);
                    if (list.isEmpty()) logStore.remove(hash);
                }
            }
        }
    }
    
}
