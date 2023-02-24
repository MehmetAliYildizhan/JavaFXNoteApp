package org.example;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.Optional;


public class NoteBox extends VBox {
    private Note note;
    private CheckBox checkBox;
    private Label headingLabel;
    private Label noteLabel;
    private Label dateLabel;

    public NoteBox(Note note) {
        this.note = note;
        this.setSpacing(10);
        this.setPrefWidth(200);
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        this.setBackground(new Background(new BackgroundFill(note.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));

        this.checkBox = new CheckBox();
        this.checkBox.setPrefWidth(20);
        this.checkBox.setPrefHeight(20);

        this.headingLabel = new Label(note.getHeading());
        this.headingLabel.setWrapText(true);
        this.headingLabel.setMaxWidth(Double.MAX_VALUE);
        this.headingLabel.setStyle("-fx-font-weight: bold;");

        this.noteLabel = new Label(note.getNote());
        this.noteLabel.setWrapText(true);
        this.noteLabel.setMaxWidth(Double.MAX_VALUE);

        LocalDateTime date = note.getDate();
        String dateString = String.format("%d-%02d-%02d %02d:%02d",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(), date.getHour(), date.getMinute());
        this.dateLabel = new Label(dateString);
        this.dateLabel.setWrapText(true);
        this.dateLabel.setMaxWidth(Double.MAX_VALUE);
        this.dateLabel.setStyle("-fx-text-fill:#2F4F4F; -fx-font-size: 11;");

        BorderPane labelBox = new BorderPane();
        labelBox.setRight(checkBox);
        labelBox.setCenter(new VBox(headingLabel, noteLabel, dateLabel));

        this.getChildren().add(labelBox);
    }

    public Note getNote() {
        return note;
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }

    public void update(Note note) {
        this.note = note;
        this.headingLabel.setText(note.getHeading());
        this.noteLabel.setText(note.getNote());
        this.setBackground(new Background(new BackgroundFill(note.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void updateNote(NoteBox noteBox) {
        Dialog<Pair<String, String>> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update Note");

        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        updateDialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the heading and note labels and text fields
        Label headingLabel = new Label("Heading:");
        Label noteLabel = new Label("Note:");
        TextField headingTextField = new TextField(noteBox.getNote().getHeading());
        TextArea noteTextArea = new TextArea(noteBox.getNote().getNote());
        VBox content = new VBox(headingLabel, headingTextField, noteLabel, noteTextArea);
        updateDialog.getDialogPane().setContent(content);

        // Enable/disable the update button depending on whether the heading and note are valid
        Node updateButton = updateDialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.setDisable(true);
        headingTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty() || noteTextArea.getText().trim().isEmpty());
        });
        noteTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty() || headingTextField.getText().trim().isEmpty());
        });

        // Request focus on the heading text field by default
        Platform.runLater(headingTextField::requestFocus);

        // Convert the result to a Pair object when the update button is clicked
        updateDialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return new Pair<>(headingTextField.getText(), noteTextArea.getText());
            }
            return null;
        });

        // Show the update dialog and wait for the user to update the note or cancel the dialog
        Optional<Pair<String, String>> result = updateDialog.showAndWait();
        result.ifPresent(headingAndNote -> {
            Note updatedNote = new Note(
                    noteBox.getNote().getId(),
                    headingAndNote.getKey(),
                    headingAndNote.getValue(),
                    noteBox.getNote().getColor(),
                    noteBox.getNote().getHashtags(),
                    noteBox.getNote().getDate()
            );
            NoteFileManager db = new NoteFileManager();
            db.update(updatedNote);
            noteBox.note = updatedNote;
        });
    }

}
