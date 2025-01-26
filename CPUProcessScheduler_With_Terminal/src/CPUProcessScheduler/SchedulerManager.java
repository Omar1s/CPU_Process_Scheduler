package CPUProcessScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SchedulerManager {

    public static void initiateScheduling(List<PCB> processList, ArrayList<FCAProcess> fcaiProcesses) {
        boolean isFCAI = false;
        Scanner userInput = new Scanner(System.in);
        System.out.println("Choose Scheduling Algorithm:");
        System.out.println("1. Non-Preemptive Priority Scheduling");
        System.out.println("2. Non-Preemptive Shortest Job First (SJF)");
        System.out.println("3. Shortest-Remaining Time First (SRTF)");
        System.out.println("4. FCAI Scheduling");
        System.out.print("Enter your choice (1-4): ");
        int algorithmChoice;
        try {
            algorithmChoice = userInput.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input! Please enter a number between 1 and 4.");
            userInput.close();
            return;
        }

        int csTime = 0;


        List<PCB> clonedProcessList = new ArrayList<>();
        for (PCB proc : processList) {
            clonedProcessList.add(proc.clonePCB());
        }

        List<ExecutionSegment> schedulingResult = new ArrayList<>();
        String selectedAlgorithm = "";
        String quantumLogDetails = "";

        switch (algorithmChoice) {
            case 1:
                System.out.print("Enter Context Switching Time: ");
                csTime = getValidInteger(userInput);
                if (csTime < 0) {
                    System.out.println("Context Switching Time cannot be negative.");
                    userInput.close();
                    return;
                }
                PriorityScheduler priorityScheduler = new PriorityScheduler(csTime);
                schedulingResult = priorityScheduler.executeScheduling(clonedProcessList);
                selectedAlgorithm = "Non-Preemptive Priority Scheduling";
                break;
            case 2:
                System.out.print("Enter Context Switching Time: ");
                csTime = getValidInteger(userInput);
                if (csTime < 0) {
                    System.out.println("Context Switching Time cannot be negative.");
                    userInput.close();
                    return;
                }
                SJFScheduler sjfScheduler = new SJFScheduler(csTime);
                schedulingResult = sjfScheduler.executeScheduling(clonedProcessList);
                selectedAlgorithm = "Non-Preemptive Shortest Job First (SJF)";
                break;
            case 3:
                System.out.print("Enter Context Switching Time: ");
                csTime = getValidInteger(userInput);
                if (csTime < 0) {
                    System.out.println("Context Switching Time cannot be negative.");
                    userInput.close();
                    return;
                }
                SRTFScheduler srtfScheduler = new SRTFScheduler(csTime);
                schedulingResult = srtfScheduler.executeScheduling(clonedProcessList);
                selectedAlgorithm = "Shortest-Remaining Time First (SRTF)";
                break;
            case 4:
                isFCAI = true;
                System.out.print("Enter Context Switching Time: ");
                csTime = getValidInteger(userInput);
                if (csTime < 0) {
                    System.out.println("Context Switching Time cannot be negative.");
                    userInput.close();
                    return;
                }
                FCAScheduler fcaiScheduler = new FCAScheduler(csTime);
                ArrayList<FCADurationSegment> fcaiResult = fcaiScheduler.execute(fcaiProcesses);
                System.out.println("\n=== FCAI Scheduling ===\n");
                System.out.println("Execution Durations:");
                for (FCADurationSegment segment : fcaiResult) {
                    System.out.println("Process: " + segment.processName + ", Start: " + segment.start
                            + ", End: " + segment.end + ", Description: " + segment.action);
                }
                System.out.println("\nWaiting Time and Turnaround Time:");
                float totalWait = 0;
                float totalTurnaround = 0;
                for (FCAProcess proc : fcaiScheduler.completedList) {
                    int turnaround = proc.getCompletionTime() - proc.getArrivalTime();
                    int wait = turnaround - proc.getOriginalBurst();
                    totalWait += wait;
                    totalTurnaround += turnaround;
                    System.out.println("Process: " + proc.getName() + ", Waiting Time: " + wait
                            + ", Turnaround Time: " + turnaround);
                }
                float avgWait = totalWait / fcaiScheduler.completedList.size();
                float avgTurnaround = totalTurnaround / fcaiScheduler.completedList.size();

                System.out.println("\nAverage Waiting Time: " + avgWait);
                System.out.println("Average Turnaround Time: " + avgTurnaround);

                fcaiScheduler.printQuantumLogs();

                break;
            default:
                System.out.println("Invalid Selection!");
                userInput.close();
                return;
        }

        if (!isFCAI) {
            presentResults(schedulingResult, clonedProcessList, selectedAlgorithm, quantumLogDetails);
        }

        userInput.close();
    }

    /**
     * Method to display the scheduling results.
     */
    public static void presentResults(List<ExecutionSegment> timeline, List<PCB> processes, String algorithmName, String quantumLog) {
        System.out.println("\n=== " + algorithmName + " ===\n");

        System.out.println("Process Execution Order:");
        for (ExecutionSegment segment : timeline) {
            System.out.println(segment.processName + " executed from " + segment.startTime + " to " + segment.endTime);
        }

        System.out.println("\nWaiting Times:");
        for (PCB proc : processes) {
            System.out.println(proc.processName + ": " + proc.waitTime);
        }

        System.out.println("\nTurnaround Times:");
        for (PCB proc : processes) {
            System.out.println(proc.processName + ": " + proc.turnaroundTime);
        }

        double totalWait = 0;
        double totalTurnaround = 0;
        int totalProcesses = processes.size();
        for (PCB proc : processes) {
            totalWait += proc.waitTime;
            totalTurnaround += proc.turnaroundTime;
        }

        System.out.println("\nAverage Waiting Time: " + (totalWait / totalProcesses));
        System.out.println("Average Turnaround Time: " + (totalTurnaround / totalProcesses));

        // If FCAI, display quantum history (Not applicable here as FCAI is handled separately)
        if (algorithmName.equals("FCAI Scheduling")) {
            System.out.println("\nQuantum Allocation History:");
            for (PCB proc : processes) {
                System.out.println(proc.processName + ":");
                for (String log : proc.quantumLogs) {
                    System.out.println("  " + log);
                }
            }
            System.out.println("\nAll Quantum Updates:");
            System.out.println(quantumLog);
        }
    }

    /**
     * Helper method to get a valid integer input.
     */
    private static int getValidInteger(Scanner scanner) {
        int value = -1;
        while (true) {
            try {
                value = scanner.nextInt();
                if (value < 0) {
                    System.out.print("Please enter a non-negative integer: ");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.print("Invalid input! Please enter an integer: ");
                scanner.next(); // Clear invalid input
            }
        }
        return value;
    }
}
