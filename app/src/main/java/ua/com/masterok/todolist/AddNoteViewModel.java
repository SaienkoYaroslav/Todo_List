package ua.com.masterok.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddNoteViewModel extends AndroidViewModel {

    // Замість   private NoteDatabase noteDatabase;   створимо посилання відразу на NotesDao
    private NotesDao notesDao;

    // Якщо створити об'єкт через new LiveData<Integer>(), то доведеться перевизначати дуже багато методів
    // можна використати успадковувача від LiveData - MutableLiveData;
    private MutableLiveData<Boolean> shouldCloseScreen = new MutableLiveData<>();

    public AddNoteViewModel(@NonNull Application application) {
        super(application);
        notesDao = NoteDatabase.getInstance(application).notesDao();
    }

    public LiveData<Boolean> getShouldCloseScreen() {
        return shouldCloseScreen;
    }

    public void saveNote(Note note) {
        // підписуємось на об'єкт RXJava Completable за допомогою метода .subscribe(), але це ще не
        // багатопоточність. Додавання виконується в головному потоці
        // new Action() в його методі run() вказуємо дію, яку потрібно виконати при успішному виконанні
        // додавання замітки
        // .subscribeOn() - це вже багатопоточність. В дужках вказується потік в якому буде виконуватись
        // код який знаходиться перед викликом цього методу. Фоновий потік в RXJava це Schedulers.io()
        // .observeOn() - перемикає потік для коду який йде після цього методу
        // AndroidSchedulers.mainThread() - це головний потік в RxJava. Робимо це для того щоб можна
        // було викликати метод .setValue(), так як він більш надійний
        notesDao.add(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
            @Override
            public void run() throws Throwable {
                // .postValue - можна викликати з любого потоку. setValue тільки з головним потоком.
                // Ці методи передають значення у об'єкт LiveData
                shouldCloseScreen.setValue(true);
            }
        });


    }


}
