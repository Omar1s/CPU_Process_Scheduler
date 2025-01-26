package scheduler.schedulers;

import scheduler.models.FCAProcess;
import scheduler.models.FCADurationSegment;

import javafx.scene.paint.Color;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * FCAScheduler Class implementing the FCAI Scheduling algorithm.
 */
public class FCAScheduler {
    public static StringBuilder logMessages;
    public static StringBuilder quantumLogs; // Quantum logs are now accessible
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

        while ((processIndex < processes.size()) || (!readyList.isEmpty())) {
            // Add all processes that have arrived by currentTime to the readyList
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
            if (readyList.get(0).burstDuration == 0) {
                readyList.get(0).setCompletionTime(currentTime);

                executionSegments.add(new FCADurationSegment(
                        readyList.get(0).getName(), lastOperationTime, currentTime, readyList.get(0).pid,
                        "Finished Executing", readyList.get(0).burstDuration, readyList.get(0).arrivalTime, readyList.get(0).color
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
                if (readyList.get(0).burstDuration != 0 && remainingQuantum.get(0) == 0) {
//                    logMessages.append(MessageFormat.format("{0} factor updated :{1}{2}{3} {4}=>{5} new quantum {6}\n",
//                            currentTime,
//                            10 - readyList.get(factorIndex).priority,
//                            (int) Math.ceil(readyList.get(factorIndex).arrivalTime / scalingFactorV1),
//                            (int) Math.ceil(readyList.get(factorIndex).burstDuration / scalingFactorV2),
//                            readyList.get(0).getName(),
//                            readyList.get(factorIndex).getName(),
//                            readyList.get(factorIndex).getQuantum()
//                    ));

                    executionSegments.add(new FCADurationSegment(
                            readyList.get(0).getName(), lastOperationTime, currentTime, readyList.get(0).pid,
                            "Finished Quantum", readyList.get(0).burstDuration, readyList.get(0).arrivalTime, readyList.get(0).color
                    ));

                    lastOperationTime = currentTime;

                    // Old Quantum
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
//                    logMessages.append(MessageFormat.format("{0} factor updated :{1}{2}{3} {4}=>{5} new quantum {6}\n",
//                            currentTime,
//                            10 - readyList.get(factorIndex).priority,
//                            (int) Math.ceil(readyList.get(factorIndex).arrivalTime / scalingFactorV1),
//                            (int) Math.ceil(readyList.get(factorIndex).burstDuration / scalingFactorV2),
//                            readyList.get(0).getName(),
//                            readyList.get(factorIndex).getName(),
//                            readyList.get(factorIndex).getQuantum()
//                    ));

                    executionSegments.add(new FCADurationSegment(
                            readyList.get(0).getName(), lastOperationTime, currentTime, readyList.get(0).pid,
                            "Swapped", readyList.get(0).burstDuration, readyList.get(0).arrivalTime, readyList.get(0).color
                    ));

                    lastOperationTime = currentTime;

                    // Record Quantum update before modification
                    int oldQuantum = readyList.get(0).getQuantum();
                    readyList.get(0).setQuantum(readyList.get(0).getQuantum() + remainingQuantum.get(0));
                    int newQuantumSwap = readyList.get(0).getQuantum();
                    quantumLogs.append(MessageFormat.format("Process {0} Quantum updated from {1} to {2} at time {3}\n",
                            readyList.get(0).getName(),
                            oldQuantum,
                            newQuantumSwap,
                            currentTime
                    ));

                    remainingQuantum.set(0, readyList.get(0).getQuantum());

                    Collections.swap(readyList, 0, factorIndex);
                    Collections.swap(remainingQuantum, 0, factorIndex);

                    // Add context switching time after process swap
                    currentTime += contextSwitchTime;
                    lastOperationTime = currentTime;
                }
            }

            if (!readyList.isEmpty()) {
                // Set startTime if it's the first execution
                FCAProcess currentProcess = readyList.get(0);
                if (currentProcess.startTime == -1) {
                    currentProcess.startTime = currentTime;
                }

                currentProcess.burstDuration--;
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
        scalingFactorV1 = (processes.get(processes.size() - 1).arrivalTime > 10)
                ? (float) (processes.get(processes.size() - 1).arrivalTime / 10.0)
                : 1;
    }

    private void updateScalingFactorV2(ArrayList<FCAProcess> processes) {
        int maxBurst = 0;
        for (FCAProcess proc : processes) {
            maxBurst = Math.max(maxBurst, proc.burstDuration);
        }
        scalingFactorV2 = (maxBurst > 10) ? (float) (maxBurst / 10.0) : 1;
    }

    private int computeFCAIFactor(ArrayList<FCAProcess> processes) {
        factors.clear();
        for (FCAProcess proc : processes) {
            int factor = (10 - proc.priority)
                    + (int) Math.ceil(proc.arrivalTime / scalingFactorV1)
                    + (int) Math.ceil(proc.burstDuration / scalingFactorV2);
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

    public void printQuantumLogs() {
        System.out.println("Quantum Updates:");
        System.out.println(quantumLogs.toString());
    }
}
