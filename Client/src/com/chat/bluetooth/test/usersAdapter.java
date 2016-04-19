package com.chat.bluetooth.test;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.chat.bluetooth.R;

/**
 * Adapter to bind a ToDoItem List to a view
 */
public class usersAdapter extends ArrayAdapter<users> {

/**
 * Adapter context
 */
Context mContext;

/**
 * Adapter View layout
 */
int mLayoutResourceId;

public usersAdapter(Context context, int layoutResourceId) {
    super(context, layoutResourceId);

    mContext = context;
    mLayoutResourceId = layoutResourceId;
}

/**
 * Returns the view for a specific item on the list
 */
@Override
public View getView(int position, View convertView, ViewGroup parent) {
    View row = convertView;

    //final ToDoItem currentItem = getItem(position);
    final users currentItem = getItem(position);

    if (row == null) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        row = inflater.inflate(mLayoutResourceId, parent, false);
    }

    row.setTag(currentItem);
    final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkToDoItem);
    checkBox.setText(currentItem.getText());
    checkBox.setChecked(false);
    checkBox.setEnabled(true);

    final TextView textView = (TextView) row.findViewById(R.id.user);
    final TextView textView2 = (TextView) row.findViewById(R.id.password);

    checkBox.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
        }
    });



    return row;
}

}