package com.chat.bluetooth.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.chat.bluetooth.R;
import com.chat.bluetooth.util.MySQLiteHelper;

import java.sql.SQLException;

public class Activity2 extends Activity implements OnClickListener{
	EditText txtMsg;
	Button  btnDone;
	private Button btnReadSDFile;
	private Button btnUpdate;
	private Button btnDelete;
	private Button btnAdd;
	private  String mySdPath;
	private EditText f1;
	private EditText f2;
	private ListView listView2;
	private ArrayAdapter<String> historic;
	private Bundle myBundle;
	private Intent myLocalIntent;
	SQLiteDatabase db;
	MySQLiteHelper dbs;
	//private String myDbPath1 = "data/data/cis470.matos.databases/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
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

				String test = historic.getItem(position);
				//prestationEco str=(prestationEco)o;//As you are using Default String Adapter

				Toast.makeText(getBaseContext(), historic.getItem(position), Toast.LENGTH_SHORT).show();

				Intent myLocalIntent = getIntent();
				Bundle myBundle = new Bundle();
				myBundle =  myLocalIntent.getExtras();
				myBundle.putString("result", historic.getItem(position));
				// attach updated bumble to invoking intent
				myLocalIntent.putExtras(myBundle);
				setResult(Activity.RESULT_OK, myLocalIntent);
				finish();
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
				String tableName = f1.getText().toString();
				String tableName2 = f2.getText().toString();
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
		//openDatabase();
		setResult(Activity.RESULT_OK, myLocalIntent);
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



	private String showCursor( Cursor cursor) {
		// show SCHEMA (column names & types)
		cursor.moveToPosition(-1); //reset cursor's top
		String cursorData = "\nCursor: [";

		try {
			// get column names
			String[] colName = cursor.getColumnNames();
			for(int i=0; i<colName.length; i++){
				String dataType = getColumnType(cursor, i);
				cursorData += colName[i] + dataType;

				if (i<colName.length-1){
					cursorData+= ", ";
				}
			}
		} catch (Exception e) {
			Log.e("<<SCHEMA>>", e.getMessage());
		}
		cursorData += "]";

		// now get the rows
		cursor.moveToPosition(-1); //reset cursor's top
		while (cursor.moveToNext()) {
			String cursorRow = "\n[";
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				cursorRow += cursor.getString(i);
				if (i<cursor.getColumnCount()-1)
					cursorRow +=  ", ";
			}
			cursorData += cursorRow + "]";
		}
		return cursorData + "\n";
	}

	private String getColumnType(Cursor cursor, int i) {
		try {
			//peek at a row holding valid data
			cursor.moveToFirst();
			int result = cursor.getType(i);
			String[] types = {":NULL", ":INT", ":FLOAT", ":STR", ":BLOB", ":UNK" };
			//backtrack - reset cursor's top
			cursor.moveToPosition(-1);
			return types[result];
		} catch (Exception e) {
			return " ";
		}
	}




	@Override
	public void onClick(View v) {
		// close current screen - terminate Activity1
		finish();
	}

}
