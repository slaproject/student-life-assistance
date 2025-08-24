package com.studentapp.frontend.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class TimerController {
    // FXML Elements
    @FXML private Circle outerRing;
    @FXML private Circle clockFace;
    @FXML private Circle innerRing;
    @FXML private StackPane clockNumbers;
    @FXML private Line hourHand;
    @FXML private Line minuteHand;
    @FXML private Line secondHand;
    @FXML private Circle centerDot;
    
    @FXML private Label digitalTimerLabel;
    @FXML private Label statusLabel;
    
    // Control Buttons
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button restartButton;
    @FXML private Button setToButton;
    
    // Quick Time Buttons
    @FXML private Button quick5Button;
    @FXML private Button quick15Button;
    @FXML private Button quick25Button;
    @FXML private Button quick45Button;
    @FXML private Button quick60Button;

    // Timer State
    private Timeline timeline;
    private Timeline clockTimeline;
    private int totalSeconds = 0;
    private int timeLeft = 0;
    private boolean isRunning = false;
    private boolean isCompleted = false;
    
    // Responsive Design
    @FXML private VBox timerContainer;
    


    @FXML
    public void initialize() {
        setupTimer();
        setupAnalogClock();
        setupQuickTimeButtons();
        setupButtonActions();
        updateDisplay();
        
        // Setup responsive design after a short delay to ensure scene is ready
        javafx.application.Platform.runLater(() -> {
            setupResponsiveDesign();
        });
    }

    private void setupTimer() {
    }

    private void setupAnalogClock() {
        createCountdownNumbers();
        updateHand(hourHand, -90, 35);
        updateHand(minuteHand, -90, 50);
        updateHand(secondHand, -90, 60);
    }

    private void createCountdownNumbers() {
        clockNumbers.getChildren().clear();
        
        for (int i = 0; i < 12; i++) {
            Line tick = new Line();
            tick.setStyle("-fx-stroke: #2c3e50; -fx-stroke-width: 2;");
            
            double angle = Math.toRadians(i * 30 - 90);
            double outerRadius = 75;
            double innerRadius = 65;
            
            double outerX = Math.cos(angle) * outerRadius;
            double outerY = Math.sin(angle) * outerRadius;
            
            double innerX = Math.cos(angle) * innerRadius;
            double innerY = Math.sin(angle) * innerRadius;
            
            tick.setStartX(innerX);
            tick.setStartY(innerY);
            tick.setEndX(outerX);
            tick.setEndY(outerY);
            
            clockNumbers.getChildren().add(tick);
        }
    }

    private void updateCountdownClock() {
        if (totalSeconds > 0) {
            int remainingHours = timeLeft / 3600;
            int remainingMinutes = (timeLeft % 3600) / 60;
            int remainingSeconds = timeLeft % 60;
            
            double hourAngle = -90 + (remainingHours * 30) + (remainingMinutes * 0.5);
            
            double minuteAngle = -90 + (remainingMinutes * 6);
            
            double secondAngle = -90 + (remainingSeconds * 6);
            
            updateHand(hourHand, hourAngle, 35);
            updateHand(minuteHand, minuteAngle, 50);
            updateHand(secondHand, secondAngle, 60);
        }
    }

    private void updateHand(Line hand, double angle, double length) {
        double radians = Math.toRadians(angle);
        double endX = Math.cos(radians) * length;
        double endY = Math.sin(radians) * length;
        
        hand.setEndX(endX);
        hand.setEndY(endY);
    }

    private void setupQuickTimeButtons() {
        quick5Button.setOnAction(e -> setQuickTime(5 * 60));
        quick15Button.setOnAction(e -> setQuickTime(15 * 60));
        quick25Button.setOnAction(e -> setQuickTime(25 * 60));
        quick45Button.setOnAction(e -> setQuickTime(45 * 60));
        quick60Button.setOnAction(e -> setQuickTime(60 * 60));
    }

    private void setupButtonActions() {
        startButton.setOnAction(this::handleStart);
        pauseButton.setOnAction(this::handlePause);
        restartButton.setOnAction(this::handleRestart);
        setToButton.setOnAction(this::handleSetTo);
    }
    
    private void setupResponsiveDesign() {
        try {
            // Check if scene and window are available
            if (timerContainer.getScene() == null) {
                // If scene is not ready, try again later
                javafx.application.Platform.runLater(() -> setupResponsiveDesign());
                return;
            }
            
            Stage stage = (Stage) timerContainer.getScene().getWindow();
            if (stage == null) {
                // If stage is not ready, try again later
                javafx.application.Platform.runLater(() -> setupResponsiveDesign());
                return;
            }
            
            // Apply initial responsive styles
            updateResponsiveStyles(stage.getWidth(), stage.getHeight());
            
            // Listen for window resize events
            stage.widthProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && newValue.doubleValue() > 0) {
                    updateResponsiveStyles(newValue.doubleValue(), stage.getHeight());
                }
            });
            
            stage.heightProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && newValue.doubleValue() > 0) {
                    updateResponsiveStyles(stage.getWidth(), newValue.doubleValue());
                }
            });
            
        } catch (Exception e) {
            // If responsive design fails, just continue without it
            System.err.println("Responsive design setup failed: " + e.getMessage());
        }
    }
    
    private void updateResponsiveStyles(double width, double height) {
        try {
            System.out.println("Window size: " + width + "x" + height);
            
            // Remove all responsive style classes
            timerContainer.getStyleClass().removeAll("tiny-window", "small-window", "large-window");
            
            String styleClass = null;
            // Apply appropriate style class based on window size
            if (width < 600 || height < 500) {
                styleClass = "tiny-window";
            } else if (width < 800 || height < 600) {
                styleClass = "small-window";
            } else if (width > 1200 || height > 800) {
                styleClass = "large-window";
            }
            
            if (styleClass != null) {
                timerContainer.getStyleClass().add(styleClass);
                System.out.println("Applied style: " + styleClass);
            } else {
                System.out.println("No responsive style applied");
            }
            
            System.out.println("Current styles: " + timerContainer.getStyleClass());
            
        } catch (Exception e) {
            System.err.println("Error updating responsive styles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setQuickTime(int seconds) {
        if (isRunning) {
            handlePause(null);
        }
        totalSeconds = seconds;
        timeLeft = seconds;
        isCompleted = false;
        updateDisplay();
        updateCountdownClock();
    }

    private void handleStart(ActionEvent event) {
        if (!isRunning && timeLeft > 0) {
            isRunning = true;
            isCompleted = false;
            
            if (timeline == null) {
                timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
                timeline.setCycleCount(Timeline.INDEFINITE);
            }
            
            timeline.play();
            updateStatus("Running");
            updateButtonStates();
        }
    }

    private void handlePause(ActionEvent event) {
        if (isRunning && timeline != null) {
            timeline.pause();
            isRunning = false;
            updateStatus("Paused");
            updateButtonStates();
        }
    }

    private void handleRestart(ActionEvent event) {
        if (timeline != null) {
            timeline.stop();
        }
        
        timeLeft = totalSeconds;
        isRunning = false;
        isCompleted = false;
        
        updateDisplay();
        updateCountdownClock();
        updateStatus("Ready");
        updateButtonStates();
    }

    private void handleSetTo(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("00:05:00");
        dialog.setTitle("Set Timer");
        dialog.setHeaderText("Set timer duration");
        dialog.setContentText("Enter time (hh:mm:ss or mm:ss):");
        
        dialog.showAndWait().ifPresent(input -> {
            int seconds = parseTime(input);
            if (seconds > 0) {
                if (isRunning) {
                    handlePause(null);
                }
                totalSeconds = seconds;
                timeLeft = seconds;
                isCompleted = false;
                updateDisplay();
                updateCountdownClock();
                updateStatus("Ready");
            }
        });
    }

    private int parseTime(String input) {
        try {
            String[] parts = input.trim().split(":");
            int h = 0, m = 0, s = 0;
            
            if (parts.length == 3) {
                h = Integer.parseInt(parts[0]);
                m = Integer.parseInt(parts[1]);
                s = Integer.parseInt(parts[2]);
            } else if (parts.length == 2) {
                m = Integer.parseInt(parts[0]);
                s = Integer.parseInt(parts[1]);
            } else if (parts.length == 1) {
                s = Integer.parseInt(parts[0]);
            }
            
            return h * 3600 + m * 60 + s;
        } catch (Exception e) {
            return 0;
        }
    }

    private void tick() {
        if (timeLeft > 0) {
            timeLeft--;
            updateDisplay();
            updateCountdownClock();
        } else {
            if (timeline != null) {
                timeline.stop();
            }
            isRunning = false;
            isCompleted = true;
            updateStatus("Completed!");
            updateButtonStates();
            playCompletionSound();
        }
    }

    private void updateDisplay() {  
        int h = timeLeft / 3600;
        int m = (timeLeft % 3600) / 60;
        int s = timeLeft % 60;
        digitalTimerLabel.setText(String.format("%02d:%02d:%02d", h, m, s));
        
        updateVisualStates();
    }

    private void updateStatus(String status) {
        statusLabel.setText(status);
    }

    private void updateButtonStates() {
        startButton.setDisable(isRunning || timeLeft == 0);
        pauseButton.setDisable(!isRunning);
        restartButton.setDisable(totalSeconds == 0);
    }

    private void updateVisualStates() {
        digitalTimerLabel.getStyleClass().removeAll("running", "paused", "completed");
        statusLabel.getStyleClass().removeAll("running", "paused", "completed");
        
        if (isCompleted) {
            digitalTimerLabel.getStyleClass().add("completed");
            statusLabel.getStyleClass().add("completed");
        } else if (isRunning) {
            digitalTimerLabel.getStyleClass().add("running");
            statusLabel.getStyleClass().add("running");
        } else if (timeLeft < totalSeconds && timeLeft > 0) {
            digitalTimerLabel.getStyleClass().add("paused");
            statusLabel.getStyleClass().add("paused");
        }
    }

    private void playCompletionSound() {
        try {
            java.awt.Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
            System.out.println("\u0007");
        }
    }
} 