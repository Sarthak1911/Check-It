package app.com.example.majsarthak.check_it;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.podcopic.animationlib.library.AnimationType;
import com.podcopic.animationlib.library.StartSmartAnimation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SwipeMenuListView list_todo;
    FloatingActionButton add_todo;
    EditText add_task;
    TextView empty;
    Button add;
    TableRow add_task_container;
    Vibrator vib;
    CustomRow adapter;
    ListItem row;
    CoordinatorLayout laylist;
    ArrayList<ListItem> li = new ArrayList<>();
    Intent toNext;
    Handler handleAnim;
    DataHelper helper;
    SQLiteDatabase db;
    SQLQueries sqlQueries;
    Cursor result;
    Calendar calender;
    SimpleDateFormat simpleDateFormat;



    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {

            switch(menu.getViewType())
            {
                case 0 :
                {
                    SwipeMenuItem delete_item = new SwipeMenuItem(getApplicationContext());

                    delete_item.setBackground(R.color.delete_item);

                    delete_item.setWidth((int) (90*getResources().getDisplayMetrics().density));

                    delete_item.setIcon(R.drawable.ic_delete_white_24dp);

                    menu.addMenuItem(delete_item);

                    break;
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initialize();


        list_todo.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index)
            {
                switch(index)
                {
                    case 0 :
                    {
                        if (!li.isEmpty())
                        {
                            vib.vibrate(10);

                            try
                            {
                                db.execSQL(sqlQueries.deleteSelected(helper.getTableName(), li.get(position).getId()));

                                String deleteTask = li.get(position).getName();

                                li.remove(position);

                                if (getAllResults() <= 0)
                                {
                                    list_todo.setVisibility(View.GONE);
                                    empty.setVisibility(View.VISIBLE);
                                }

                                adapterRefresh();

                                Snackbar.make(laylist, deleteTask +  " is deleted!", Snackbar.LENGTH_SHORT ).show();
                            }
                            catch (SQLException sqlException)
                            {
                                Snackbar.make(findViewById(R.id.main), R.string.error, Snackbar.LENGTH_SHORT ).show();
                            }

                            break;
                        }
                    }
                }
                return false;
            }
        });

        list_todo.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        add_todo.setOnClickListener(this);

        add.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.completed :
            {
                toNext = new Intent(getApplicationContext(), Completed.class);
                startActivity(toNext);

                break;
            }

            case R.id.incomplete :
            {
                toNext = new Intent(getApplicationContext(), Incomplete.class);
                startActivity(toNext);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void initialize()
    {
        //for DB
        helper = new DataHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        sqlQueries = new SQLQueries();

        add_todo = (FloatingActionButton) findViewById(R.id.add_todo);
        add_task = (EditText) findViewById(R.id.add_task);
        empty = (TextView) findViewById(R.id.empty);
        empty.setVisibility(View.GONE);
        add = (Button) findViewById(R.id.add);
        add_task_container = (TableRow) findViewById(R.id.add_task_container);
        add_task_container.setVisibility(View.GONE);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        laylist = (CoordinatorLayout) findViewById(R.id.main);
        handleAnim = new Handler();
        list_todo = (SwipeMenuListView) findViewById(R.id.list_todo);
        list_todo.setMenuCreator(creator);
        getIn();
        adapterRefresh();

        Log.i("IS RUNNING: ", String.valueOf(isMyServiceRunning(NotifyService.class)));

        if (!isMyServiceRunning(NotifyService.class))
        {
            startService(new Intent(this, NotifyService.class));

            Log.i("IS RUNNING: ", String.valueOf(isMyServiceRunning(NotifyService.class)));
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void getIn() {
        //Get all to-dos from Database CheckIt
        //Check if table is Empty
        try
        {
            if (getAllResults() == 0)
            {
                //No results to display
                list_todo.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
            else
            {
                result = db.rawQuery(sqlQueries.getAllForToday(helper.getTableName(), getCurrentDate()), null);
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
                    li.add(row = new ListItem(id, todo_name, checked, date));

                    result.moveToNext();//Move to next result
                }
            }

            result.close();
        }
        catch(SQLException exception)
        {
            Snackbar.make(findViewById(R.id.main), R.string.error, Snackbar.LENGTH_SHORT).show();
        }
    }


    public void adapterRefresh()
    {
        adapter = new CustomRow(getApplicationContext(), R.layout.custom_row, li, helper.getTableName());
        list_todo.setAdapter(adapter);
    }

    public String getCurrentDate()
    {

        calender = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(" yyyy / MM / dd ", Locale.getDefault());

        return simpleDateFormat.format(calender.getTime());

    }

    public int getAllResults()
    {
        result = db.rawQuery(sqlQueries.getAllForToday(helper.getTableName(), getCurrentDate()), null);

        int count = result.getCount();
        result.close();

        return count;
    }

    public boolean addTask()
    {
        //Check for NULL
        if (!add_task.getText().toString().trim().isEmpty())
        {
            //Task Entered then add it

            //Check for Duplicates

//            if (!isSame(add_task.getText().toString().toLowerCase()))
//            {
                //Not empty and no duplicates then we can add task in Array List
                try
                {
                    db.execSQL(sqlQueries.addTodo(helper.getTableName(), add_task.getText().toString(), getCurrentDate()));

                    //Add to the list li
                    result = db.rawQuery(sqlQueries.getLastIdItems(helper.getTableName()), null);
                    if (result.getCount() == 0)
                    {
                        //Enter task in DB
                        result.close();
                    }
                    else
                    {
                        result.moveToFirst();

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
                        li.add(row = new ListItem(id, todo_name, checked, date));

                        result.close();

                    }

                    if (getAllResults() > 0)
                    {
                        list_todo.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.GONE);
                    }

                    adapterRefresh();
                    add_task.setText(null);
                    return true;
                }
                catch (SQLException exception)
                {
                    Snackbar.make(findViewById(R.id.main), R.string.error, Snackbar.LENGTH_SHORT).show();
                }
//            }

            //if Duplicate add_task Edit Text
            vib.vibrate(1000);

            Toast.makeText(getApplicationContext(), R.string.duplicate_edit, Toast.LENGTH_SHORT).show();

            StartSmartAnimation.startAnimation(findViewById(R.id.add_task), AnimationType.Shake, 500, 0, true);

            return false;//if Duplicates then false
        }

        //if Empty add_task Edit Text
        vib.vibrate(1000);

        add_task.setHint(R.string.empty_edit);

        StartSmartAnimation.startAnimation(findViewById(R.id.add_task), AnimationType.Shake, 400, 0, true);

        handleAnim.postDelayed(new Runnable() {
            @Override
            public void run() {

                StartSmartAnimation.startAnimation(findViewById(R.id.add_task), AnimationType.StandUp, 200, 0, true);

                add_task.setHint(R.string.add_task);

            }
        }, 1500);

        return false;//if Empty then false
    }


//    public boolean isSame(String task_string)
//    {
//        result = db.rawQuery(sqlQueries.isSame(helper.getTableName(), getCurrentDate(), task_string), null);
//
//        int check = result.getCount();
//
//        return check > 0;
//
//    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.add_todo :
            {
                vib.vibrate(10);

                StartSmartAnimation.startAnimation(findViewById(R.id.add_todo), AnimationType.FadeOut, 450, 0 , true);

                handleAnim.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        add_todo.setVisibility(View.GONE);

                        list_todo.setSwipeDirection(SwipeDismissBehavior.STATE_IDLE);
                    }
                },450);

                StartSmartAnimation.startAnimation(findViewById(R.id.add_task_container), AnimationType.SlideInUp, 450, 0 , true);

                handleAnim.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        add_task_container.setVisibility(View.VISIBLE);
                        add_task.requestFocus();

                    }
                }, 450);

                break;
            }

            case R.id.add :
            {

                if (addTask())
                {
                    StartSmartAnimation.startAnimation(findViewById(R.id.add_task_container), AnimationType.SlideOutDown, 450, 0 , true);

                    handleAnim.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            add_task_container.setVisibility(View.GONE);

                        }
                    }, 450);

                    StartSmartAnimation.startAnimation(findViewById(R.id.add_todo), AnimationType.FadeIn, 450, 0 , true);

                    handleAnim.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            add_todo.setVisibility(View.VISIBLE);

                            list_todo.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

                        }
                    },450);
                }
                break;

            }
        }

    }


}
