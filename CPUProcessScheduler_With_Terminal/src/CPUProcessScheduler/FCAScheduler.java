package CPUProcessScheduler;

import java.text.MessageFormat;
import java.util.*;

public class FCAScheduler {
    public static StringBuilder logMessages;
    public static StringBuilder quantumLogs;
    public ArrayList<FCAProcess> readyList = new ArrayList<>();
    public ArrayList<FCAProcess> completedList = new ArrayList<>();
    public ArrayList<Integer> remainingQuantum = new ArrayList<>();
    public ArrayList<Integer> factors = new ArrayList<>();
    public float scalingFactorV1;
    public float scalingFactorV2;
    private int contextSwitchTime;

    public FCAScheduler(int contextSwitchTime) {
        logMessages = new StringBuilder();
        quantumLogs = new StringBuilder();
        this.contextSwitchTime = contextSwitchTime;
    }


    public ArrayList<FCADurationSegment> execute(ArrayList<FCAProcess> processes) {
        ArrayList<FCADurationSegment> executionSegments = new ArrayList<>();
        sortProcesses(processes, Comparator.comparing(FCAProcess::getArrivalTime));
        initializeScalingFactors(processes);

        int currentTime = 0, processIndex = 0, lastOperationTime = 0;
        boolean isFirstProcess = true;

        while ((processIndex < processes.size()) || (!readyList.isEmpty())) {
            for (; processIndex < processes.size(); processIndex++) {
                if (processes.get(processIndex).getArrivalTime() <= currentTime) {
                    readyList.add(processes.get(processIndex));
                    remainingQuantum.add(processes.get(processIndex).getQuantum());
                } else {
                    break;
                }
            }

            if (readyList.isEmpty()) {
                currentTime++;
                continue;
            }

            updateScalingFactorV2(readyList);
            if (readyList.get(0).getBurstDuration() == 0) {
                readyList.get(0).setCompletionTime(currentTime);

                executionSegments.add(new FCADurationSegment(
                        readyList.get(0).getName(), lastOperationTime, currentTime, readyList.get(0).getPid(),
                        "finished executing", readyList.get(0).getBurstDuration(), readyList.get(0).getArrivalTime()
                ));

                lastOperationTime = currentTime;
                readyList.get(0).setQuantum(0);
                completedList.add(readyList.get(0));
                readyList.remove(0);
                remainingQuantum.remove(0);


                currentTime += contextSwitchTime;
                lastOperationTime = currentTime;
            }

            if (readyList.isEmpty()) {
                currentTime++;
                continue;
            }

            int factorIndex = computeFCAIFactor(readyList);
            if (readyList.get(0).getQuantum() - remainingQuantum.get(0) >= Math.ceil(readyList.get(0).getQuantum() * 0.4)) {
                if (readyList.get(0).getBurstDuration() != 0 && remainingQuantum.get(0) == 0) {
                    logMessages.append(MessageFormat.format("{0} factor updated :{1}{2}{3} {4}=>{5} new quantum {6}\n",
                            currentTime,
                            10 - readyList.get(factorIndex).getPriority(),
                            (int) Math.ceil(readyList.get(factorIndex).getArrivalTime() / scalingFactorV1),
                            (int) Math.ceil(readyList.get(factorIndex).getBurstDuration() / scalingFactorV2),
                            readyList.get(0).getName(),
                            readyList.get(factorIndex).getName(),
                            readyList.get(factorIndex).getQuantum()
                    ));

                    executionSegments.add(new FCADurationSegment(
                            readyList.get(0).getName(), lastOperationTime, currentTime, readyList.get(0).getPid(),
                            "finished its quantum", readyList.get(0).getBurstDuration(), readyList.get(0).getArrivalTime()
                    ));

                    lastOperationTime = currentTime;


                    int oldQuantum = readyList.get(0).getQuantum();
                    FCAProcess currentProcess = readyList.get(0);
                    currentProcess.setQuantum(currentProcess.getQuantum() + 2);
                    int newQuantum = currentProcess.getQuantum();

                    quantumLogs.append(MessageFormat.format("Process {0} Quantum updated from {1} to {2} at time {3}\n",
                            currentProcess.getName(),
                            oldQuantum,
                            newQuantum,
                            currentTime
                    ));

                    readyList.remove(0);
                    remainingQuantum.remove(0);
                    readyList.add(currentProcess);
                    remainingQuantum.add(currentProcess.getQuantum());

                    currentTime += contextSwitchTime;
                    lastOperationTime = currentTime;
                } else if (factorIndex != 0) {
                    logMessages.append(MessageFormat.format("{0} factor updated :{1}{2}{3} {4}=>{5} new quantum {6}\n",
                            currentTime,
                            10 - readyList.get(factorIndex).getPriority(),
                            (int) Math.ceil(readyList.get(factorIndex).getArrivalTime() / scalingFactorV1),
                            (int) Math.ceil(readyList.get(factorIndex).getBurstDuration() / scalingFactorV2),
                            readyList.get(0).getName(),
                            readyList.get(factorIndex).getName(),
                            readyList.get(factorIndex).getQuantum()
                    ));

                    executionSegments.add(new FCADurationSegment(
                            readyList.get(0).getName(), lastOperationTime, currentTime, readyList.get(0).getPid(),
                            "swapped", readyList.get(0).getBurstDuration(), readyList.get(0).getArrivalTime()
                    ));

                    lastOperationTime = currentTime;


                    int oldQuantum = readyList.get(0).getQuantum();
                    readyList.get(0).setQuantum(readyList.get(0).getQuantum() + remainingQuantum.get(0));
                    int newQuantum = readyList.get(0).getQuantum();
                    quantumLogs.append(MessageFormat.format("Process {0} Quantum updated from {1} to {2} at time {3}\n",
                            readyList.get(0).getName(),
                            oldQuantum,
                            newQuantum,
                            currentTime
                    ));

                    remainingQuantum.set(0, readyList.get(0).getQuantum());

                    Collections.swap(readyList, 0, factorIndex);
                    Collections.swap(remainingQuantum, 0, factorIndex);


                    currentTime += contextSwitchTime;
                    lastOperationTime = currentTime;
                }
            }

            if (!readyList.isEmpty()) {
                readyList.get(0).setBurstDuration( readyList.get(0).getBurstDuration()-1);
                remainingQuantum.set(0, remainingQuantum.get(0) - 1);
                currentTime++;
            }
        }

        return executionSegments;
    }

    private void sortProcesses(ArrayList<FCAProcess> processes, Comparator<FCAProcess> comparator) {
        processes.sort(comparator);
    }

    private void initializeScalingFactors(ArrayList<FCAProcess> processes) {
        scalingFactorV1 = (processes.get(processes.size() - 1).getArrivalTime() > 10)
                ? (float) (processes.get(processes.size() - 1).getArrivalTime() / 10.0)
                : 1;
    }

    private void updateScalingFactorV2(ArrayList<FCAProcess> processes) {
        int maxBurst = 0;
        for (FCAProcess proc : processes) {
            maxBurst = Math.max(maxBurst, proc.getBurstDuration());
        }
        scalingFactorV2 = (maxBurst > 10) ? (float) (maxBurst / 10.0) : 1;
    }

    private int computeFCAIFactor(ArrayList<FCAProcess> processes) {
        factors.clear();
        for (FCAProcess proc : processes) {
            int factor = (10 - proc.getPriority())
                    + (int) Math.ceil(proc.getArrivalTime() / scalingFactorV1)
                    + (int) Math.ceil(proc.getBurstDuration() / scalingFactorV2);
            factors.add(factor);
        }

        int minIndex = 0;
        for (int i = 0; i < factors.size(); i++) {
            if (factors.get(i) <= factors.get(minIndex)) {
                minIndex = i;
            }
        }
        return minIndex;
    }


    public  void printQuantumLogs() {
        System.out.println("Quantum Updates:");
        System.out.println(quantumLogs.toString());
    }
}