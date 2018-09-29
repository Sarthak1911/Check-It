package app.com.example.majsarthak.check_it;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class CustomStaticRow  extends ArrayAdapter<ListItem>{

    private Context context;
    private ArrayList<ListItem> completed_list;

    CustomStaticRow(Context context, int resource, ArrayList<ListItem> completed_list) {
        super(context, resource, completed_list);

        this.context = context;

        this.completed_list = new ArrayList<>();

        this.completed_list.addAll(completed_list);

    }

    private class ViewHolder
    {
        TextView completed_todo, created_at;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_static, null);

            holder = new ViewHolder();

            holder.completed_todo = (TextView) convertView.findViewById(R.id.completed_todo);
            holder.created_at = (TextView) convertView.findViewById(R.id.created_at);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem item = completed_list.get(position);

        holder.completed_todo.setText(item.getName());
        holder.created_at.setText(item.getDate());
        holder.created_at.setTag(item);

        return convertView;
    }
}
