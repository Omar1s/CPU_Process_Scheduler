package scheduler.models;

import javafx.scene.paint.Color;

/**
 * FCAProcess Class representing a process for FCAI Scheduling.
 */
public class FCAProcess {
    public String name;
    public int arrivalTime;
    public int burstDuration;
    public int priority;
    public int quantum;
    public int pid;
    public Color color; // Added Color Field

    private int originalBurst;
    private int completionTimestamp;
    public int startTime = -1; // Added startTime

    public FCAProcess(String name, int arrivalTime, int burstDuration, int priority, int quantum, int pid, Color color) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstDuration = burstDuration;
        this.originalBurst = burstDuration;
        this.priority = priority;
        this.quantum = quantum;
        this.pid = pid;
        this.color = color; // Initialize Color
    }

    public String getName() {
        return name;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public int getPid() {
        return pid;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getOriginalBurst() {
        return originalBurst;
    }

    public int getCompletionTime() {
        return completionTimestamp;
    }

    public void setCompletionTime(int completionTimestamp) {
        this.completionTimestamp = completionTimestamp;
    }

    public FCAProcess duplicate() {
        FCAProcess clone = new FCAProcess(this.name, this.arrivalTime, this.burstDuration, this.priority, this.quantum, this.pid, this.color);
        clone.completionTimestamp = this.completionTimestamp;
        clone.startTime = this.startTime;
        return clone;
    }
}
