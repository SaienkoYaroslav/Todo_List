package ua.com.masterok.todolist;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainViewModel(@NonNull Application application) {
        super(application);
        // значення БД призначаємо в конструкторі
        noteDatabase = NoteDatabase.getInstance(application);
    }

    // Реалізація видалення замітки
    public void remove(Note note) {
        Disposable disposable = noteDatabase.notesDao().remove(note.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.i("RXJAVA3", "hello");
                    }
                });
        compositeDisposable.add(disposable);
    }

    public LiveData<List<Note>> getNotes() {
        return noteDatabase.notesDao().getNotes();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
