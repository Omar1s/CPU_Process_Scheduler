package scheduler.schedulers;

import scheduler.models.ExecutionSegment;
import scheduler.models.FCADurationSegment;
import scheduler.models.PCB;
import scheduler.models.FCAProcess;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * SchedulerManager Class to handle scheduling execution.
 */
public class SchedulerManager {
    public static List<ExecutionSegment> runScheduling(String algorithm, int csTime, List<PCB> processes, ArrayList<FCAProcess> fcaiProcesses) {
        List<ExecutionSegment> timeline = new ArrayList<>();

        switch (algorithm) {
            case "Non-Preemptive Priority Scheduling":
                PriorityScheduler priorityScheduler = new PriorityScheduler(csTime);
                timeline = priorityScheduler.executeScheduling(processes);
                break;
            case "Non-Preemptive Shortest Job First (SJF)":
                SJFScheduler sjfScheduler = new SJFScheduler(csTime);
                timeline = sjfScheduler.executeScheduling(processes);
                break;
            case "Shortest-Remaining Time First (SRTF)":
                SRTFScheduler srtfScheduler = new SRTFScheduler(csTime);
                timeline = srtfScheduler.executeScheduling(processes);
                break;
            case "FCAI Scheduling":
                FCAScheduler fcaiScheduler = new FCAScheduler(csTime);
                List<FCADurationSegment> fcaiResult = fcaiScheduler.execute(fcaiProcesses);
                timeline = convertFCADurationToExecution(fcaiResult);
                break;
            default:
                break;
        }

        return timeline;
    }

    private static List<ExecutionSegment> convertFCADurationToExecution(List<FCADurationSegment> fcaiResult) {
        List<ExecutionSegment> executionSegments = new ArrayList<>();
        for (FCADurationSegment seg : fcaiResult) {
            executionSegments.add(new ExecutionSegment(
                    seg.processName, seg.start, seg.end, seg.pid, seg.action, seg.color
            ));
        }
        return executionSegments;
    }
}
