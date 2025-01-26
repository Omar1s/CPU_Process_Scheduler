package scheduler.schedulers;

import scheduler.models.PCB;
import scheduler.models.ExecutionSegment;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * SRTFScheduler Class implementing Shortest Remaining Time First Scheduling.
 */
public class SRTFScheduler {
    int contextSwitchTime;

    public SRTFScheduler(int contextSwitchTime) {
        this.contextSwitchTime = contextSwitchTime;
    }

    public List<ExecutionSegment> executeScheduling(List<PCB> processes) {
        List<ExecutionSegment> timeline = new ArrayList<>();
        int currentTime = 0;
        int completedCount = 0;
        int totalProcesses = processes.size();
        int lastProcessID = -1;
        int remainingCSTime = 0;

        // Initialize remainingBurst for each process
        for (PCB proc : processes) {
            proc.remainingBurst = proc.totalBurst;
        }

        while (completedCount != totalProcesses) {
            PCB shortestProcess = null;
            int processIdx = -1;
            int minRemaining = Integer.MAX_VALUE;

            for (int i = 0; i < totalProcesses; i++) {
                PCB proc = processes.get(i);
                if (proc.arrivalTime <= currentTime && proc.remainingBurst > 0) {
                    if (proc.remainingBurst < minRemaining) {
                        minRemaining = proc.remainingBurst;
                        shortestProcess = proc;
                        processIdx = i;
                    } else if (proc.remainingBurst == minRemaining && proc.arrivalTime < shortestProcess.arrivalTime) {
                        shortestProcess = proc;
                        processIdx = i;
                    }
                }
            }

            if (shortestProcess == null) {
                currentTime++;
                continue;
            }

            if (lastProcessID != shortestProcess.processID) {
                if (remainingCSTime > 0) {
                    remainingCSTime--;
                    currentTime++;
                    continue;
                }
                remainingCSTime = contextSwitchTime;
            }

            if (shortestProcess.startTime == -1) {
                shortestProcess.startTime = currentTime;
            }

            timeline.add(new ExecutionSegment(
                    shortestProcess.processName,
                    currentTime,
                    currentTime + 1,
                    shortestProcess.processID,
                    "Executed",
                    shortestProcess.color // Pass Color
            ));
            shortestProcess.remainingBurst--;
            currentTime++;

            if (shortestProcess.remainingBurst == 0) {
                shortestProcess.finishTime = currentTime;
                shortestProcess.turnaroundTime = shortestProcess.finishTime - shortestProcess.arrivalTime;
                shortestProcess.waitTime = shortestProcess.turnaroundTime - shortestProcess.totalBurst;
                completedCount++;
                lastProcessID = -1;
                remainingCSTime = contextSwitchTime;
            } else {
                lastProcessID = shortestProcess.processID;
            }

            if (remainingCSTime > 0) {
                remainingCSTime--;
            }
        }

        return timeline;
    }
}
