package ua.com.masterok.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNotes;
    private FloatingActionButton buttonAddNewNote;
    private NotesAdapter notesAdapter;

    private ArrayList<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        Random random = new Random();
        // random.nextInt(3) - рандомне число від 0 до 3, не включаючи 3
        for (int i = 0; i < 20; i++) {
            Note note = new Note(i, "Note " + i, random.nextInt(3));
            notes.add(note);
        }

        adapter();
        showNotes();

        onClickButtonAddNote();

    }

    private void init() {
        rvNotes = findViewById(R.id.recycler_view_notes);
        buttonAddNewNote = findViewById(R.id.button_add_note);
    }

    private void adapter() {
        notesAdapter = new NotesAdapter();
        notesAdapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Note note) {
                Toast.makeText(
                        MainActivity.this,
                        "Clicked" + note.getId(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        rvNotes.setAdapter(notesAdapter);
        // встановлено в xml
        //rvNotes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // dragDirs - напрямок переміщення. В даному випадку не використовується, тому 0
        // swipeDirs - напрямок свайпа (вліво, чи вправо). В даному випадку в дві сторони.
        // Використовуються константи
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                ) {
                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            @NonNull RecyclerView.ViewHolder target
                    ) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction
                    ) {
                        // отримання позиції елемента по якому був виконаний свайп
                        int position = viewHolder.getAdapterPosition();
                        Note note = notesAdapter.getNotes().get(position);
                        notes.remove(note.getId());
                        // оновлює список
                        showNotes();
                    }
                });
        itemTouchHelper.attachToRecyclerView(rvNotes);
    }

    private void showNotes() {
        notesAdapter.setNotes(notes);
    }

    private void onClickButtonAddNote() {
        buttonAddNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddNoteActivity.newIntent(getApplicationContext());
                startActivity(intent);
            }
        });
    }

}