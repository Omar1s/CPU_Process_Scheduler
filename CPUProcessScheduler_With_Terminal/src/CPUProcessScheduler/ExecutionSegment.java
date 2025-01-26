package CPUProcessScheduler;

public class ExecutionSegment {
    String processName;
    int startTime;
    int endTime;
    int pid;
    String action;

    public ExecutionSegment(String processName, int startTime, int endTime, int pid, String action) {
        this.processName = processName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pid = pid;
        this.action = action;
    }
}
