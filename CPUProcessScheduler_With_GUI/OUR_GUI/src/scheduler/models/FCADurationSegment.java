package scheduler.models;

import javafx.scene.paint.Color;

/**
 * FCADurationSegment Class representing the execution period of a process in FCAI Scheduling.
 */
public class FCADurationSegment {
    public String processName;
    public int start;
    public int end;
    public int pid;
    public String action;
    public int burstDuration;
    public int arrivalTime;
    public Color color; // Added Color Field

    public FCADurationSegment(String processName, int start, int end, int pid, String action, int burstDuration, int arrivalTime, Color color) {
        this.processName = processName;
        this.start = start;
        this.end = end;
        this.pid = pid;
        this.action = action;
        this.burstDuration = burstDuration;
        this.arrivalTime = arrivalTime;
        this.color = color; // Initialize Color
    }

    public void updateEnd(int newEnd) {
        this.end = newEnd;
    }
}
