package ua.com.masterok.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddNoteViewModel extends AndroidViewModel {

    // Замість   private NoteDatabase noteDatabase;   створимо посилання відразу на NotesDao
    private NotesDao notesDao;

    // Тип який повертає метод .subscribe() з RxJava. В цього об'єкта можна викликати метод
    // dispose(), який відміняє підписку. Це потрібно виконати при знищенні ВьюМоделі, щоб не було витоку пам’яті
    // За допомогою цього об'єкту можна керувати життєвим циклом підписки
    // private Disposable disposable;

    // Типу як колекція об'єктів Disposable. Потрібен, якщо в нас буде багато об'єктів Disposable і
    // щоб в кожного окремо не викликати один і той же метод, можна його викликати в об'єкті CompositeDisposable
    // перед цим об'єкти Disposable потрібно додати до CompositeDisposable через метод .add()
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        Disposable disposable = notesDao.add(note)
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
        compositeDisposable.add(disposable);
    }

    // Метод який викликаєтсья при закінченні життєвого циклу ВьюМоделі
    @Override
    protected void onCleared() {
        super.onCleared();
        // можна і так
        //disposable.dispose();

        compositeDisposable.dispose();
    }
}
