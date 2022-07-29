package ua.com.masterok.todolist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

// DAO - data access object (об'єкт доступу до даних)

@Dao
public interface NotesDao {

    @Query("SELECT * FROM notes")
    // На об'єкт LiveData можна підписуватись, щоб реагувати на всі зміни в List<Note>
    // Коли ми тут вказуємо тип повернення LiveData, то запит до БД робиться автоматично у фоновому
    // потоці!
    LiveData<List<Note>> getNotes();
    // Важливо! Взаємодія у архітектурі MVVM будується через об'єкт LiveData

    // onConflict = описує дію при ситуації, коли ми вставляємо в базу об'єкт який вже є в базі
    // Так можна реалізувати редагування
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Completable - клас з бібліотеки RxJava3. На нього можна підписуватись. Потрібен, щоб знати
    // успішно чи неуспішно виконався метод. Якщо не підписатись нічого не відбудеться
    Completable add(Note note);

    // :id - через : передається параметр з методу remove(int id)
    @Query("DELETE FROM notes WHERE id = :id")
    void remove(int id);

}
