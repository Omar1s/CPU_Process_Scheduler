package scheduler.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * StatisticsPanel Class displaying performance metrics.
 */
public class StatisticsPanel {
    private HBox pane;
    private Label awtLabel;
    private Label atatLabel;
    private Label scheduleNameLabel;

    public StatisticsPanel() {
        pane = new HBox(20);
        pane.setPadding(new Insets(10));
        pane.setStyle("-fx-background-color: #333333;");

        scheduleNameLabel = new Label("Schedule Name: ");
        scheduleNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        awtLabel = new Label("AWT: ");
        awtLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        atatLabel = new Label("ATAT: ");
        atatLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        pane.getChildren().addAll(scheduleNameLabel, awtLabel, atatLabel);
    }

    public HBox getPane() {
        return pane;
    }

    public void updateStatistics(String scheduleName, double awt, double atat) {
        scheduleNameLabel.setText("Schedule Name: " + scheduleName);
        awtLabel.setText(String.format("AWT: %.2f", awt));
        atatLabel.setText(String.format("ATAT: %.2f", atat));
    }
}
