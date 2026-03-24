package com.example.query.model;

public class LogEvent {
    private long timestamp;
    private String message;
    public LogEvent() {
    }
    public LogEvent(long timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public String getMessage() {
        return message;
    }
}
