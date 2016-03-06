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
import com.chat.bluetooth.util.ToastUtil;
import com.google.gson.Gson;
import net.sf.json.util.JSONUtils;
import org.json.JSONArray;

import java.io.IOException;
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
	//private Gson gson;
	public com.chat.bluetooth.util.JSONUtils util;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		myBundle = savedInstanceState;
		toastUtil = new ToastUtil(this);
		init();
		myBundle =  myLocalIntent.getExtras();
		util = new com.chat.bluetooth.util.JSONUtils();
		//gson = new Gson();
		/*****************************************************************************/
		final String msgFromHost = new String (myBundle.getByteArray("package"));


//		if(util.isJSONValid(msgFromHost))
//			toastUtil.showToast("yep");
//		else
//			toastUtil.showToast("nope");

			new parseJSON().execute(msgFromHost);
//		else
//			toastUtil.showToast("nope");
	}

//	public boolean isJSONValid(String JSON_STRING) {
//		try {
//			gson.fromJson(JSON_STRING, Object.class);
//			return true;
//		} catch(com.google.gson.JsonSyntaxException ex) {
//			return false;
//		}
//	}

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

	/*********************************************************************************************/
	/*********************************************************************************************/
	/*********************************************************************************************/
	// database methods

	private void useCursor1() {
		try {
			String[] columns = { "ID", "song", "artist", "songdata" };
			Cursor c = db.query("tableDB", columns, null, null, null, null,
					"ID");

			int idCol = c.getColumnIndex("ID");
			int nameCol = c.getColumnIndex("song");
			int phoneCol = c.getColumnIndex("artist");

			c.moveToPosition(-1);
			while (c.moveToNext()) {
				columns[0] = Integer.toString((c.getInt(idCol)));
				columns[1] = c.getString(nameCol);
				columns[2] = c.getString(phoneCol);

				historic.add("ID:" + columns[0] + " - " + columns[1] + " by " + columns[2] );
				historic.notifyDataSetChanged();
				listView2.requestFocus();
			}
		} catch (Exception e) {
			finish();
		}
	}// useCursor1
	/*******************************************************************************/
	private void insertSomeDbData() {
		db.beginTransaction();
		try {
			// create table
			db.execSQL("create table tableDB ("
					+ " ID integer PRIMARY KEY autoincrement, "
					+ " song  text, " + " artist text , " + " songdata text );  ");
		} catch (Exception e1) {
			finish();
		} finally {
			db.endTransaction();
		}
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
		} catch (Exception e) {
		}
	}// useCursor1

	private void openDatabase() {
		try {
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

	public void init(){
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

		btnReadSDFile = (Button) findViewById(R.id.buttonsd);
		btnReadSDFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				useCursor1();
			}// onClick
		});

		btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				useUpdateMethod();
			}// onClick
		});

		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}// onClick
		});

		btnAdd.setOnClickListener(new OnClickListener() {/***************************************************/
		@Override
		public void onClick(View v) {
			useAddMethod("song1", "song2");
		}// onClick
		});
	}//init
}
