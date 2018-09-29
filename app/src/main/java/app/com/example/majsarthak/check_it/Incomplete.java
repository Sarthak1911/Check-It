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

public class Incomplete extends AppCompatActivity {

    ListView incomplete_list;
    ArrayList<ListItem> unchecked_tasks = new ArrayList<>();
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
        setContentView(R.layout.activity_incomplete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialize();
        showToast();

    }

    public void initialize()
    {
        incomplete_list = (ListView) findViewById(R.id.incomplete_list);

        //For DB
        helper = new DataHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        sqlQueries = new SQLQueries();
        getIn();
        adapterRefresh();
    }

    public void adapterRefresh()
    {
        adapter = new CustomStaticRow(getApplicationContext(), R.layout.custom_row_static, unchecked_tasks);
        incomplete_list.setAdapter(adapter);
    }

    public int getUnCheckedCount()
    {
        result = db.rawQuery(sqlQueries.getUnChecked(helper.getTableName()), null);

        int count = result.getCount();

        result.close();

        return count;
    }

    public void showToast()
    {
        toast = Toast.makeText(getApplicationContext(), getText(R.string.incomplete_tasks).toString() + String.valueOf(getUnCheckedCount()), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void getIn()
    {
        try
        {
            if (getUnCheckedCount() <= 0)
            {
                //No checked tasks
                Toast.makeText(getApplicationContext(), String.valueOf(getUnCheckedCount()), Toast.LENGTH_SHORT).show();
            }
            else
            {
                result = db.rawQuery(sqlQueries.getUnChecked(helper.getTableName()), null);
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
                    unchecked_tasks.add(row = new ListItem(id, todo_name, checked, date));

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
