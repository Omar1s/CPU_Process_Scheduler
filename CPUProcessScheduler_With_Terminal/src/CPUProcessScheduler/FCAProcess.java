package CPUProcessScheduler;

public class FCAProcess {
    private String name;
    private int arrivalTime;
    private int burstDuration;
    private int priority;
    private int quantum;
    private int pid;

    private int originalBurst;
    private int completionTimestamp;

    public FCAProcess(String name, int arrivalTime, int burstDuration, int priority, int quantum, int pid) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstDuration = burstDuration;
        this.originalBurst = burstDuration;
        this.priority = priority;
        this.quantum = quantum;
        this.pid = pid;
    }
    public int getBurstDuration(){
        return burstDuration;
    }
    public int getPriority(){
        return priority;
    }

    public String getName() {
        return name;
    }

    public int getQuantum() {
        return quantum;
    }
    public void setBurstDuration(int brust){
        burstDuration=brust;

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
        return new FCAProcess(name, arrivalTime, burstDuration, priority, quantum, pid);
    }
}
