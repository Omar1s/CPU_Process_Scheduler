package scheduler.models;

import javafx.scene.paint.Color;

/**
 * PCB (Process Control Block) Class representing each process with necessary attributes.
 */
public class PCB {
    public String processName;
    public int arrivalTime;
    public int totalBurst;
    public int priorityLevel;
    public int timeQuantum;
    public int processID;
    public int remainingBurst;
    public int startTime = -1;
    public int finishTime = -1;
    public int waitTime;
    public int turnaroundTime;
    public   Color color;

    public PCB(String processName, int arrivalTime, int totalBurst, int priorityLevel, int timeQuantum, int processID, Color color) {
        this.processName = processName;
        this.arrivalTime = arrivalTime;
        this.totalBurst = totalBurst;
        this.priorityLevel = priorityLevel;
        this.timeQuantum = timeQuantum;
        this.processID = processID;
        this.remainingBurst = totalBurst;
        this.color = color; // Initialize Color
    }

    public PCB clonePCB() {
        PCB clone = new PCB(this.processName, this.arrivalTime, this.totalBurst, this.priorityLevel, this.timeQuantum, this.processID, this.color);
        clone.remainingBurst = this.remainingBurst;
        clone.startTime = this.startTime;
        clone.finishTime = this.finishTime;
        clone.waitTime = this.waitTime;
        clone.turnaroundTime = this.turnaroundTime;
        return clone;
    }
}
