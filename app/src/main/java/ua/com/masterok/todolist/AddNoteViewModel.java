package ua.com.masterok.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AddNoteViewModel extends AndroidViewModel {

    // Замість   private NoteDatabase noteDatabase;   створимо посилання відразу на NotesDao
    private NotesDao notesDao;

    private MutableLiveData<Boolean> shouldCloseScreen = new MutableLiveData<>();

    public AddNoteViewModel(@NonNull Application application) {
        super(application);
        notesDao = NoteDatabase.getInstance(application).notesDao();
    }

    public LiveData<Boolean> getShouldCloseScreen() {
        return shouldCloseScreen;
    }

    public void saveNote(Note note) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                notesDao.add(note);
                // .postValue - можна викликати з любого потоку. setValue в даному випадку не годиться,
                // тому що він працює тільки з головним потоком
                shouldCloseScreen.postValue(true);
            }
        });
        thread.start();
    }


}
