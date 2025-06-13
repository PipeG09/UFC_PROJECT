package org.example.ufc_api.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class FightUpdateMessage {
    private String type;
    private Object data;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String source;

    public FightUpdateMessage() {
        this.timestamp = LocalDateTime.now();
        this.source = "UFC_LIVE_TRACKER";
    }

    public FightUpdateMessage(String type, Object data) {
        this();
        this.type = type;
        this.data = data;
    }

    public FightUpdateMessage(String type, Object data, String source) {
        this();
        this.type = type;
        this.data = data;
        this.source = source;
    }

    // Getters y setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "FightUpdateMessage{" +
                "type='" + type + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", source='" + source + '\'' +
                '}';
    }
}