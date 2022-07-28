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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNotes;
    private FloatingActionButton buttonAddNewNote;
    private NotesAdapter notesAdapter;
    private NoteDatabase noteDatabase;

    // Handler - клас, який може тримати в собі посилання на головний потік
    // Потрібен для оновлення вью. Так як з БД ми маємо працювати у фоновому потоці, а оновлювати вью
    // у фоновому потоці не можна. Вью оновлюються тільки у мейн потоці
    // Looper.getMainLooper() - цей параметр дозволяє хендлеру тримати посилання на головний потік
    private Handler handler = new Handler(Looper.getMainLooper());
    // Тепер в цей об'єкт можна відправляти повідомлення, які хендлер буде обробляти
    // Повідомлення будуть типу Ранбл. Тобто нашому об'єкту handler буде відправлятись об'єкт типу
    // ранбл і хендлер буде викликати в переданого об'єкту метод ран в головному потоці.

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

        noteDatabase = NoteDatabase.getInstance(getApplication());
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

        // підписуємся на LiveData
        // У об'єкта LiveData можна викликати метод .observe(), тим самим підписавшись на всі зміни
        // які будуть в БД
        // В .observe() передається 2 параметри. 1 - об'єкт у якого є життєвий цикл. Передаємо
        // активіті (this)
        // 2 - передається об`єкт який реалізує інтерфейс обсервер. Цей інтерфейс реалізує 1 метод
        // який необхідно перевизначити. Створимо об'єкт анонімного класу
        noteDatabase.notesDao().getNotes().observe(this, new Observer<List<Note>>() {
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
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // видалення даних у фоновому потоці
                                noteDatabase.notesDao().remove(note.getId());
                            }
                        });
                        thread.start();
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