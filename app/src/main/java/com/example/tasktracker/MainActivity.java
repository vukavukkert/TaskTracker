package com.example.tasktracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button addNoteButton;
    private TextView noteTextView;
    private DBHelper dbHelper;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        addNoteButton = findViewById(R.id.addNoteButton);
        noteTextView = findViewById(R.id.noteTextView);
        dbHelper = new DBHelper(this);
        // Устанавливаем слушатель для выбора даты
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            displayNoteForDate(selectedDate);

        });

        // Устанавливаем слушатель для кнопки добавления заметки
        addNoteButton.setOnClickListener(v -> showAddNoteDialog());
    }

    private void showAddNoteDialog() {
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Note");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String note = input.getText().toString();
            if (!note.isEmpty()) {
                dbHelper.addOrUpdateTask(selectedDate, note);
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
                displayNoteForDate(selectedDate);
            } else {
                Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void displayNoteForDate(String date) {
        String note = dbHelper.getNoteByDate(date);
        if (note != null) {
            noteTextView.setText(note);
        } else {
            noteTextView.setText("No Notes");
        }
    }


}