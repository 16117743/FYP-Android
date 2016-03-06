package com.chat.bluetooth.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.chat.bluetooth.R;
import com.chat.bluetooth.util.MySQLiteHelper;
import com.chat.bluetooth.util.ToastUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Activity2 extends Activity implements OnClickListener{
	EditText txtMsg;
	Button  btnDone;
	private Button btnReadSDFile;
	private Button btnUpdate;
	private Button btnDelete;
	private Button btnAdd;
	private  String mySdPath;
	private ListView listView2;
	private ArrayAdapter<String> historic;
	private Bundle myBundle;
	private Intent myLocalIntent;
	SQLiteDatabase db;
	private ToastUtil toastUtil;
	//MySQLiteHelper dbs;
	//private String myDbPath1 = "data/data/cis470.matos.databases/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		myBundle = savedInstanceState;
		toastUtil = new ToastUtil(this);
		init();
		myBundle =  myLocalIntent.getExtras();
		/*****************************************************************************/
		String msg1 = new String (myBundle.getByteArray("package"));
		//toastUtil.showToast(msg1);
		final byte[] param = myBundle.getByteArray("package");
		new LongOperation().execute(param);
//		List <String> returnList = new ArrayList<>();
//		try {
//
//			//	obj = new JSONObject(params.toString());
//			//	JSONObject o = new JSONParser().parse("{\"a\": \"A\"}");
//			JSONArray array = new JSONArray(msg1);
//
//			for(int i=0; i<array.length(); i++){
//				//JSONObject jsonObj  = result;
//				returnList.add(array.getJSONObject(i).getString("Song"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		//openDatabase();
	//	setResult(Activity.RESULT_OK, myLocalIntent);
	}

	public void init(){
		//txtMsg = (EditText) findViewById(R.id.etDataReceived);
		btnDone = (Button) findViewById(R.id.btnDone);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnDone.setOnClickListener(this);
		mySdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		myLocalIntent = getIntent();
		/**************************************************/
		listView2 = (ListView)findViewById(R.id.listView2);
		historic = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		listView2.setAdapter(historic);
		/************************************************************/
		openDatabase(); // open (create if needed) database
		dropTable(); // if needed drop table tblAmigos
		insertSomeDbData();
		/**********************************************//**********************************************/

		listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				//String test = historic.getItem(position);
				//prestationEco str=(prestationEco)o;//As you are using Default String Adapter

				//Toast.makeText(getBaseContext(), historic.getItem(position), Toast.LENGTH_SHORT).show();

				Intent myLocalIntent = getIntent();
			//	Bundle myBundle = new Bundle();
				myBundle =  myLocalIntent.getExtras();

				//String byteToString = new String(myBundle.getByteArray("package"));

//				try {
//					JSONArray array = new JSONArray(byteToString);
//					for(int i=0; i<array.length(); i++){
//                        JSONObject jsonObj  = array.getJSONObject(i);
//						toastUtil.showToast(jsonObj.getString("No"));
//						toastUtil.showToast(jsonObj.getString("Name"));
//                    }
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}

				//historic.getItem(position)
			//	myBundle.putString("result", myBundle.getString("str"));
		//		toastUtil.showToast(myBundle.getString("str"));
				// attach updated bumble to invoking intent

				myLocalIntent.putExtras(myBundle);
				setResult(Activity.RESULT_OK, myLocalIntent);
			//	finish();
			}
		});/**********************************************//**********************************************/

		btnReadSDFile = (Button) findViewById(R.id.buttonsd);
		btnReadSDFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Double v1 = Double.parseDouble(txtValue1.getText().toString());
				//String tableName = f1.getText().toString();
				//String tableName2 = f2.getText().toString();
				//showTable("tableDB");
				useCursor1();
			}// onClick
		});

		btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				useUpdateMethod();
				//updateDB();
			}// onClick
		});

		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//String tableName = f1.getText().toString();
				//	String tableName2 = f2.getText().toString();
				//	dbs.helperUseDeleteMethod(tableName);
			}// onClick
		});

		btnAdd.setOnClickListener(new OnClickListener() {/***************************************************/
		@Override
		public void onClick(View v) {
			//String tableName = f1.getText().toString();
			//String tableName2 = f2.getText().toString();
			//addMethod(tableName, tableName2);
			//	dbs.helperAddMethod("song1","artist1");
			useAddMethod("song1", "song2");
		}// onClick
		});
	}

	/**** ASYNC TASK *************************/
	private class LongOperation extends AsyncTask<byte[], Void, List <String>> {

		@Override
		protected List <String> doInBackground(byte[]... params) {
			//JSONArray array = null;
			JSONObject obj = null;
			List <String> returnList = new ArrayList<>();
			returnList.add(params.toString());
			//Log.d("parameters size ", params.);
			final String par = params.toString();
			Log.d("parameters ", par);
			try {

			//	obj = new JSONObject(params.toString());
			//	JSONObject o = new JSONParser().parse("{\"a\": \"A\"}");
				JSONArray array = new JSONArray(par);
				Log.d("msg", "got this far");
				for(int i=0; i<array.length(); i++){
					//JSONObject jsonObj  = result;
					returnList.add(array.getJSONObject(i).getString("Song"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return returnList;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			Log.d("msg", "got to post exe");
			//Log.d("msg", "got this far");
			//	TextView txt = (TextView) findViewById(R.id.output);
			//	txt.setText("Executed"); // txt.setText(result);
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
//			try {
//			for(int i=0; i<result.size(); i++){
//				JSONObject jsonObj  = result;
//				//toastUtil.showToast(jsonObj.getString("No"));
//				//toastUtil.showToast(jsonObj.getString("Name"));
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
			if(result !=null) {
				toastUtil.showToast(result.get(1));
			}
//			for(int i=0; i<result.size(); i++) {
//				historic.add("" + result.get(i));
//			}
//			historic.notifyDataSetChanged();
//			listView2.requestFocus();
			else if(result == null)
				toastUtil.showToast("testing done");
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

	/*********************************************************************************************/
	/*********************************************************************************************/
	/*********************************************************************************************/
	// database methods

	private void useCursor1() {
		try {
			// this is similar to showCursor(...)
			// obtain a list of records[recId, name, phone] from DB
			String[] columns = { "ID", "song", "artist", "songdata" };
			// using simple parametric cursor
			Cursor c = db.query("tableDB", columns, null, null, null, null,
					"ID");

			int theTotal = c.getCount();
		//	txtMsg.append("\n-useCursor1 - Total rec " + theTotal);
		//	txtMsg.append("\n");
			int idCol = c.getColumnIndex("ID");
			int nameCol = c.getColumnIndex("song");
			int phoneCol = c.getColumnIndex("artist");

			c.moveToPosition(-1);
			while (c.moveToNext()) {
				columns[0] = Integer.toString((c.getInt(idCol)));
				columns[1] = c.getString(nameCol);
				columns[2] = c.getString(phoneCol);

			//	txtMsg.append(columns[0] + " " + columns[1] + " " + columns[2]
			//			+ "\n");

				historic.add("ID:" + columns[0] + " - " + columns[1] + " by " + columns[2] );
				historic.notifyDataSetChanged();
				listView2.requestFocus();
			}

		} catch (Exception e) {
		//	txtMsg.append("\nError useCursor1: " + e.getMessage());
			finish();
		}
	}// useCursor1
	/*******************************************************************************/
	private void insertSomeDbData() {
		// create table: tblAmigo
		db.beginTransaction();
		try {
			// create table
			db.execSQL("create table tableDB ("
					+ " ID integer PRIMARY KEY autoincrement, "
					+ " song  text, " + " artist text , " + " songdata text );  ");
			// commit your changes
			db.setTransactionSuccessful();



		} catch (Exception e1) {
			finish();
		} finally {
			db.endTransaction();
		}
		// populate table: tblAmigo
		db.beginTransaction();
		try {
			for(int i=0; i <5; i++) {
				db.execSQL("insert into tableDB(song, artist) "
						+ " values ('"+ "song" + Integer.toString(i)  + "', '" +"artist" + Integer.toString(i)+"' );");
			}

			// commit your changes
			db.setTransactionSuccessful();

		} catch (SQLiteException e2) {

		} finally {
			db.endTransaction();
		}
	}// insertSomeData

	private void showTable(String tableName) {
		try {
			String sql = "select * from " + tableName ;
			Cursor c = db.rawQuery(sql, null);
			//Cursor c = dbs.rawQuery(sql, null);
		//	txtMsg.append("\n-showTable: " + tableName + showCursor(c));

			/**********/
			//historic.add("Me: " + message);
		} catch (Exception e) {
		//	txtMsg.append("\nError showTable: " + e.getMessage());

		}
	}// useCursor1

	private void openDatabase() {
		try {
			// path to private memory:data/data/cis470.matos.databases/myfriendsDB2.db
			//myDbPath = "data/data/cis470.matos.databases/";
			//mySdPath = Environment.getExternalStorageDirectory().getPath();
			String myDbPath = mySdPath  + "/myDB1.db";

			db = SQLiteDatabase.openDatabase(myDbPath, null,
					SQLiteDatabase.CREATE_IF_NECESSARY);

		} catch (SQLiteException e) {
			txtMsg.append("\nError openDatabase: " + e.getMessage());
			finish();
		}
	}// createDatabase

	private void dropTable() {
		try {
			db.execSQL("DROP TABLE IF EXISTS tableDB;");

		} catch (Exception e) {

			finish();
		}
	}

	public void useAddMethod(String fn,String ln ) {
		//SQLiteDatabase db;

		String mySdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		String myDbPath = mySdPath  + "/myDB1.db";

		db = SQLiteDatabase.openDatabase(myDbPath, null,
				SQLiteDatabase.CREATE_IF_NECESSARY);

		db.beginTransaction();
		try {
			db.execSQL("insert into tableDB(song, artist) "
					+ " values ('"+ fn + "', '"+ln+"' );");
			// commit your changes
			db.setTransactionSuccessful();

		} catch (SQLiteException e2) {

		} finally {
			db.endTransaction();
		}
	}// insertSomeData



	private void useUpdateMethod() {
		try {
			// using the 'update' method to change name of selected friend
			String[] whereArgs = { "2" };

			ContentValues updValues = new ContentValues();
			updValues.put("name", "UPDATE");

			int recAffected = db.update("tableDB", updValues,
					"recID = ? ", whereArgs);

			showTable("tblAmigo");

		} catch (Exception e) {

		}
	}

	@Override
	public void onClick(View v) {
		// close current screen - terminate Activity1
		finish();
	}



}
