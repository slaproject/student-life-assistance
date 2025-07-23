package com.studentapp.frontend.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.util.Duration;

public class TimerController {
    @FXML private Canvas analogClockCanvas;
    @FXML private Label digitalTimerLabel;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button restartButton;
    @FXML private Button setToButton;

    private Timeline timeline;
    private int totalSeconds = 0;
    private int timeLeft = 0;
    private boolean isRunning = false;

    @FXML
    public void initialize() {
        updateDigitalTimer();
        drawAnalogClock();
        setupButtonActions();
    }

    private void setupButtonActions() {
        startButton.setOnAction(this::handleStart);
        pauseButton.setOnAction(this::handlePause);
        restartButton.setOnAction(this::handleRestart);
        setToButton.setOnAction(this::handleSetTo);
    }

    private void handleStart(ActionEvent event) {
        if (!isRunning && timeLeft > 0) {
            isRunning = true;
            if (timeline == null) {
                timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
                timeline.setCycleCount(Timeline.INDEFINITE);
            }
            timeline.play();
        }
    }

    private void handlePause(ActionEvent event) {
        if (isRunning && timeline != null) {
            timeline.pause();
            isRunning = false;
        }
    }

    private void handleRestart(ActionEvent event) {
        timeLeft = totalSeconds;
        updateDigitalTimer();
        drawAnalogClock();
        if (isRunning && timeline != null) {
            timeline.playFromStart();
        }
    }

    private void handleSetTo(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("00:05:00");
        dialog.setTitle("Set Timer");
        dialog.setHeaderText("Set timer (hh:mm:ss)");
        dialog.setContentText("Time:");
        dialog.showAndWait().ifPresent(input -> {
            int seconds = parseTime(input);
            if (seconds > 0) {
                totalSeconds = seconds;
                timeLeft = seconds;
                updateDigitalTimer();
                drawAnalogClock();
            }
        });
    }

    private int parseTime(String input) {
        try {
            String[] parts = input.split(":");
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
            updateDigitalTimer();
            drawAnalogClock();
        } else {
            if (timeline != null) timeline.stop();
            isRunning = false;
        }
    }

    private void updateDigitalTimer() {
        int h = timeLeft / 3600;
        int m = (timeLeft % 3600) / 60;
        int s = timeLeft % 60;
        digitalTimerLabel.setText(String.format("%02d:%02d:%02d", h, m, s));
    }

    private void drawAnalogClock() {
        GraphicsContext gc = analogClockCanvas.getGraphicsContext2D();
        double w = analogClockCanvas.getWidth();
        double h = analogClockCanvas.getHeight();
        gc.clearRect(0, 0, w, h);
        // Draw clock face
        gc.strokeOval(10, 10, w - 20, h - 20);
        // Draw hand (timer progress)
        if (totalSeconds > 0) {
            double angle = 360.0 * (1.0 - (double)timeLeft / totalSeconds) - 90;
            double centerX = w / 2;
            double centerY = h / 2;
            double radius = (w - 40) / 2;
            double rad = Math.toRadians(angle);
            double x = centerX + radius * Math.cos(rad);
            double y = centerY + radius * Math.sin(rad);
            gc.strokeLine(centerX, centerY, x, y);
        }
    }
} 