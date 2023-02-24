package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.Note;
import org.example.NoteBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class Main extends Application {
    private List<Note> notes = new ArrayList<>();
    NoteFileManager db = new NoteFileManager();
    private int nextNoteId = 1;

    @Override
    public void start(Stage primaryStage) {

        db.createNotesFile();
        notes = db.read();
        Label headingLabel = new Label("Heading:");
        Label noteLabel = new Label("Note:");
        TextField headingTextField = new TextField();
        TextArea noteTextArea = new TextArea();
        VBox noteList = new VBox();
        Button saveButton = new Button("Save");
        Label colorLabel = new Label("Color:");
        ComboBox<String> colorBox = new ComboBox<>();
        colorBox.getItems().addAll(
                "Red",
                "Blue",
                "Green",
                "Yellow"
        );
        colorBox.getSelectionModel().selectFirst();
        saveButton.setOnAction(event -> {
            Note newNote = new Note(nextNoteId, headingTextField.getText(), noteTextArea.getText(), convertColorStringToColor(colorBox.getValue()), new ArrayList<>(), LocalDateTime.now());
            notes.add(newNote);
            db.save(newNote);
            nextNoteId++;
            headingTextField.clear();
            noteTextArea.clear();
            noteList.getChildren().add(0, new NoteBox(newNote));
            if(newNote.getHeading().equals("") && newNote.getNote().equals("")){
                AlertHelper myAlert = new AlertHelper();
                myAlert.showWarning("Warning","This note will be save empty, header and text is empty.");
            }else if(newNote.getHeading().equals("")){
                AlertHelper myAlert = new AlertHelper();
                myAlert.showWarning("Warning","This note's header will be saved empty, header is empty.");
            }else if(newNote.getNote().equals("")){
                AlertHelper myAlert = new AlertHelper();
                myAlert.showWarning("Warning","This note's text will be saved empty, text is empty.");
            }
        });

        Button deleteButton = new Button("Delete");
        HBox buttonBox = new HBox(deleteButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        VBox colorLayout = new VBox(colorLabel,colorBox);
        HBox searchAndColor = new HBox(colorBox,saveButton,buttonBox);
        searchAndColor.setAlignment(Pos.CENTER);
        VBox inputBox = new VBox(headingLabel,headingTextField, noteLabel, noteTextArea, searchAndColor);


        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER);



        Label searchLabel = new Label("Search:");
        searchLabel.setAlignment(Pos.CENTER_RIGHT);
        TextField searchField = new TextField();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            noteList.getChildren().clear();
            notes.clear();
            notes = db.search(newValue);
            notes.sort(Comparator.comparing(Note::getDate).reversed());
            for (Note note : notes) {
                noteList.getChildren().add(new NoteBox(note));
            }
        });

        notes.sort(Comparator.comparing(Note::getDate).reversed());
        for (Note note : notes) {
            noteList.getChildren().add(new NoteBox(note));
        }

        ScrollPane noteBox = new ScrollPane(noteList);
        noteBox.setFitToWidth(true);
        noteBox.setPrefHeight(400);

        searchBox.getChildren().addAll(searchLabel, searchField);



        deleteButton.setOnAction(event -> {
            List<Node> toRemove = new ArrayList<>();
            for (Node node : noteList.getChildren()) {
                NoteBox noteBoxX = (NoteBox) node;
                if (noteBoxX.isChecked()) {
                    db.delete(noteBoxX.getNote());
                    toRemove.add(noteBoxX);
                }
            }
            noteList.getChildren().removeAll(toRemove);
        });

        VBox root = new VBox(inputBox,searchBox, noteBox);

        Scene scene = new Scene(root, 400, 500);

        primaryStage.setTitle("Notes Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }

    public static List<Note> search(List<Note> notes, String keyword) {
        List<Note> searchResults = new ArrayList<>();
        for (Note note : notes) {
            if (note.getHeading().toLowerCase().contains(keyword.toLowerCase()) ||
                    note.getNote().toLowerCase().contains(keyword.toLowerCase()) ||
                    note.getHashtags().contains(keyword)) {
                searchResults.add(note);
            }
        }
        return searchResults;
    }





    private Color convertColorStringToColor(String colorString) {
        switch (colorString) {
            case "Red":
                return Color.LIGHTPINK;
            case "Blue":
                return Color.LIGHTBLUE;
            case "Green":
                return Color.LIGHTGREEN;
            case "Yellow":
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }
    }
}