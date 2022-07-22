package ua.com.masterok.todolist;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    // singleton
    private static NoteDatabase instance = null;
    private static final String DB_NAME = "notes.db";

    // Application - передається, як контекст. Краще передавати аплікейшн, а не зіс, щоб не було
    // витоку пам’яті.
    public static NoteDatabase getInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(application, NoteDatabase.class, DB_NAME).build();
        }
        return instance;
    }
    // .databaseBuilder створює спадкоємця класу NoteDatabase і повертає його об’єкт


    public abstract NotesDao notesDao();

}
