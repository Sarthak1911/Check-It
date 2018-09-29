package app.com.example.majsarthak.check_it;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DataHelper extends SQLiteOpenHelper {


    /*
        Attributes in ToDos
        id INTEGER AUTOINCREMENT,
        todo_name VARCHAR(25) Primary Key,
        checked INTEGER DEFAULT 0,
        createdAt TEXT

     */

    private static final String DATABASE_NAME = "CheckIt";
    private static final String TABLE_NAME = "ToDos";
    private static final int DATABASE_VERSION = 1;
    private static final String ID = "id";
    private static final String TODO_NAME = "todo_name";
    private static final String CHECKED = "checked";
    private static final String CREATED_AT = "createdAt";

    private SQLQueries sql = new SQLQueries();

    DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(sql.addTable(TABLE_NAME, ID, TODO_NAME, CHECKED, CREATED_AT));

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(sql.dropTableIf(TABLE_NAME));

        onCreate(sqLiteDatabase);
    }

    String getTableName()
    {
        return TABLE_NAME;
    }
}
