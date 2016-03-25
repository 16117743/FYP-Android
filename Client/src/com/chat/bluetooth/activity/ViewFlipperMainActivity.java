package com.chat.bluetooth.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.chat.bluetooth.R;
import com.chat.bluetooth.util.MyExpandableAdapter;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ViewFlipperMainActivity extends Activity
{
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
         setContentView(R.layout.view_flipper_main);
         viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
         init();


      //  ExpandableListView expandableList = getExpandableListView();
        expandableList = (ExpandableListView) findViewById(R.id.queueList);


        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        setGroupParents();
        setChildData();
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

        selectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent myLocalIntent = getIntent();
                Bundle thisBundle = new Bundle();
                //myBundle = myLocalIntent.getExtras();

                selectionHistoric.getItem(position);
                thisBundle.putString("result", selectionHistoric.getItem(position));

                myLocalIntent.putExtras(thisBundle);
                setResult(Activity.RESULT_OK, myLocalIntent);

                finish();
            }
        });/**********************************************//**********************************************/

       /* queueList = (ListView)findViewById(R.id.queueList);
        queueHistoric = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        queueList.setAdapter(queueHistoric);

        queueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent myLocalIntent = getIntent();
                Bundle thisBundle = new Bundle();
                //myBundle = myLocalIntent.getExtras();

               // queueHistoric.getItem(position);
               // thisBundle.putString("result", queueHistoric.getItem(position));

                myLocalIntent.putExtras(thisBundle);
                setResult(Activity.RESULT_OK, myLocalIntent);

                finish();
            }
        });/**********************************************//**********************************************/

        Bundle myBundle =  myLocalIntent.getExtras();
//		util = new com.chat.bluetooth.util.JSONUtils();
        /*****************************************************************************/
        final String msgFromHost = new String (myBundle.getByteArray("package"));
        new parseJSON().execute(msgFromHost);
    }

    /**** ASYNC TASK *************************/
    private class parseJSON extends AsyncTask<String, Void, List<String>> {

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
        selectionHistoric.add("      " + result.get(i));
        //queueHistoric.add("      " + result.get(i));
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