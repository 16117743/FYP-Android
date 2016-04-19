package com.chat.bluetooth.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.chat.bluetooth.util.ComBean;
import com.chat.bluetooth.R;
import com.chat.bluetooth.util.MyExpandableAdapter;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Thomas Flynn
 * 19-03-16
 */
public class SongRequestActivity extends Activity
{
    private ViewFlipper viewFlipper;
    public float lastX;

    private ListView selectionList;
    private ArrayAdapter<String> selectionHistoric;

    private ArrayList<String> queueSongItems = new ArrayList<String>();
    private ArrayList<Object> queueChildItems = new ArrayList<Object>();

    private ExpandableListView queueExpandableList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.view_flipper_main);
         viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
         init();

        queueExpandableList = (ExpandableListView) findViewById(R.id.queueList);
        queueExpandableList.setDividerHeight(2);
        queueExpandableList.setGroupIndicator(null);
        queueExpandableList.setClickable(true);

        MyExpandableAdapter adapter = new MyExpandableAdapter(this, queueSongItems, queueChildItems);

        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        queueExpandableList.setAdapter(adapter);

        queueExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Toast.makeText(getApplicationContext(), queueSongItems.get(groupPosition), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), (String) queueChildItems.get(childPosition), Toast.LENGTH_SHORT).show();
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

                selectionHistoric.getItem(position);
                thisBundle.putString("result", selectionHistoric.getItem(position));

                myLocalIntent.putExtras(thisBundle);
                setResult(Activity.RESULT_OK, myLocalIntent);

                finish();
            }
        });

        Bundle myBundle =  myLocalIntent.getExtras();

        final String msgFromHost = new String (myBundle.getByteArray("package"));
        new parseJSON().execute(msgFromHost);

    }


    /** ASYNC TASK */
    private class parseJSON extends AsyncTask<String, Void, List<ComBean>> {

    @Override
    protected List <ComBean> doInBackground(String... params) {
        String packStr0 = new String(params[0]);
        //'&' character separates selection from queue list
        String[] parts = packStr0.split("&");

        List <ComBean> returnList = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(parts[0]);

            ComBean comBean;
            for (int i = 0; i < array.length(); i++) {
                comBean = new ComBean();
                comBean.setSong(array.getJSONObject(i).getString("song"));
                returnList.add(comBean);
            }
            JSONArray array2 = new JSONArray(parts[1]);

            ComBean comBean2;
            for (int i = 0; i < array2.length(); i++) {
                comBean2 = new ComBean();
                comBean2.setSong(array2.getJSONObject(i).getString("song"));
                comBean2.setArtist(array2.getJSONObject(i).getString("artist"));
                comBean2.setVotes(Integer.parseInt(array2.getJSONObject(i).getString("votes")));
                returnList.add(comBean2);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    return returnList;
    }

    @Override
    protected void onPostExecute(List<ComBean> result) {
        ArrayList<String> child = new ArrayList<String>();
        for(int i=0; i<result.size(); i++) {
            if(result.get(i).getVotes()==0)
                selectionHistoric.add(result.get(i).getSong());
            else{
                queueSongItems.add(result.get(i).getSong());
                child.add("Artist: " +result.get(i).getArtist());
                child.add("Veto's required:" + Integer.toString(result.get(i).getVotes()));
                queueChildItems.add(child);
                child = new ArrayList<String>();
            }
        }
        selectionHistoric.notifyDataSetChanged();
        selectionList.requestFocus();
        }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}

    }//end async task
}