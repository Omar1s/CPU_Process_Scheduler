package CPUProcessScheduler;

import java.util.ArrayList;
import java.util.List;

public class SJFScheduler {
    int contextSwitchTime;

    public SJFScheduler(int contextSwitchTime) {
        this.contextSwitchTime = contextSwitchTime;
    }

    public List<ExecutionSegment> executeScheduling(List<PCB> processes) {
        List<ExecutionSegment> timeline = new ArrayList<>();
        int currentTime = 0;
        int completedCount = 0;
        int totalProcesses = processes.size();
        boolean[] isCompleted = new boolean[totalProcesses];

        while (completedCount != totalProcesses) {
            PCB currentProcess = null;
            int shortestBurst = Integer.MAX_VALUE;
            int processIdx = -1;

            for (int i = 0; i < totalProcesses; i++) {
                PCB proc = processes.get(i);
                if (proc.arrivalTime <= currentTime && !isCompleted[i]) {
                    if (proc.totalBurst < shortestBurst) {
                        shortestBurst = proc.totalBurst;
                        currentProcess = proc;
                        processIdx = i;
                    } else if (proc.totalBurst == shortestBurst && proc.arrivalTime < currentProcess.arrivalTime) {
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
                    "Executed"
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
