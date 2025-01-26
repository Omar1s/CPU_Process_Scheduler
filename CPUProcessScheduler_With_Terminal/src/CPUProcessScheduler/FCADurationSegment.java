package CPUProcessScheduler;

public class FCADurationSegment {
    public String processName;
    public int start;
    public int end;
    public int pid;
    public String action;
    public int burstDuration;
    public int arrivalTime;

    public FCADurationSegment(String processName, int start, int end, int pid, String action, int burstDuration, int arrivalTime) {
        this.processName = processName;
        this.start = start;
        this.end = end;
        this.pid = pid;
        this.action = action;
        this.burstDuration = burstDuration;
        this.arrivalTime = arrivalTime;
    }

    public void updateEnd(int newEnd) {
        this.end = newEnd;
    }
}
