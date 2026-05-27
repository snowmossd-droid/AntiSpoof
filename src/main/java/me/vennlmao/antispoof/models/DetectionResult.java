package me.vennlmao.antispoof.models;

public class DetectionResult {
    private final boolean detected;
    private final String clientName;
    private final String reason;

    private DetectionResult(boolean detected, String clientName, String reason) {
        this.detected = detected; this.clientName = clientName; this.reason = reason;
    }
    public static DetectionResult clean() { return new DetectionResult(false, "", ""); }
    public static DetectionResult flag(String c, String r) { return new DetectionResult(true, c, r); }
    public boolean isDetected() { return detected; }
    public String getClientName() { return clientName; }
    public String getReason() { return reason; }
}
