package com.studentapp.frontend.view;

import com.studentapp.common.model.BudgetLimit;
import com.studentapp.common.model.ExpenseCategory;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Dialog for adding or editing budget limits
 */
public class BudgetDialog extends Dialog<BudgetLimit> {

    private ComboBox<ExpenseCategory> categoryComboBox;
    private ComboBox<String> monthComboBox;
    private ComboBox<Integer> yearComboBox;
    private TextField limitAmountField;
    private TextField alertThresholdField;

    private BudgetLimit budgetLimit;
    private List<ExpenseCategory> categories;

    public BudgetDialog(BudgetLimit budgetLimit, List<ExpenseCategory> categories) {
        this.budgetLimit = budgetLimit;
        this.categories = categories;

        setTitle(budgetLimit.getId() == null ? "Add Budget Limit" : "Edit Budget Limit");
        setHeaderText(budgetLimit.getId() == null ? "Set budget limit for category" : "Modify budget limit");

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
        limitAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(!isValidForm());
        });

        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(!isValidForm());
        });

        // Convert the result when the save button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createBudgetLimitFromForm();
            }
            return null;
        });

        // Pre-populate fields if editing
        if (budgetLimit.getId() != null) {
            populateFields();
        } else {
            // Set defaults for new budget
            LocalDate now = LocalDate.now();
            monthComboBox.setValue(now.getMonth().name().substring(0, 1) +
                                 now.getMonth().name().substring(1).toLowerCase());
            yearComboBox.setValue(now.getYear());
            alertThresholdField.setText("80.0");
        }
    }

    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Category
        categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(categories);
        categoryComboBox.setConverter(new StringConverter<ExpenseCategory>() {
            @Override
            public String toString(ExpenseCategory category) {
                return category != null ? category.getName() : "";
            }

            @Override
            public ExpenseCategory fromString(String string) {
                return categories.stream()
                    .filter(c -> c.getName().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });
        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryComboBox, 1, 0);

        // Month
        monthComboBox = new ComboBox<>();
        monthComboBox.getItems().addAll(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        );
        grid.add(new Label("Month:"), 0, 1);
        grid.add(monthComboBox, 1, 1);

        // Year
        yearComboBox = new ComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 2; i <= currentYear + 5; i++) {
            yearComboBox.getItems().add(i);
        }
        grid.add(new Label("Year:"), 0, 2);
        grid.add(yearComboBox, 1, 2);

        // Limit Amount
        limitAmountField = new TextField();
        limitAmountField.setPromptText("0.00");
        grid.add(new Label("Budget Limit ($):"), 0, 3);
        grid.add(limitAmountField, 1, 3);

        // Alert Threshold
        alertThresholdField = new TextField();
        alertThresholdField.setPromptText("80.0");
        grid.add(new Label("Alert Threshold (%):"), 0, 4);
        grid.add(alertThresholdField, 1, 4);

        return grid;
    }

    private boolean isValidForm() {
        return categoryComboBox.getValue() != null &&
               !limitAmountField.getText().trim().isEmpty() &&
               monthComboBox.getValue() != null &&
               yearComboBox.getValue() != null;
    }

    private void populateFields() {
        if (budgetLimit.getCategory() != null) {
            categoryComboBox.setValue(budgetLimit.getCategory());
        }

        if (budgetLimit.getBudgetMonth() != null) {
            String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            };
            monthComboBox.setValue(months[budgetLimit.getBudgetMonth() - 1]);
        }

        yearComboBox.setValue(budgetLimit.getBudgetYear());

        if (budgetLimit.getLimitAmount() != null) {
            limitAmountField.setText(budgetLimit.getLimitAmount().toString());
        }

        if (budgetLimit.getAlertThreshold() != null) {
            alertThresholdField.setText(budgetLimit.getAlertThreshold().toString());
        }
    }

    private BudgetLimit createBudgetLimitFromForm() {
        if (budgetLimit.getId() == null) {
            budgetLimit = new BudgetLimit();
        }

        budgetLimit.setCategory(categoryComboBox.getValue());
        budgetLimit.setBudgetMonth(monthComboBox.getSelectionModel().getSelectedIndex() + 1);
        budgetLimit.setBudgetYear(yearComboBox.getValue());

        try {
            budgetLimit.setLimitAmount(new BigDecimal(limitAmountField.getText().trim()));

            String thresholdText = alertThresholdField.getText().trim();
            if (!thresholdText.isEmpty()) {
                budgetLimit.setAlertThreshold(new BigDecimal(thresholdText));
            } else {
                budgetLimit.setAlertThreshold(BigDecimal.valueOf(80.0));
            }
        } catch (NumberFormatException e) {
            return null; // Invalid amount
        }

        return budgetLimit;
    }
}
