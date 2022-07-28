package ua.com.masterok.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNotes;
    private FloatingActionButton buttonAddNewNote;
    private NotesAdapter notesAdapter;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        adapter();
        onClickButtonAddNote();
    }

    private void init() {
        rvNotes = findViewById(R.id.recycler_view_notes);
        buttonAddNewNote = findViewById(R.id.button_add_note);
        // Правильне створення вьюмоделі, щоб вона переживала перевертання екрану
        // viewModel буде знищино, коли ми повністю підемо з екрану (метод finish())
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void adapter() {
        notesAdapter = new NotesAdapter();
        rvNotes.setAdapter(notesAdapter);

        // підписуємся на LiveData
        // У об'єкта LiveData можна викликати метод .observe(), тим самим підписавшись на всі зміни
        // які будуть в БД
        // В .observe() передається 2 параметри. 1 - об'єкт у якого є життєвий цикл. Передаємо
        // активіті (this)
        // 2 - передається об`єкт який реалізує інтерфейс обсервер. Цей інтерфейс реалізує 1 метод
        // який необхідно перевизначити. Створимо об'єкт анонімного класу
        viewModel.getNotes().observe(this, new Observer<List<Note>>() {
            @Override
            // В цей метод приходить колекція всіх заміток. Цей метод викликається кожного разу коли
            // в БД відбуваються зміни
            public void onChanged(List<Note> notes) {
                notesAdapter.setNotes(notes);
            }
        });

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
                        // видалення за MVVM архітектурою
                        viewModel.remove(note);
                    }
                });
        itemTouchHelper.attachToRecyclerView(rvNotes);
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