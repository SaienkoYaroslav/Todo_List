package ua.com.masterok.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextNote;
    private RadioGroup radioGroupPriority;
    private RadioButton radioButtonLow, radioButtonMedium, radioButtonHigh;
    private Button buttonSave;
    private NoteDatabase noteDatabase;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        init();
        onClickButtonSave();
    }

    private void init() {
        editTextNote = findViewById(R.id.edit_text_note);
        radioGroupPriority = findViewById(R.id.radio_group_priority);
        radioButtonLow = findViewById(R.id.radio_button_low);
        radioButtonMedium = findViewById(R.id.radio_button_medium);
        radioButtonHigh = findViewById(R.id.radio_button_high);
        buttonSave = findViewById(R.id.button_save);

        noteDatabase = NoteDatabase.getInstance(getApplication());
    }

    private void onClickButtonSave() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();

            }
        });
    }

    private void saveNote() {
        String text = editTextNote.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(AddNoteActivity.this, "Enter the text", Toast.LENGTH_SHORT).show();
            return;
        }
        int priority = getPriority();
        Note note = new Note(text, priority);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                noteDatabase.notesDao().add(note);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });
        thread.start();
    }

    private int getPriority() {
        int priority;
        if (radioButtonLow.isChecked()) {
            priority = 0;
        } else if (radioButtonMedium.isChecked()) {
            priority = 1;
        } else {
            priority = 2;
        }
        return priority;
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AddNoteActivity.class);
    }


}