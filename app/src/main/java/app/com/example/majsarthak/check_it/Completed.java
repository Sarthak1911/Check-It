package app.com.example.majsarthak.check_it;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Completed extends AppCompatActivity {

    ListView completed_list;
    ArrayList<ListItem> checked_tasks = new ArrayList<>();
    CustomStaticRow adapter;
    Cursor result;
    DataHelper helper;
    SQLiteDatabase db;
    SQLQueries sqlQueries;
    ListItem row;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialize();

        showToast();

    }

    public void initialize()
    {
        completed_list = (ListView) findViewById(R.id.completed_list);

        //For DB
        helper = new DataHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        sqlQueries = new SQLQueries();
        getIn();
        adapterRefresh();
    }

    public void adapterRefresh()
    {
        adapter = new CustomStaticRow(getApplicationContext(), R.layout.custom_row_static, checked_tasks);
        completed_list.setAdapter(adapter);
    }

    public int getCheckedCount()
    {
        result = db.rawQuery(sqlQueries.getChecked(helper.getTableName()), null);

        int count = result.getCount();

        result.close();

        return count;
    }

    public void showToast()
    {
        toast = Toast.makeText(getApplicationContext(), getText(R.string.completed_tasks).toString() + String.valueOf(getCheckedCount()), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void getIn()
    {
        try
        {
            if (getCheckedCount() <= 0)
            {
                //No checked tasks
                Toast.makeText(getApplicationContext(), String.valueOf(getCheckedCount()), Toast.LENGTH_SHORT).show();
            }
            else
            {
                result = db.rawQuery(sqlQueries.getChecked(helper.getTableName()), null);
                result.moveToFirst();

                while(!result.isAfterLast())
                {
                    //Retrieve from DB and add in li
                    int id = Integer.parseInt(result.getString(result.getColumnIndex("id")));
                    String todo_name = result.getString(result.getColumnIndex("todo_name"));
                    Boolean checked = null;
                    if (result.getInt(result.getColumnIndex("checked")) == 1)
                    {
                        checked = true;
                    }
                    else if (result.getInt(result.getColumnIndex("checked")) == 0)
                    {
                        checked = false;
                    }
                    String date = result.getString(result.getColumnIndex("createdAt"));
                    checked_tasks.add(row = new ListItem(id, todo_name, checked, date));

                    result.moveToNext();//Move to next result
                }
            }

            result.close();
        }
        catch(SQLException sqlException)
        {
            Snackbar.make(findViewById(R.id.main), R.string.error, Snackbar.LENGTH_SHORT).show();
        }
    }

}
