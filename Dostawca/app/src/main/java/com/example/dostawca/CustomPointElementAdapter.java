package com.example.dostawca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.dostawca.dto.Point;
import com.example.dostawca.service.CurrentRouteService;

import java.util.ArrayList;
import java.util.List;

public class CustomPointElementAdapter extends BaseAdapter implements ListAdapter {
    private List<Point> points;
    private Context context;


    public CustomPointElementAdapter(List<Point> list, Context context) {
        this.points = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return points.size();
    }

    @Override
    public Object getItem(int pos) {
        return points.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_of_addresses_element, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
        listItemText.setText(points.get(position).getName() + "\n" + points.get(position).getCoordinates());

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button) view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                points.remove(position); //or some other task
//                CurrentRouteService.getCurrentRoute().getPoints().remove(position);
                notifyDataSetChanged();
            }
        });


        return view;
    }
}