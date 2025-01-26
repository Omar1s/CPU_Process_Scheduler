package scheduler.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import scheduler.models.ExecutionSegment;

import java.util.List;
import java.util.Map;

/**
 * CanvasPane Class for Scheduling Graph Visualization.
 */
public class CanvasPane {
    private Pane pane;
    private Canvas canvas;
    private GraphicsContext gc;

    public CanvasPane() {
        pane = new Pane();
        canvas = new Canvas(1000, 600);
        gc = canvas.getGraphicsContext2D();
        pane.getChildren().add(canvas);
        pane.setStyle("-fx-background-color: #1e1e1e;");
    }

    public Pane getPane() {
        return pane;
    }

    public void drawSchedule(List<ExecutionSegment> timeline) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Determine the scale based on time
        int maxTime = timeline.stream().mapToInt(seg -> seg.endTime).max().orElse(100);
        double scaleX = (maxTime > 0) ? (canvas.getWidth() - 100) / (double) maxTime : 1;
        double rowHeight = 50;

        // Draw X-axis time markers
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        for (int t = 0; t <= maxTime; t++) {
            double x = t * scaleX+50; // Adding margin
            gc.strokeLine(x, 30, x, canvas.getHeight() - 20);
           if (t % 5 == 0 || t == maxTime) { // Label every 5 units and the last time
                gc.setFill(Color.WHITE);
                gc.fillText(String.valueOf(t), x - 5, 20);
            }
        }

        // Draw timeline
        Map<Integer, Integer> processRowMap = new java.util.HashMap<>();
        int currentRow = 0;

        for (ExecutionSegment seg : timeline) {
            // Assign a row to each process if not already assigned
            if (!processRowMap.containsKey(seg.pid)) {
                processRowMap.put(seg.pid, currentRow++);
            }
            int row = processRowMap.get(seg.pid);

            double x = seg.startTime * scaleX + 50; // Adding margin
            double width = (seg.endTime - seg.startTime) * scaleX;
            double y = row * rowHeight + 50;

            // Draw process block
            gc.setFill(seg.color);
            gc.fillRect(x, y, width, rowHeight - 10);

            // Draw process name
            gc.setFill(Color.WHITE);
            gc.fillText(seg.processName, x + 5, y + 20);
        }

        // Draw process labels on the Y-axis
        gc.setFill(Color.WHITE);
        for (Map.Entry<Integer, Integer> entry : processRowMap.entrySet()) {
            int pid = entry.getKey();
            pid++;
            int row = entry.getValue();
            gc.fillText("P" + pid, 10, row * rowHeight + 50 + (rowHeight / 2));
        }
    }
}
