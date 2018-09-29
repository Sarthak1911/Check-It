package app.com.example.majsarthak.check_it;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static app.com.example.majsarthak.check_it.R.id.check_todo;
import static app.com.example.majsarthak.check_it.R.id.todo;


class CustomRow extends ArrayAdapter<ListItem> {

    private DataHelper helper;
    private SQLQueries sqlQueries;
    private SQLiteDatabase db;
    private int checked;

    private ArrayList<ListItem> list;
    private Context context;
    private String table_name;

    CustomRow(Context context, int resource, ArrayList<ListItem> list, String table_name)
    {
        super(context, resource, list);

        this.list = new ArrayList<>();

        this.list.addAll(list);

        this.context = context;

        this.table_name = table_name;
    }

    private class ViewHolder{

        CheckBox check_todo;
        TextView todo;

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null)
        {
            LayoutInflater listInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = listInflater.inflate(R.layout.custom_row, null);

            holder = new ViewHolder();

            holder.check_todo = (CheckBox) convertView.findViewById(check_todo);

            holder.todo = (TextView) convertView.findViewById(todo);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.check_todo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                ListItem listItem = getItem(position);

                if (b)
                {
                    helper = new DataHelper(context);
                    db = helper.getWritableDatabase();
                    sqlQueries = new SQLQueries();

                    checked = 1;

                    try
                    {
                        db.execSQL(sqlQueries.setChecked(table_name, checked, listItem.getId()));

                        listItem.setChecked(b);

                        holder.check_todo.setEnabled(false);
                        holder.todo.setPaintFlags(holder.todo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.todo.setTextColor(context.getResources().getColor(R.color.black_overlay));

                    }
                    catch(SQLException sqlException)
                    {
                        Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    holder.check_todo.setEnabled(true);
                    holder.todo.setPaintFlags(holder.todo.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }

            }
        });

        ListItem todo_list = list.get(position);
        holder.todo.setText(todo_list.getName());
        if (!todo_list.isChecked())
        {
            holder.todo.setTextColor(context.getResources().getColor(R.color.app_font));
        }
        holder.check_todo.setChecked(todo_list.isChecked());
        holder.check_todo.setTag(todo_list);

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
