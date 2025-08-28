package com.studentapp.frontend.view;

import com.studentapp.common.model.Expense;
import com.studentapp.common.model.ExpenseCategory;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Dialog for adding or editing expenses
 */
public class ExpenseDialog extends Dialog<Expense> {

    private TextField titleField;
    private TextArea descriptionArea;
    private TextField amountField;
    private DatePicker datePicker;
    private ComboBox<ExpenseCategory> categoryComboBox;
    private ComboBox<String> paymentMethodComboBox;
    private TextField locationField;
    private TextField tagsField;
    private CheckBox recurringCheckBox;
    private ComboBox<String> frequencyComboBox;

    private Expense expense;
    private List<ExpenseCategory> categories;

    public ExpenseDialog(Expense expense, List<ExpenseCategory> categories) {
        this.expense = expense;
        this.categories = categories;

        setTitle(expense.getId() == null ? "Add Expense" : "Edit Expense");
        setHeaderText(expense.getId() == null ? "Enter expense details" : "Modify expense details");

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
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || amountField.getText().trim().isEmpty());
        });

        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(titleField.getText().trim().isEmpty() || newValue.trim().isEmpty());
        });

        // Convert the result when the save button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createExpenseFromForm();
            }
            return null;
        });

        // Pre-populate fields if editing
        if (expense.getId() != null) {
            populateFields();
        } else {
            // Set defaults for new expense
            datePicker.setValue(LocalDate.now());
            paymentMethodComboBox.setValue("card");
        }
    }

    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Title
        titleField = new TextField();
        titleField.setPromptText("Expense title");
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);

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
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryComboBox, 1, 1);

        // Amount
        amountField = new TextField();
        amountField.setPromptText("0.00");
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);

        // Date
        datePicker = new DatePicker();
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);

        // Payment Method
        paymentMethodComboBox = new ComboBox<>();
        paymentMethodComboBox.getItems().addAll("cash", "card", "digital_wallet", "bank_transfer", "other");
        grid.add(new Label("Payment Method:"), 0, 4);
        grid.add(paymentMethodComboBox, 1, 4);

        // Location
        locationField = new TextField();
        locationField.setPromptText("Where was this expense?");
        grid.add(new Label("Location:"), 0, 5);
        grid.add(locationField, 1, 5);

        // Description
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Additional details...");
        descriptionArea.setPrefRowCount(3);
        grid.add(new Label("Description:"), 0, 6);
        grid.add(descriptionArea, 1, 6);

        // Tags
        tagsField = new TextField();
        tagsField.setPromptText("Comma-separated tags");
        grid.add(new Label("Tags:"), 0, 7);
        grid.add(tagsField, 1, 7);

        // Recurring
        recurringCheckBox = new CheckBox();
        frequencyComboBox = new ComboBox<>();
        frequencyComboBox.getItems().addAll("weekly", "monthly", "yearly");
        frequencyComboBox.setDisable(true);

        recurringCheckBox.setOnAction(e -> {
            frequencyComboBox.setDisable(!recurringCheckBox.isSelected());
            if (!recurringCheckBox.isSelected()) {
                frequencyComboBox.setValue(null);
            }
        });

        HBox recurringBox = new HBox(10);
        recurringBox.getChildren().addAll(recurringCheckBox, new Label("Frequency:"), frequencyComboBox);

        grid.add(new Label("Recurring:"), 0, 8);
        grid.add(recurringBox, 1, 8);

        return grid;
    }

    private void populateFields() {
        titleField.setText(expense.getTitle());
        descriptionArea.setText(expense.getDescription());
        if (expense.getAmount() != null) {
            amountField.setText(expense.getAmount().toString());
        }
        datePicker.setValue(expense.getExpenseDate());

        // Find and set category
        if (expense.getCategory() != null) {
            categoryComboBox.setValue(expense.getCategory());
        }

        paymentMethodComboBox.setValue(expense.getPaymentMethod());
        locationField.setText(expense.getLocation());
        tagsField.setText(expense.getTags());

        if (expense.getIsRecurring() != null && expense.getIsRecurring()) {
            recurringCheckBox.setSelected(true);
            frequencyComboBox.setDisable(false);
            frequencyComboBox.setValue(expense.getRecurringFrequency());
        }
    }

    private Expense createExpenseFromForm() {
        if (expense.getId() == null) {
            expense = new Expense();
        }

        expense.setTitle(titleField.getText().trim());
        expense.setDescription(descriptionArea.getText().trim());

        try {
            expense.setAmount(new BigDecimal(amountField.getText().trim()));
        } catch (NumberFormatException e) {
            return null; // Invalid amount
        }

        expense.setExpenseDate(datePicker.getValue());
        expense.setCategory(categoryComboBox.getValue());
        expense.setPaymentMethod(paymentMethodComboBox.getValue());
        expense.setLocation(locationField.getText().trim());
        expense.setTags(tagsField.getText().trim());
        expense.setIsRecurring(recurringCheckBox.isSelected());
        expense.setRecurringFrequency(recurringCheckBox.isSelected() ? frequencyComboBox.getValue() : null);

        return expense;
    }
}
