//
package com.chat.bluetooth.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.chat.bluetooth.R;
import com.chat.bluetooth.business.ChatBusinessLogic;
import com.chat.bluetooth.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GenericActivity{
	
	public static int MSG_TOAST = 1;
	public static int MSG_BLUETOOTH = 2;
	public static int JSON_BLUETOOTH = 3;
	public static int BT_TIMER_VISIBLE = 30;

	/*****CONSTANTS ****************************/
	public static int SONG_SELECT = 1;
	public static int SONG_SELECTED = 2;
	public static int DJ_COMMENT = 3;
	public static int SKIP_SONG  = 4;
	public static int ECHO_SHARED_PREF_SONGS = 5;
	public static int ECHO_BLOB_SONGS = 6;
	public static int REMOTE_SELECT = 7;
	public static int WANT_END = 8;
	/********************************************/
	
	private final int BT_ACTIVATE = 0;
	private final int BT_VISIBLE = 1;
	private final int DB_RETURN = 101;
	/******************************************/
	private Button buttonDB;
	private Button buttonClient;
	private Button buttonDelete;
	private Button buttonload;

	final int MY_PREFS_PRIV_MODE = Activity.MODE_PRIVATE;
	final String MY_PREFS_FILE = "MusicPreferences";
	private String mySdPath;
	// create a reference to the shared preferences object
	SharedPreferences mySharedPreferences;
	// obtain an editor to add data to my SharedPreferences object
	SharedPreferences.Editor myEditor;

	private ImageButton buttonSend;
	private EditText editTextMessage;
	private ListView listVewHistoric;
	private ArrayAdapter<String> historic;
	
	private ToastUtil toastUtil;
	private ChatBusinessLogic chatBusinessLogic;
	private ProgressDialog progressDialog;
    Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		
		settingsAttributes();
		settingsView();
		
		initializaBluetooth();
		registerFilters();
		context = this;
		sharedPreferences();
	}

	public void sharedPreferences(){
		mySdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		// create a reference to the SharedPreferences file
		mySharedPreferences = getSharedPreferences(MY_PREFS_FILE, MY_PREFS_PRIV_MODE);
		// obtain an editor to add data to (my)SharedPreferences object
		myEditor = mySharedPreferences.edit();

		String name = mySharedPreferences.getString("User", "");
		if(!name.equalsIgnoreCase("Tom"))
		{
			myEditor.putString("User", "Tom");
			toastUtil.showToast("saved");
		}

		myEditor.putBoolean("Flag", false);

		myEditor.commit();

		//String favColor = settings.getString("favorite_color", "default black");
		//int favNumber = settings.getInt("favorite_number", 0);

		//Toast.makeText(this, favColor + " " + favNumber, 1).show();
	}

	public void readSharedPreferences(){
		mySharedPreferences = getSharedPreferences(MY_PREFS_FILE,
			Activity.MODE_PRIVATE );


		String name = mySharedPreferences.getString("User", "");
		if(!name.equalsIgnoreCase(""))
		{
			name = name;
		}
		// retrieving data from SharedPreferences container
		//String favColor = mySharedPreferences.getString("User", "default black");
		//mySharedPreferences.getStringSet("UserPreferences", "UserDetails");
		//int favNumber = mySharedPreferences.getInt("favorite_number", 0);
		toastUtil.showToast(name);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
    }
        
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
	public void settingsAttributes() {
		toastUtil = new ToastUtil(this);
		//create business logic object for communication
		chatBusinessLogic = new ChatBusinessLogic(this, handler);
	}

	@Override
	public void settingsView() {
		editTextMessage = (EditText)findViewById(R.id.editTextMessage);
		
		historic = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		listVewHistoric = (ListView)findViewById(R.id.listVewHistoric);
		listVewHistoric.setAdapter(historic);
		
		buttonSend = (ImageButton)findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String message = editTextMessage.getText().toString(); 
				
				if(message.trim().length() > 0){
					if(chatBusinessLogic.sendMessage(message,2)){
						editTextMessage.setText(""); 
						
						historic.add("Me: " + message);
						historic.notifyDataSetChanged();			
					}
				}else{
					toastUtil.showToast(getString(R.string.enter_message));
				}
			}
		});

		buttonload = (Button)findViewById(R.id.load_button);
		buttonload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sharedPreferences();
				Intent dbIntent = new Intent (MainActivity.this,
					ToDoActivity.class);

				Bundle myDataBundle = new Bundle();

				// attach the container to the intent
				dbIntent.putExtras(myDataBundle);

				startActivityForResult(dbIntent, 101);
			}
		});

		buttonDelete = (Button)findViewById(R.id.delete_button);
		buttonDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				readSharedPreferences();
			//	toastUtil.showToast("delete");
			}
		});

		buttonClient = (Button)findViewById(R.id.buttonClient);
		buttonClient.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				chatBusinessLogic.startFoundDevices();
			}
		});

		buttonClient = (Button)findViewById(R.id.buttonClient);
		buttonClient.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				chatBusinessLogic.startFoundDevices();
			}
		});

		buttonDB = (Button)findViewById(R.id.db);
		buttonDB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent dbIntent = new Intent (MainActivity.this,
					ViewFlipperMainActivity.class);

				Bundle myDataBundle = new Bundle();

				String jsonArrayString = "[{\"song\":\"song1\",\"artist\":\"artist0\",\"votes\":0},{\"song\":\"song2\",\"artist\":\"artist1\",\"votes\":1},{\"song\":\"song3\", " +
				"\"artist\":\"artist2\",\"votes\":2},{\"song\":\"song4\",\"artist\":\"artist3\",\"votes\":3}," +
					"{\"song\":\"song5\",\"artist\":\"artist4\",\"votes\":4},"+
					"{\"song\":\"song5\",\"artist\":\"artist4\",\"votes\":4},"+
					"{\"song\":\"song5\",\"artist\":\"artist4\",\"votes\":4},"+
					"{\"song\":\"song5\",\"artist\":\"artist4\",\"votes\":4},"+
					"{\"song\":\"song5\",\"artist\":\"artist4\",\"votes\":4},"+
					"{\"song\":\"song5\",\"artist\":\"artist4\",\"votes\":4},"+
					"{\"song\":\"song6\",\"artist\":\"artist5\",\"votes\":5}]";
				//String json = "[{\"Song\":\"Song11\",\"Name\":\"ABC\"},{\"Song\":\"Song22\",\"Name\":\"PQR\"},{\"Song\":\"Song33\",\"Name\":\"XYZ\"}]";

				myDataBundle.putByteArray("package", jsonArrayString.getBytes());
			//	myDataBundle.putString("str", "testing String");

				// attach the container to the intent
				dbIntent.putExtras(myDataBundle);

				startActivityForResult(dbIntent, 101);
			}
		});
	}
	
	public void initializaBluetooth() {
		if (chatBusinessLogic.getBluetoothManager().verifySuportedBluetooth()) {
			if (!chatBusinessLogic.getBluetoothManager().isEnabledBluetooth()) { 
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
				startActivityForResult(enableBtIntent, BT_ACTIVATE);
			}
		} else {
			toastUtil.showToast(getString(R.string.no_support_bluetooth));
			finish();
		}
	}
	
	public void registerFilters(){
		chatBusinessLogic.registerFilter();
	}
	/*****************************************************************************************************/
	//handles messages received from music host
	private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            synchronized (msg) {
                switch (msg.what) {
                	case 1://SONG_SELECT
                		toastUtil.showToast("1 here");
                		break;
                	case 2://SONG_SELECTED
						toastUtil.showToast("2 here");
                		historic.add((String)(msg.obj));
       				 	historic.notifyDataSetChanged();
						break;
					case 3://DJ_COMMENT
						toastUtil.showToast("3 here");
						Intent dbIntent = new Intent (MainActivity.this,
							ViewFlipperMainActivity.class);

						Bundle myDataBundle = new Bundle();
						String packStr = new String ((String)(msg.obj));

						myDataBundle.putByteArray("package", (packStr.getBytes()));

						// attach the container to the intent
						dbIntent.putExtras(myDataBundle);

						startActivityForResult(dbIntent, 101);

       				 	listVewHistoric.requestFocus();
       				 	break;
					case 4://SKIP_SONG
						toastUtil.showToast("4 here");
						//
						break;
					case 5://ECHO_SHARED_PREF
						toastUtil.showToast("5 here");

						break;
					case 6://SONG_SELECTED
						toastUtil.showToast("6 here");

						break;
					case 7://SONG_SELECTED
						toastUtil.showToast("7 here");

						break;
					case 8://SONG_SELECTED
						toastUtil.showToast("8 here");
						break;
                }
            }
        };
    };
	/*****************************************************************************************************/

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode)
		{
			case BT_ACTIVATE:
				if (RESULT_OK != resultCode) {
					toastUtil.showToast(getString(R.string.activate_bluetooth_to_continue));
					finish(); 
				}
				break;
			case DB_RETURN:
				Bundle myResultBundle = data.getExtras();
				String myResult = myResultBundle.getString("result");
				String myResult2 = myResultBundle.getString("song");
				toastUtil.showToast(myResult);
				toastUtil.showToast(myResult2 + " " + myResult);
				if(chatBusinessLogic.sendMessage(myResult,1)) {
				}
				break;

//			case BT_VISIBLE:
//				if (resultCode == BT_TIMER_VISIBLE) {
//
//					chatBusinessLogic.stopCommucanition();
//					chatBusinessLogic.startServer();
//				} else {
//					toastUtil.showToast(getString(R.string.device_must_visible));
//				}
//				break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		chatBusinessLogic.unregisterFilter();
		chatBusinessLogic.stopCommucanition();
	}


private class updateGUI extends AsyncTask<String, Integer, List<String>> {

	@Override
	protected List<String> doInBackground(String... params) {
		List<String> returnList = new ArrayList<>();

		try {
			for(int i =0; i<10;i++) {
				Thread.sleep(1000);

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return returnList;
	}

	@Override
	protected void onPostExecute(List<String> result) {

	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		String cnt = Integer.toString(values[0]);
	//	progressDialog = ProgressDialog.show(context,
			//"test broadcast" + cnt,
		//	context.getText(R.string.msg_searching_devices));
	}
}
}