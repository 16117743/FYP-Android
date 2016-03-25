package com.chat.bluetooth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.chat.bluetooth.R;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ViewFlipperMainActivity extends Activity
{
    private ViewFlipper viewFlipper;
    private float lastX;

    private ListView selectionList;
    private ArrayAdapter<String> selectionHistoric;
    private Bundle myBundle;
    private Intent myLocalIntent;

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.view_flipper_main);
             viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
             init();
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

        selectionList = (ListView)findViewById(R.id.selectionList);
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
        }
        selectionHistoric.notifyDataSetChanged();
        selectionList.requestFocus();
        }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}
        }
    /**** ASYNC TASK *************************/
}