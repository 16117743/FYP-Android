package com.chat.bluetooth.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;
import com.chat.bluetooth.R;


public class MyExpandableAdapter extends BaseExpandableListAdapter {

	private Activity activity;
	private ArrayList<Object> childtems;
	private LayoutInflater inflater;
	private ArrayList<String> parentItems, child;
	public Context context;
	private Activity mActivity;
	public Intent intent;

	public MyExpandableAdapter(Activity activity, ArrayList<String> parents, ArrayList<Object> childern) {
		this.parentItems = parents;
		this.childtems = childern;
		this.mActivity = activity;
	}

	public void setInflater(LayoutInflater inflater, Activity activity) {
		this.inflater = inflater;
		this.activity = activity;
	}

	
	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
		child = (ArrayList<String>) childtems.get(groupPosition);
		
		TextView textView = null;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.group, null);
		}
		
		textView = (TextView) convertView.findViewById(R.id.textView1);
		textView.setText(child.get(childPosition));
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
//				if(childPosition == 1) {
//					Intent myLocalIntent = mActivity.getIntent();
//					Bundle myBundle = new Bundle();
//					myBundle.putString("result", child.get(childPosition));
//					myBundle.putString("song", parentItems.get(groupPosition));
//					myLocalIntent.putExtras(myBundle);
//					mActivity.setResult(Activity.RESULT_OK, myLocalIntent);
//					//setResult(Activity.RESULT_OK, myLocalIntent);
//					mActivity.finish();
//				}
			}
		});
		
		return convertView;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row, null);
		}
		
		((CheckedTextView) convertView).setText(parentItems.get(groupPosition));
		((CheckedTextView) convertView).setChecked(isExpanded);
		
		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) childtems.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return parentItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
		//return false;
	}

}
