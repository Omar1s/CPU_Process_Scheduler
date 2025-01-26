package scheduler;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduler.schedulers.SchedulerManager;
import scheduler.ui.CanvasPane;
import scheduler.ui.ControlsPanel;
import scheduler.ui.ProcessesInfoPanel;
import scheduler.ui.StatisticsPanel;
import scheduler.models.PCB;
import scheduler.models.FCAProcess;
import scheduler.schedulers.FCAScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CPUProcessSchedulerGUI: A JavaFX-based GUI application for CPU scheduling simulations.
 */
public class CPUProcessSchedulerGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CPU Process Scheduler");

        // Initialize main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #2b2b2b;"); // Dark background

        // Initialize GUI components
        CanvasPane canvasPane = new CanvasPane();
        ProcessesInfoPanel infoPanel = new ProcessesInfoPanel();
        StatisticsPanel statsPanel = new StatisticsPanel();
        ControlsPanel controlsPanel = new ControlsPanel(canvasPane, infoPanel, statsPanel);

        // Set layout regions
        mainLayout.setCenter(canvasPane.getPane());
        mainLayout.setRight(infoPanel.getPane()); // Updated to include ScrollPane
        mainLayout.setBottom(statsPanel.getPane());
        mainLayout.setTop(controlsPanel.getPane());

        BorderPane.setMargin(infoPanel.getPane(), new Insets(10));
        BorderPane.setMargin(statsPanel.getPane(), new Insets(10));


        Scene scene = new Scene(mainLayout, 1400, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method to launch the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
