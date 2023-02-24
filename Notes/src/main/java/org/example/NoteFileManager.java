package org.example;

import javafx.scene.paint.Color;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NoteFileManager {
    private static final String FILENAME = "Notes.txt";

    public void save(Note note) {
        try (FileWriter fw = new FileWriter(FILENAME, true)) {
            int id = getLastNoteId() + 1;
            LocalDateTime date = LocalDateTime.now();
            String formattedDate = date.format(DateTimeFormatter.ISO_DATE_TIME);
            String line = String.format("%d;%s;%s;%s;%s;", id, note.getHeading(), note.getNote(), note.getColor(), formattedDate);
            if(id != 1){
                line = "\r\n" + line;
            }
            fw.write(line);
        } catch (IOException e) {
            System.out.println("Error saving note: " + e.getMessage());
        }
    }

    public List<Note> read() {
        List<Note> notes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                int id = Integer.parseInt(parts[0]);
                String heading = parts[1];
                String noteText = parts[2];
                Color color = Color.valueOf(parts[3]);
                LocalDateTime date = LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_DATE_TIME);
                Note note = new Note(id, heading, noteText, color, new ArrayList<>(), date);
                notes.add(note);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Notes file not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading notes: " + e.getMessage());
        }
        return notes;
    }

    public void update(Note note) {
        List<Note> notes = read();
        int index = -1;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == note.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            Note updatedNote = notes.get(index);
            updatedNote.setHeading(note.getHeading());
            updatedNote.setColor(note.getColor());
            notes.set(index, updatedNote);
            try (FileWriter fw = new FileWriter(FILENAME, false)) {
                for (Note n : notes) {
                    LocalDateTime date = n.getDate();
                    String formattedDate = date.format(DateTimeFormatter.ISO_DATE_TIME);
                    String line = String.format("%d;%s;%s;%s;%s;%s", n.getId(), n.getHeading(), n.getNote(),
                            n.getColor().toString(), formattedDate, String.join(",", n.getHashtags()));
                    if (n.getId() != 1) {
                        line = "\r\n" + line;
                    }
                    fw.write(line);
                }
            } catch (IOException e) {
                System.out.println("Error updating note: " + e.getMessage());
            }
        } else {
            System.out.println("Note with ID " + note.getId() + " not found.");
        }
    }


    public void delete(Note note) {
        List<Note> notes = read();
        notes.removeIf(n -> n.getId() == note.getId());
        try (FileWriter fw = new FileWriter(FILENAME, false)) {
            for (Note n : notes) {
                LocalDateTime date = n.getDate();
                String formattedDate = date.format(DateTimeFormatter.ISO_DATE_TIME);
                String line = String.format("%d;%s;%s;%s;%s;%s", n.getId(), n.getHeading(), n.getNote(),
                        n.getColor().toString(), formattedDate, String.join(",", n.getHashtags()));
                if (n.getId() != 1) {
                    line = "\r\n" + line;
                }
                fw.write(line);
            }
        } catch (IOException e) {
            System.out.println("Error deleting note: " + e.getMessage());
        }
    }


    public static List<Note> search(String keyword) {
        List<Note> notes = new ArrayList<>();
        List<Note> searchResults = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                int id = Integer.parseInt(parts[0]);
                String heading = parts[1];
                String noteText = parts[2];
                Color color = Color.valueOf(parts[3]);
                LocalDateTime date = LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_DATE_TIME);
                Note note = new Note(id, heading, noteText, color, new ArrayList<>(), date);
                notes.add(note);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Notes file not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading notes: " + e.getMessage());
        }
        if(keyword!= "") {
            for (Note note : notes) {
                if (note.getHeading().toLowerCase().contains(keyword.toLowerCase()) ||
                        note.getNote().toLowerCase().contains(keyword.toLowerCase()) ||
                        note.getHashtags().contains(keyword)) {
                    searchResults.add(note);
                }
            }
        }else{
            return notes;
        }
        return searchResults;
    }

    private int getLastNoteId() {
        int lastId = 0;
        List<Note> notes = read();
        if (!notes.isEmpty()) {
            Note lastNote = notes.get(notes.size() - 1);
            lastId = lastNote.getId();
        }
        return lastId;
    }

    public void createNotesFile() {
        try {
            File file = new File(FILENAME);
            if (file.createNewFile()) {
                System.out.println("Notes file created: " + file.getAbsolutePath());
            } else {
                System.out.println("Notes file already exists: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Error creating notes file: " + e.getMessage());
        }
    }

    private static Color convertColorStringToColor(String colorString) {
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
