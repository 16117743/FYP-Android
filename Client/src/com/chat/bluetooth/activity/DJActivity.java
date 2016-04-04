package com.chat.bluetooth.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.chat.bluetooth.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.chat.bluetooth.util.MyExpandableAdapter;
import com.chat.bluetooth.util.SongBean;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class DJActivity extends Activity {

private ViewFlipper viewFlipper;
public float lastX;

private ListView selectionList;
private ArrayAdapter<String> selectionHistoric;
private ExpandableListView queueList;
//private ExpandableListAdapter<> queueHistoric;
private ArrayList<String> parentItems = new ArrayList<String>();
private ArrayList<Object> childItems = new ArrayList<Object>();

private ArrayList<String> songQTitles = new ArrayList<String>();
private ArrayList<Object> songQItems = new ArrayList<Object>();
private ExpandableListView expandableList;

@Override
protected void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.dj_layout);
	viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
	init();


	//  ExpandableListView expandableList = getExpandableListView();
	expandableList = (ExpandableListView) findViewById(R.id.queueList);


	expandableList.setDividerHeight(2);
	expandableList.setGroupIndicator(null);
	expandableList.setClickable(true);

	MyExpandableAdapter adapter = new MyExpandableAdapter(this, parentItems, childItems);

	adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
	expandableList.setAdapter(adapter);
	/**adapter = new BookmarkAdapter(this ,book_mark_group ,book_mark_child);
	 adapter.changedata(this, book_mark_group, book_mark_child);
	 bookmark_list.setAdapter(adapter);*/
	//parentItems.setOnChildClickListener(this);
	expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
									int groupPosition, int childPosition, long id) {
			//Toast.makeText(this, ""+ childItems.get(childPosition),  Toast.LENGTH_SHORT).show();
			Toast.makeText(getApplicationContext(), parentItems.get(groupPosition), Toast.LENGTH_SHORT).show();
			Toast.makeText(getApplicationContext(), (String)childItems.get(childPosition), Toast.LENGTH_SHORT).show();
			// parentItems.get(childPosition));
			// Toast.makeText(this, .child.get(childPosition),
			//      Toast.LENGTH_SHORT).show();
			// Toast.makeText(context, ""+CatList.get(childPosition).getId(), 1).show();
			// TODO Auto-generated method stub
			return false;
		}
	});

}

// Method to handle touch event like left to right swap and right to left swap
public boolean onTouchEvent(MotionEvent touchevent)
{
	switch (touchevent.getAction())
	{
		// when user first touches the screen to swap
		case MotionEvent.ACTION_DOWN:
		{
			lastX = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP:
		{
			float currentX = touchevent.getX();

			// if left to right swipe on screen
			if (lastX < currentX)
			{
				// If no more View/Child to flip
				if (viewFlipper.getDisplayedChild() == 0)
					break;

				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Left and current Screen will go OUT from Right
				viewFlipper.setInAnimation(this, R.anim.in_from_left);
				viewFlipper.setOutAnimation(this, R.anim.out_to_right);
				// Show the next Screen
				viewFlipper.showNext();
			}

			// if right to left swipe on screen
			if (lastX > currentX)
			{
				if (viewFlipper.getDisplayedChild() == 1)
					break;
				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Right and current Screen will go OUT from Left
				viewFlipper.setInAnimation(this, R.anim.in_from_right);
				viewFlipper.setOutAnimation(this, R.anim.out_to_left);
				// Show The Previous Screen
				viewFlipper.showPrevious();
			}
			break;
		}
	}
	return false;
}

public void init(){
	Intent myLocalIntent = getIntent();

	selectionList = (ListView) findViewById(R.id.selectionList);
	selectionHistoric = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
	selectionList.setAdapter(selectionHistoric);

//	selectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//		public void onItemClick(AdapterView<?> parent, View view,
//								int position, long id) {
//			Intent myLocalIntent = getIntent();
//			Bundle thisBundle = new Bundle();
//			//myBundle = myLocalIntent.getExtras();
//
//			selectionHistoric.getItem(position);
//			thisBundle.putString("result", selectionHistoric.getItem(position));
//
//			myLocalIntent.putExtras(thisBundle);
//			setResult(Activity.RESULT_OK, myLocalIntent);
//
//			finish();
//		}
//	});/**********************************************//**********************************************/

	Bundle myBundle =  myLocalIntent.getExtras();
	/*****************************************************************************/
	final String msgFromHost = new String (myBundle.getByteArray("package"));
	new parseJSON().execute(msgFromHost);
}

/**** ASYNC TASK *************************/
private class parseJSON extends AsyncTask<String, Void, List<SongBean>> {

	@Override
	protected List <SongBean> doInBackground(String... params) {
		String packStr0 = new String(params[0]);
		String[] parts = packStr0.split("&");

		List <SongBean> returnList = new ArrayList<>();
		try {
				JSONArray array = new JSONArray(parts[0]);
				Log.d("msg", "got this far");
				SongBean songBean;
				for (int i = 0; i < array.length(); i++) {
					songBean = new SongBean();
					songBean.setDJComment(array.getJSONObject(i).getString("DJComment"));
					returnList.add(songBean);
				}
				JSONArray array2 = new JSONArray(parts[1]);
				Log.d("msg", "got this far 2");
				SongBean songBean2;
				for (int i = 0; i < array2.length(); i++) {
					songBean2 = new SongBean();
					songBean2.setSong(array2.getJSONObject(i).getString("song"));
					songBean2.setArtist(array2.getJSONObject(i).getString("artist"));
					songBean2.setVotes(Integer.parseInt(array2.getJSONObject(i).getString("votes")));
					returnList.add(songBean2);
				}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return returnList;
	}

	@Override
	protected void onPostExecute(List<SongBean> result) {
		ArrayList<String> child = new ArrayList<String>();
		for(int i=0; i<result.size(); i++) {
			if(result.get(i).getVotes()==0)
				selectionHistoric.add(result.get(i).getDJComment());
			else{
				parentItems.add(result.get(i).getSong());
				child.add("Artist: " +result.get(i).getArtist());
				child.add("skip votes:" + Integer.toString(result.get(i).getVotes()));
				childItems.add(child);
				child = new ArrayList<String>();
			}
		}
		selectionHistoric.notifyDataSetChanged();
		selectionList.requestFocus();
		// queueHistoric.notifyDataSetChanged();
		// queueList.requestFocus();
	}

	@Override
	protected void onPreExecute() {}

	@Override
	protected void onProgressUpdate(Void... values) {}
}
/**** ASYNC TASK *************************/

public void setGroupParents() {
	parentItems.add("song 1");
	parentItems.add("song 2");
	parentItems.add("song 3");
	parentItems.add("song 4");
}

public void setChildData() {

	// Android
	ArrayList<String> child = new ArrayList<String>();
	child.add("Artist - artist 1");
	child.add("votes - 2");
	childItems.add(child);


	// Core Java
	child = new ArrayList<String>();
	child.add("Artist - artist 2");
	child.add("votes - 4");
	childItems.add(child);

	// Desktop Java
	child = new ArrayList<String>();
	child.add("Artist - artist 3");
	child.add("votes - 1");
	childItems.add(child);


	// Enterprise Java
	child = new ArrayList<String>();
	child.add("Artist - artist 4");
	child.add("votes - 0");
	childItems.add(child);
}
}