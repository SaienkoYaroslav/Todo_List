package ua.com.masterok.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

// MVVM - Model-View-ViewModel

// View - відповідає за відображення даних.
// Model - відповідає за дані
// ViewModel - посередник між ними
// View звертається до ViewModel, а та до Model. Цей процес односторонній. Model не бачить ViewModel,
// а ViewModel не бачить View


// ViewModel Прийнято називати по View. У нас View це клас MainActivity

public class MainViewModel extends AndroidViewModel {

    // ViewModel в архітектурі MVVM може взаємодіяти з Model, тому можна додати посилання на БД
    private NoteDatabase noteDatabase;

    public MainViewModel(@NonNull Application application) {
        super(application);
        // значення БД призначаємо в конструкторі
        noteDatabase = NoteDatabase.getInstance(application);
    }

    // Реалізація видалення замітки
    public void remove(Note note) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                noteDatabase.notesDao().remove(note.getId());
            }
        });
        thread.start();
    }

    public LiveData<List<Note>> getNotes() {
        return noteDatabase.notesDao().getNotes();
    }

}
