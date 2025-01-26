package scheduler.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import scheduler.models.PCB;
import scheduler.models.FCAProcess;
import scheduler.schedulers.SchedulerManager;
import scheduler.schedulers.FCAScheduler;
import scheduler.models.ExecutionSegment;

import java.util.*;
import java.util.List;
import java.util.Map;

/**
 * ControlsPanel Class handling user interactions and scheduling execution.
 * Modified to display Quantum Logs in the GUI.
 */
public class ControlsPanel {
    private VBox pane;
    private CanvasPane canvasPane;
    private ProcessesInfoPanel infoPanel;
    private StatisticsPanel statsPanel;

    private TextField processNameField;
    private TextField arrivalTimeField;
    private TextField burstTimeField;
    private TextField priorityField;
    private TextField quantumField;
    private ComboBox<String> colorComboBox; // Added Color ComboBox
    private Button addButton;
    private Button scheduleButton;

    private List<PCB> processList;
    private ArrayList<FCAProcess> fcaiProcessList;

    private ComboBox<String> algorithmComboBox;

    public ControlsPanel(CanvasPane canvasPane, ProcessesInfoPanel infoPanel, StatisticsPanel statsPanel) {
        this.canvasPane = canvasPane;
        this.infoPanel = infoPanel;
        this.statsPanel = statsPanel;
        this.processList = new ArrayList<>();
        this.fcaiProcessList = new ArrayList<>();

        pane = new VBox(10);
        pane.setPadding(new Insets(10));
        pane.setStyle("-fx-background-color: #444444;");

        // Process Input Fields
        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(5));

        processNameField = new TextField();
        processNameField.setPromptText("Process Name");

        arrivalTimeField = new TextField();
        arrivalTimeField.setPromptText("Arrival Time");

        burstTimeField = new TextField();
        burstTimeField.setPromptText("Burst Time");

        priorityField = new TextField();
        priorityField.setPromptText("Priority");

        quantumField = new TextField();
        quantumField.setPromptText("Quantum");

        // Initialize Color ComboBox with Popular Colors
        colorComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Red", "Green", "Blue", "Yellow", "Orange",
                "Purple", "Cyan", "Magenta", "Brown", "Black",
                "Pink", "Gray", "Light Blue", "Lime", "Navy"
        ));
        colorComboBox.setPromptText("Select Color");
        colorComboBox.setValue("Blue"); // Default Color

        addButton = new Button("Add Process");
        addButton.setOnAction(e -> addProcess());

        inputBox.getChildren().addAll(
                processNameField,
                arrivalTimeField,
                burstTimeField,
                priorityField,
                quantumField,
                colorComboBox,
                addButton
        );

        // Scheduling Algorithm Selection
        HBox algoBox = new HBox(10);
        algoBox.setPadding(new Insets(5));

        algorithmComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Non-Preemptive Priority Scheduling",
                "Non-Preemptive Shortest Job First (SJF)",
                "Shortest-Remaining Time First (SRTF)",
                "FCAI Scheduling"
        ));
        algorithmComboBox.setValue("Non-Preemptive Priority Scheduling");

        // Context Switching Time
        TextField csTimeField = new TextField();
        csTimeField.setPromptText("Context Switch Time");

        scheduleButton = new Button("Run Scheduling");
        scheduleButton.setOnAction(e -> runScheduling(csTimeField.getText()));

        algoBox.getChildren().addAll(new Label("Algorithm:"), algorithmComboBox, csTimeField, scheduleButton);

        pane.getChildren().addAll(inputBox, algoBox);
    }

    public VBox getPane() {
        return pane;
    }

    private void addProcess() {
        String name = processNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Invalid Input", "Process Name cannot be empty.");
            return;
        }

        int arrival, burst, priority, quantum;
        try {
            arrival = Integer.parseInt(arrivalTimeField.getText().trim());
            burst = Integer.parseInt(burstTimeField.getText().trim());
            priority = Integer.parseInt(priorityField.getText().trim());
            quantum = Integer.parseInt(quantumField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numerical values for Arrival Time, Burst Time, Priority, and Quantum.");
            return;
        }

        String colorName = colorComboBox.getValue();
        Color color = getColorFromName(colorName);

        PCB pcb = new PCB(name, arrival, burst, priority, quantum, processList.size(), color);
        processList.add(pcb);

        FCAProcess fcaiProc = new FCAProcess(name, arrival, burst, priority, quantum, fcaiProcessList.size(), color);
        fcaiProcessList.add(fcaiProc);

        showAlert("Process Added", "Process " + name + " has been added successfully.");

        // Clear input fields
        processNameField.clear();
        arrivalTimeField.clear();
        burstTimeField.clear();
        priorityField.clear();
        quantumField.clear();
        colorComboBox.setValue("Blue"); // Reset to Default Color
    }

    private void runScheduling(String csTimeStr) {
        if (processList.isEmpty()) {
            showAlert("No Processes", "Please add at least one process before scheduling.");
            return;
        }

        int csTime;
        try {
            csTime = Integer.parseInt(csTimeStr.trim());
            if (csTime < 0) {
                showAlert("Invalid Input", "Context Switch Time cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid numerical value for Context Switch Time.");
            return;
        }

        String selectedAlgorithm = algorithmComboBox.getValue();
        List<ExecutionSegment> timeline = new ArrayList<>();
        double avgWait = 0;
        double avgTurnaround = 0;

        // Clone process lists to avoid modifying originals
        List<PCB> clonedProcessList = clonePCBList(processList);
        ArrayList<FCAProcess> clonedFcaiProcessList = cloneFcaiProcessList(fcaiProcessList);

        // Run scheduling based on selected algorithm
        timeline = SchedulerManager.runScheduling(selectedAlgorithm, csTime, clonedProcessList, clonedFcaiProcessList);

        // Calculate statistics and map results if FCAI Scheduling is selected
        if (selectedAlgorithm.equals("FCAI Scheduling")) {
            double totalWait = 0;
            double totalTurnaround = 0;
            for (FCAProcess proc : clonedFcaiProcessList) {
                int turnaround = proc.getCompletionTime() - proc.getArrivalTime();
                int wait = turnaround - proc.getOriginalBurst();
                totalWait += wait;
                totalTurnaround += turnaround;

                // Map results back to PCB
                for (PCB pcb : clonedProcessList) {
                    if (pcb.processID == proc.pid) {
                        pcb.startTime = proc.startTime;
                        pcb.finishTime = proc.getCompletionTime();
                        pcb.turnaroundTime = pcb.finishTime - pcb.arrivalTime;
                        pcb.waitTime = pcb.turnaroundTime - pcb.totalBurst;
                        break;
                    }
                }
            }
            avgWait = totalWait / clonedFcaiProcessList.size();
            avgTurnaround = totalTurnaround / clonedFcaiProcessList.size();
        } else {
            // Calculate AWT and ATAT for other algorithms
            double totalWait = 0;
            double totalTurnaround = 0;
            for (PCB proc : clonedProcessList) {
                totalWait += proc.waitTime;
                totalTurnaround += proc.turnaroundTime;
            }
            avgWait = totalWait / clonedProcessList.size();
            avgTurnaround = totalTurnaround / clonedProcessList.size();
        }

        // Update GUI Components
        canvasPane.drawSchedule(timeline);
        infoPanel.displayProcesses(clonedProcessList, generateProcessColorMap(clonedProcessList));
        statsPanel.updateStatistics(selectedAlgorithm, avgWait, avgTurnaround);

        // Update Quantum Logs if FCAI Scheduling is selected
        if (selectedAlgorithm.equals("FCAI Scheduling")) {
            String logs = FCAScheduler.quantumLogs.toString();
            infoPanel.setQuantumLogs(logs);
        } else {
            infoPanel.setQuantumLogs(""); // Clear logs for other algorithms
        }
    }

    private List<PCB> clonePCBList(List<PCB> original) {
        List<PCB> cloned = new ArrayList<>();
        for (PCB proc : original) {
            cloned.add(proc.clonePCB());
        }
        return cloned;
    }

    private ArrayList<FCAProcess> cloneFcaiProcessList(ArrayList<FCAProcess> original) {
        ArrayList<FCAProcess> cloned = new ArrayList<>();
        for (FCAProcess proc : original) {
            cloned.add(proc.duplicate());
        }
        return cloned;
    }

    private Color getColorFromName(String colorName) {
        switch (colorName.toLowerCase()) {
            case "red":
                return Color.RED;
            case "green":
                return Color.GREEN;
            case "blue":
                return Color.BLUE;
            case "yellow":
                return Color.YELLOW;
            case "orange":
                return Color.ORANGE;
            case "purple":
                return Color.PURPLE;
            case "cyan":
                return Color.CYAN;
            case "magenta":
                return Color.MAGENTA;
            case "brown":
                return Color.BROWN;
            case "black":
                return Color.BLACK;
            case "pink":
                return Color.PINK;
            case "gray":
                return Color.GRAY;
            case "light blue":
                return Color.LIGHTBLUE;
            case "lime":
                return Color.LIME;
            case "navy":
                return Color.NAVY;
            default:
                return Color.BLUE; // Default color
        }
    }

    private Map<Integer, Color> generateProcessColorMap(List<PCB> processes) {
        Map<Integer, Color> colorMap = new HashMap<>();
        for (PCB proc : processes) {
            colorMap.put(proc.processID, proc.color);
        }
        return colorMap;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
