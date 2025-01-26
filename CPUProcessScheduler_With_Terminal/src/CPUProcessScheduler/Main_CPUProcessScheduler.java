package CPUProcessScheduler;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main_CPUProcessScheduler {
    public static void main(String[] args) {
        Scanner userScanner = new Scanner(System.in);
        List<PCB> processList = new ArrayList<>();
        ArrayList<FCAProcess> fcaiProcessList = new ArrayList<>();
        int totalProcesses = 0;

        try {
            System.out.print("Enter the number of processes: ");
            totalProcesses = userScanner.nextInt();
            userScanner.nextLine(); // Consume the newline

            for (int i = 0; i < totalProcesses; i++) {
                System.out.println("\nEnter details for Process " + (i + 1) + ":");
                System.out.print("Process Name: ");
                String name = userScanner.nextLine();

                System.out.print("Arrival Time: ");
                int arrival = getValidInteger(userScanner);

                System.out.print("Burst Time: ");
                int burst = getValidInteger(userScanner);

                System.out.print("Priority Level: ");
                int priority = getValidInteger(userScanner);

                System.out.print("Quantum: ");
                int quantum = getValidInteger(userScanner);
                userScanner.nextLine(); // Consume the newline

                processList.add(new PCB(name, arrival, burst, priority, quantum, i));
                fcaiProcessList.add(new FCAProcess(name, arrival, burst, priority, quantum, i));
            }

            SchedulerManager.initiateScheduling(processList, fcaiProcessList);

        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please ensure numerical values are entered where required.");
        }

        userScanner.close();
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
