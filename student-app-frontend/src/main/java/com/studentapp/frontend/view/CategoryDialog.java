package com.studentapp.frontend.view;

import com.studentapp.common.model.ExpenseCategory;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Dialog for adding or editing expense categories
 */
public class CategoryDialog extends Dialog<ExpenseCategory> {

    private TextField nameField;
    private TextArea descriptionArea;
    private TextField colorField;
    private ComboBox<String> iconComboBox;
    private CheckBox activeCheckBox;

    private ExpenseCategory category;

    public CategoryDialog(ExpenseCategory category) {
        this.category = category;

        setTitle(category.getId() == null ? "Add Category" : "Edit Category");
        setHeaderText(category.getId() == null ? "Create new expense category" : "Modify category details");

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
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        // Convert the result when the save button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createCategoryFromForm();
            }
            return null;
        });

        // Pre-populate fields if editing
        if (category.getId() != null) {
            populateFields();
        } else {
            // Set defaults for new category
            activeCheckBox.setSelected(true);
            colorField.setText("#FF6B6B");
            iconComboBox.setValue("category");
        }
    }

    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Name
        nameField = new TextField();
        nameField.setPromptText("Category name");
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        // Description
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Category description...");
        descriptionArea.setPrefRowCount(3);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        // Color
        colorField = new TextField();
        colorField.setPromptText("#FF6B6B");
        grid.add(new Label("Color (Hex):"), 0, 2);
        grid.add(colorField, 1, 2);

        // Icon
        iconComboBox = new ComboBox<>();
        iconComboBox.getItems().addAll(
            "restaurant", "car", "movie", "book", "health",
            "shopping", "utility", "category", "money", "home"
        );
        grid.add(new Label("Icon:"), 0, 3);
        grid.add(iconComboBox, 1, 3);

        // Active
        activeCheckBox = new CheckBox();
        grid.add(new Label("Active:"), 0, 4);
        grid.add(activeCheckBox, 1, 4);

        return grid;
    }

    private void populateFields() {
        nameField.setText(category.getName());
        descriptionArea.setText(category.getDescription());
        colorField.setText(category.getColor());
        iconComboBox.setValue(category.getIcon());
        activeCheckBox.setSelected(category.getIsActive() != null ? category.getIsActive() : true);
    }

    private ExpenseCategory createCategoryFromForm() {
        if (category.getId() == null) {
            category = new ExpenseCategory();
        }

        category.setName(nameField.getText().trim());
        category.setDescription(descriptionArea.getText().trim());
        category.setColor(colorField.getText().trim());
        category.setIcon(iconComboBox.getValue());
        category.setIsActive(activeCheckBox.isSelected());

        return category;
    }
}
