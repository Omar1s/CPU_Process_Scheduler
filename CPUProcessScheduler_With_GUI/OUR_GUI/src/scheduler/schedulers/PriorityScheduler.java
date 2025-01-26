package scheduler.schedulers;

import scheduler.models.PCB;
import scheduler.models.ExecutionSegment;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * PriorityScheduler Class implementing Non-Preemptive Priority Scheduling.
 */
public class PriorityScheduler {
    int contextSwitchTime;

    public PriorityScheduler(int contextSwitchTime) {
        this.contextSwitchTime = contextSwitchTime;
    }

    public List<ExecutionSegment> executeScheduling(List<PCB> processes) {
        List<ExecutionSegment> timeline = new ArrayList<>();
        int currentTime = 0;
        int completedCount = 0;
        int totalProcesses = processes.size();
        boolean[] isCompleted = new boolean[totalProcesses];

        // Sort processes based on arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (completedCount != totalProcesses) {
            PCB currentProcess = null;
            int highestPriority = Integer.MAX_VALUE;
            int processIdx = -1;

            for (int i = 0; i < totalProcesses; i++) {
                PCB proc = processes.get(i);
                if (proc.arrivalTime <= currentTime && !isCompleted[i]) {
                    if (proc.priorityLevel < highestPriority) {
                        highestPriority = proc.priorityLevel;
                        currentProcess = proc;
                        processIdx = i;
                    } else if (proc.priorityLevel == highestPriority && proc.arrivalTime < currentProcess.arrivalTime) {
                        currentProcess = proc;
                        processIdx = i;
                    }
                }
            }

            if (currentProcess == null) {
                currentTime++;
                continue;
            }

            if (currentProcess.startTime == -1) {
                currentProcess.startTime = currentTime;
            }

            timeline.add(new ExecutionSegment(
                    currentProcess.processName,
                    currentTime,
                    currentTime + currentProcess.totalBurst,
                    currentProcess.processID,
                    "Executed",
                    currentProcess.color // Pass Color
            ));
            currentTime += currentProcess.totalBurst;
            currentProcess.finishTime = currentTime;
            currentProcess.turnaroundTime = currentProcess.finishTime - currentProcess.arrivalTime;
            currentProcess.waitTime = currentProcess.turnaroundTime - currentProcess.totalBurst;
            isCompleted[processIdx] = true;
            completedCount++;
            currentTime += contextSwitchTime;
        }

        return timeline;
    }
}
