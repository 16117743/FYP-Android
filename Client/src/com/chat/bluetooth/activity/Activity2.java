package com.chat.bluetooth.activity;

import android.app.Activity;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.chat.bluetooth.R;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class Activity2 extends Activity implements OnClickListener, View.OnTouchListener {
	Button  btnDone;
	private ListView listView2;
	private ArrayAdapter<String> historic;
	private Bundle myBundle;
	private Intent myLocalIntent;
	public com.chat.bluetooth.util.JSONUtils util;
	public Toast myToast;
	private float x1,x2;
	static final int MIN_DISTANCE = 150;
	float initialX, initialY;
 	String TAG = "tag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main2);
		setContentView(R.layout.view_flipper_main);

		myBundle = savedInstanceState;
		init();
		//myBundle =  myLocalIntent.getExtras();
//		util = new com.chat.bluetooth.util.JSONUtils();
		/*****************************************************************************/
		//final String msgFromHost = new String (myBundle.getByteArray("package"));
		//new parseJSON().execute(msgFromHost);

	}

	/**** ASYNC TASK *************************/
	private class parseJSON extends AsyncTask<String, Void, List <String>> {

		@Override
		protected List <String> doInBackground(String... params) {
			List <String> returnList = new ArrayList<>();
			try {
				JSONArray array = new JSONArray(params[0]);
				Log.d("msg", "got this far");
				for(int i=0; i<array.length(); i++){
					returnList.add(array.getJSONObject(i).getString("song"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return returnList;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			for(int i=0; i<result.size(); i++) {
				historic.add("      " + result.get(i));
			}
			historic.notifyDataSetChanged();
			listView2.requestFocus();
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}
	/**** ASYNC TASK *************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clean:
				historic.clear();
				historic.notifyDataSetChanged();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Intent myLocalIntent = getIntent();
		Bundle thisBundle = new Bundle();
		myBundle =  myLocalIntent.getExtras();
		thisBundle.putString("result", "end");
		myLocalIntent.putExtras(thisBundle);
		setResult(Activity.RESULT_OK, myLocalIntent);
		finish();
	}

	public void init(){
		btnDone = (Button) findViewById(R.id.btnDone);
		btnDone.setOnClickListener(this);

		myLocalIntent = getIntent();

		listView2 = (ListView)findViewById(R.id.listViewHistoric2);
		historic = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		listView2.setAdapter(historic);

		listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent myLocalIntent = getIntent();
				Bundle thisBundle = new Bundle();
				myBundle =  myLocalIntent.getExtras();

				historic.getItem(position);
				thisBundle.putString("result", historic.getItem(position));

				myLocalIntent.putExtras(thisBundle);
				setResult(Activity.RESULT_OK, myLocalIntent);

				finish();
			}
		});/**********************************************//**********************************************/
	}//init
public boolean onTouch(View v, MotionEvent event) {
	int action = event.getActionMasked();

	switch (action) {

		case MotionEvent.ACTION_DOWN:
			initialX = event.getX();
			initialY = event.getY();

			Log.d(TAG, "Action was DOWN");
			break;

		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "Action was MOVE");
			break;

		case MotionEvent.ACTION_UP:
			float finalX = event.getX();
			float finalY = event.getY();

			Log.d(TAG, "Action was UP");

			if (initialX < finalX) {
				Log.d(TAG, "Left to Right swipe performed");
			}

			if (initialX > finalX) {
				Log.d(TAG, "Right to Left swipe performed");
			}

			if (initialY < finalY) {
				Log.d(TAG, "Up to Down swipe performed");
			}

			if (initialY > finalY) {
				Log.d(TAG, "Down to Up swipe performed");
			}

			break;

		case MotionEvent.ACTION_CANCEL:
			Log.d(TAG,"Action was CANCEL");
			break;

		case MotionEvent.ACTION_OUTSIDE:
			Log.d(TAG, "Movement occurred outside bounds of current screen element");
			break;
	}

	return super.onTouchEvent(event);
}
}
/**View.setOnTouchListener(new OnSwipeTouchListener(Activity2.this) {
 public void onSwipeTop() {
 Toast.makeText(Activity2.this, "top", Toast.LENGTH_SHORT).show();
 }
 public void onSwipeRight() {
 Toast.makeText(Activity2.this, "right", Toast.LENGTH_SHORT).show();
 }
 public void onSwipeLeft() {
 Toast.makeText(Activity2.this, "left", Toast.LENGTH_SHORT).show();
 }
 public void onSwipeBottom() {
 Toast.makeText(Activity2.this, "bottom", Toast.LENGTH_SHORT).show();
 }

 });*/