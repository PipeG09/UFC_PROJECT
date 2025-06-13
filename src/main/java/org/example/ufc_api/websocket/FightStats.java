package org.example.ufc_api.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FightStats {

    @JsonProperty("blueStrikes")
    private int blueStrikes;

    @JsonProperty("redStrikes")
    private int redStrikes;

    @JsonProperty("blueTakedowns")
    private int blueTakedowns;

    @JsonProperty("redTakedowns")
    private int redTakedowns;

    @JsonProperty("blueCageControl")
    private int blueCageControl; // segundos

    @JsonProperty("redCageControl")
    private int redCageControl;  // segundos

    @JsonProperty("blueProbability")
    private int blueProbability; // porcentaje

    @JsonProperty("redProbability")
    private int redProbability;  // porcentaje

    @JsonProperty("currentRound")
    private int currentRound;

    @JsonProperty("timeRemaining")
    private String timeRemaining;

    @JsonProperty("fightStatus")
    private String fightStatus;

    @JsonProperty("eventName")
    private String eventName;

    // Constructor vacío
    public FightStats() {
        this.fightStatus = "LIVE";
        this.eventName = "UFC 300 - Main Event";
    }

    // Constructor completo
    public FightStats(int blueStrikes, int redStrikes, int blueTakedowns, int redTakedowns,
                      int blueCageControl, int redCageControl, int blueProbability, int redProbability,
                      int currentRound, String timeRemaining) {
        this();
        this.blueStrikes = blueStrikes;
        this.redStrikes = redStrikes;
        this.blueTakedowns = blueTakedowns;
        this.redTakedowns = redTakedowns;
        this.blueCageControl = blueCageControl;
        this.redCageControl = redCageControl;
        this.blueProbability = blueProbability;
        this.redProbability = redProbability;
        this.currentRound = currentRound;
        this.timeRemaining = timeRemaining;
    }

    // Getters y setters
    public int getBlueStrikes() {
        return blueStrikes;
    }

    public void setBlueStrikes(int blueStrikes) {
        this.blueStrikes = blueStrikes;
    }

    public int getRedStrikes() {
        return redStrikes;
    }

    public void setRedStrikes(int redStrikes) {
        this.redStrikes = redStrikes;
    }

    public int getBlueTakedowns() {
        return blueTakedowns;
    }

    public void setBlueTakedowns(int blueTakedowns) {
        this.blueTakedowns = blueTakedowns;
    }

    public int getRedTakedowns() {
        return redTakedowns;
    }

    public void setRedTakedowns(int redTakedowns) {
        this.redTakedowns = redTakedowns;
    }

    public int getBlueCageControl() {
        return blueCageControl;
    }

    public void setBlueCageControl(int blueCageControl) {
        this.blueCageControl = blueCageControl;
    }

    public int getRedCageControl() {
        return redCageControl;
    }

    public void setRedCageControl(int redCageControl) {
        this.redCageControl = redCageControl;
    }

    public int getBlueProbability() {
        return blueProbability;
    }

    public void setBlueProbability(int blueProbability) {
        this.blueProbability = blueProbability;
    }

    public int getRedProbability() {
        return redProbability;
    }

    public void setRedProbability(int redProbability) {
        this.redProbability = redProbability;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public String getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(String timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public String getFightStatus() {
        return fightStatus;
    }

    public void setFightStatus(String fightStatus) {
        this.fightStatus = fightStatus;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    // Métodos de utilidad
    public String getFormattedBlueCageControl() {
        return formatTime(blueCageControl);
    }

    public String getFormattedRedCageControl() {
        return formatTime(redCageControl);
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }

    public int getTotalStrikes() {
        return blueStrikes + redStrikes;
    }

    public int getTotalTakedowns() {
        return blueTakedowns + redTakedowns;
    }

    @Override
    public String toString() {
        return "FightStats{" +
                "blueStrikes=" + blueStrikes +
                ", redStrikes=" + redStrikes +
                ", blueTakedowns=" + blueTakedowns +
                ", redTakedowns=" + redTakedowns +
                ", blueCageControl=" + blueCageControl +
                ", redCageControl=" + redCageControl +
                ", blueProbability=" + blueProbability +
                ", redProbability=" + redProbability +
                ", currentRound=" + currentRound +
                ", timeRemaining='" + timeRemaining + '\'' +
                ", fightStatus='" + fightStatus + '\'' +
                ", eventName='" + eventName + '\'' +
                '}';
    }
}