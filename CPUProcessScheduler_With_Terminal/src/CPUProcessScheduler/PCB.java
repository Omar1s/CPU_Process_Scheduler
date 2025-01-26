package CPUProcessScheduler;
import java.util.ArrayList;
import java.util.List;

public class PCB {
    String processName;
    int arrivalTime;
    int totalBurst;
    int priorityLevel;
    int timeQuantum;
    int processID;
    int remainingBurst;
    int startTime = -1;
    int finishTime;
    int waitTime;
    int turnaroundTime;

    List<String> quantumLogs = new ArrayList<>();

    public PCB(String processName, int arrivalTime, int totalBurst, int priorityLevel, int timeQuantum, int processID) {
        this.processName = processName;
        this.arrivalTime = arrivalTime;
        this.totalBurst = totalBurst;
        this.priorityLevel = priorityLevel;
        this.timeQuantum = timeQuantum;
        this.processID = processID;
        this.remainingBurst = totalBurst;
    }

    public PCB clonePCB() {
        PCB clone = new PCB(this.processName, this.arrivalTime, this.totalBurst, this.priorityLevel, this.timeQuantum, this.processID);
        clone.remainingBurst = this.remainingBurst;
        clone.startTime = this.startTime;
        clone.finishTime = this.finishTime;
        clone.waitTime = this.waitTime;
        clone.turnaroundTime = this.turnaroundTime;
        clone.quantumLogs = new ArrayList<>(this.quantumLogs);
        return clone;
    }
}
