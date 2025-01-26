package scheduler.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import scheduler.models.PCB;

import java.util.List;
import java.util.Map;


public class ProcessesInfoPanel {
    private ScrollPane pane;
    private VBox mainBox;
    private Label title;
    private VBox processesBox;
    private Label quantumLogsTitle;
    private TextArea quantumLogsArea;

    public ProcessesInfoPanel() {
        mainBox = new VBox(10);
        mainBox.setPadding(new Insets(10));
        mainBox.setStyle("-fx-background-color: #333333;");

        // Title for Processes Information
        title = new Label("Processes Information");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Container for individual process info
        processesBox = new VBox(10);

        // Title for Quantum Logs
        quantumLogsTitle = new Label("Quantum Logs");
        quantumLogsTitle.setTextFill(Color.WHITE);
        quantumLogsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // TextArea to display Quantum Logs
        quantumLogsArea = new TextArea();
        quantumLogsArea.setEditable(false);
        quantumLogsArea.setWrapText(true);
        quantumLogsArea.setStyle("-fx-control-inner-background: #555555; -fx-text-fill: white;");
        quantumLogsArea.setPrefHeight(200); // Adjust height as needed

        // Add all components to mainBox
        mainBox.getChildren().addAll(title, processesBox, quantumLogsTitle, quantumLogsArea);

        // Wrap mainBox in a ScrollPane for scrolling capability
        pane = new ScrollPane(mainBox);
        pane.setFitToWidth(true);
        pane.setStyle("-fx-background-color: #333333;");
    }

    public ScrollPane getPane() {
        return pane;
    }

    public void displayProcesses(List<PCB> processes, Map<Integer, Color> processColors) {
        processesBox.getChildren().clear();

        for (PCB proc : processes) {
            VBox infoBox = new VBox(5);
            infoBox.setPadding(new Insets(5));
            infoBox.setStyle("-fx-border-color: #555555; -fx-border-radius: 5; -fx-background-color: #444444;");

            HBox header = new HBox(10);
            Rectangle colorRect = new Rectangle(20, 20, proc.color); // Use Process Color
            Label nameLabel = new Label("Name: " + proc.processName);
            nameLabel.setTextFill(Color.WHITE);
            Label pidLabel = new Label("PID: " + proc.processID);
            pidLabel.setTextFill(Color.WHITE);
            Label priorityLabel = new Label("Priority: " + proc.priorityLevel);
            priorityLabel.setTextFill(Color.WHITE);

            header.getChildren().addAll(colorRect, nameLabel, pidLabel, priorityLabel);

            Label arrivalLabel = new Label("Arrival Time: " + proc.arrivalTime);
            arrivalLabel.setTextFill(Color.WHITE);
            Label burstLabel = new Label("Burst Time: " + proc.totalBurst);
            burstLabel.setTextFill(Color.WHITE);
            Label startLabel = new Label("Start Time: " + (proc.startTime != -1 ? proc.startTime : "N/A"));
            startLabel.setTextFill(Color.WHITE);
            Label finishLabel = new Label("Finish Time: " + (proc.finishTime != -1 ? proc.finishTime : "N/A"));
            finishLabel.setTextFill(Color.WHITE);
            Label waitLabel = new Label("Wait Time: " + proc.waitTime);
            waitLabel.setTextFill(Color.WHITE);
            Label turnaroundLabel = new Label("Turnaround Time: " + proc.turnaroundTime);
            turnaroundLabel.setTextFill(Color.WHITE);

            infoBox.getChildren().addAll(header, arrivalLabel, burstLabel, startLabel, finishLabel, waitLabel, turnaroundLabel);
            processesBox.getChildren().add(infoBox);
        }
    }


    public void setQuantumLogs(String logs) {
        quantumLogsArea.setText(logs);
    }
}
