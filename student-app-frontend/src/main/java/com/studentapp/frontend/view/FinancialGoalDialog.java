package com.studentapp.frontend.view;

import com.studentapp.common.model.FinancialGoal;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dialog for adding or editing financial goals
 */
public class FinancialGoalDialog extends Dialog<FinancialGoal> {

    private TextField goalNameField;
    private TextArea descriptionArea;
    private TextField targetAmountField;
    private TextField currentAmountField;
    private DatePicker targetDatePicker;
    private ComboBox<String> goalTypeComboBox;
    private CheckBox activeCheckBox;

    private FinancialGoal goal;

    public FinancialGoalDialog(FinancialGoal goal) {
        this.goal = goal;

        setTitle(goal.getId() == null ? "Add Financial Goal" : "Edit Financial Goal");
        setHeaderText(goal.getId() == null ? "Create new financial goal" : "Modify goal details");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = createForm();
        getDialogPane().setContent(grid);

        // Enable/disable save button based on validation
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validation
        goalNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(!isValidForm());
        });

        targetAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(!isValidForm());
        });

        // Convert the result when the save button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createGoalFromForm();
            }
            return null;
        });

        // Pre-populate fields if editing
        if (goal.getId() != null) {
            populateFields();
        } else {
            // Set defaults for new goal
            activeCheckBox.setSelected(true);
            currentAmountField.setText("0.00");
            goalTypeComboBox.setValue("savings");
        }
    }

    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Goal Name
        goalNameField = new TextField();
        goalNameField.setPromptText("Goal name");
        grid.add(new Label("Goal Name:"), 0, 0);
        grid.add(goalNameField, 1, 0);

        // Description
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Goal description...");
        descriptionArea.setPrefRowCount(3);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        // Target Amount
        targetAmountField = new TextField();
        targetAmountField.setPromptText("0.00");
        grid.add(new Label("Target Amount ($):"), 0, 2);
        grid.add(targetAmountField, 1, 2);

        // Current Amount
        currentAmountField = new TextField();
        currentAmountField.setPromptText("0.00");
        grid.add(new Label("Current Amount ($):"), 0, 3);
        grid.add(currentAmountField, 1, 3);

        // Target Date
        targetDatePicker = new DatePicker();
        grid.add(new Label("Target Date:"), 0, 4);
        grid.add(targetDatePicker, 1, 4);

        // Goal Type
        goalTypeComboBox = new ComboBox<>();
        goalTypeComboBox.getItems().addAll(
            "savings", "debt_payoff", "purchase", "emergency_fund",
            "vacation", "education", "retirement", "other"
        );
        grid.add(new Label("Goal Type:"), 0, 5);
        grid.add(goalTypeComboBox, 1, 5);

        // Active
        activeCheckBox = new CheckBox();
        grid.add(new Label("Active:"), 0, 6);
        grid.add(activeCheckBox, 1, 6);

        return grid;
    }

    private boolean isValidForm() {
        return !goalNameField.getText().trim().isEmpty() &&
               !targetAmountField.getText().trim().isEmpty();
    }

    private void populateFields() {
        goalNameField.setText(goal.getGoalName());
        descriptionArea.setText(goal.getDescription());

        if (goal.getTargetAmount() != null) {
            targetAmountField.setText(goal.getTargetAmount().toString());
        }

        if (goal.getCurrentAmount() != null) {
            currentAmountField.setText(goal.getCurrentAmount().toString());
        }

        targetDatePicker.setValue(goal.getTargetDate());
        goalTypeComboBox.setValue(goal.getGoalType());
        activeCheckBox.setSelected(goal.getIsActive() != null ? goal.getIsActive() : true);
    }

    private FinancialGoal createGoalFromForm() {
        if (goal.getId() == null) {
            goal = new FinancialGoal();
        }

        goal.setGoalName(goalNameField.getText().trim());
        goal.setDescription(descriptionArea.getText().trim());
        goal.setTargetDate(targetDatePicker.getValue());
        goal.setGoalType(goalTypeComboBox.getValue());
        goal.setIsActive(activeCheckBox.isSelected());

        try {
            goal.setTargetAmount(new BigDecimal(targetAmountField.getText().trim()));

            String currentText = currentAmountField.getText().trim();
            if (!currentText.isEmpty()) {
                goal.setCurrentAmount(new BigDecimal(currentText));
            } else {
                goal.setCurrentAmount(BigDecimal.ZERO);
            }
        } catch (NumberFormatException e) {
            return null; // Invalid amount
        }

        return goal;
    }
}
