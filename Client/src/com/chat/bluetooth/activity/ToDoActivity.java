package com.chat.bluetooth.activity;




//TODO comment out these lines
//import java.util.ArrayList;
//import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.chat.bluetooth.R;
import com.chat.bluetooth.ignore.Ignore;
import com.chat.bluetooth.test.usersAdapter;
import com.chat.bluetooth.test.users;
import com.chat.bluetooth.util.ToastUtil;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;

public class ToDoActivity extends Activity {

//	TODO Uncomment these lines to create references to the mobile service client and table
	private MobileServiceClient mClient;
	//private MobileServiceTable<ToDoItem> mToDoTable;

	private MobileServiceTable<users> userTable;


//  TODO Comment out this line to remove the in-memory store
//public List<ToDoItem> toDoItemList = new ArrayList<ToDoItem>();

private usersAdapter usersAdapter;
private EditText mTextNewToDo;
private ProgressBar mProgressBar;
private Button refresh;
private ToastUtil toastUtil;

/**
 * Initializes the activity
 */
@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_to_do);

	mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

	// Initialize the progress bar
	mProgressBar.setVisibility(ProgressBar.GONE);
	toastUtil = new ToastUtil(this);
//		TODO Uncomment the the following code to create the mobile services client
		try {
			// Create the Mobile Service Client instance, using the provided
			// Mobile Service URL and key
			Ignore ignore = new Ignore();
			mClient = new MobileServiceClient(
					ignore.getUrl(),
					ignore.getAppKey(),
					this).withFilter(new ProgressFilter());

			//mClient.getTable("users");

//	 Get the Mobile Service Table instance to use
			userTable = mClient.getTable(users.class); /*******************************************************************************/
		} catch (MalformedURLException e) {
			createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
			toastUtil.showToast("error url");
		}

	mTextNewToDo = (EditText) findViewById(R.id.textNewToDo);

	// Create an adapter to bind the items with the view
	usersAdapter = new usersAdapter(this, R.layout.row_list_to_do);
	ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
	listViewToDo.setAdapter(usersAdapter);

	// Load the items from the Mobile Service
	refreshItemsFromTable();
	refresh = (Button)findViewById(R.id.refresh);
	refresh.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
// refreshItemsFromTable();
			toastUtil.showToast("test");
		}
	});
}

/**
 * Initializes the activity menu
 */
@Override
public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
}

/**
 * Select an option from the menu
 */
@Override
public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.menu_refresh) {
		refreshItemsFromTable();
	}

	return true;
}

/**
 * Mark an item as completed
 *
 * @param item
 *            The item to mark
 */
public void checkItem(final users item) {

	// Set the item as completed and update it in the table
	item.setComplete(true);

//		TODO Uncomment the the following code when using a mobile service
	    new AsyncTask<Void, Void, Void>() {

	        @Override
	        protected Void doInBackground(Void... params) {
	            try {
	                userTable.update(item).get();
	                runOnUiThread(new Runnable() {
	                    public void run() {
	                        if (item.isComplete()) {
	                            usersAdapter.remove(item);
	                        }
	                        refreshItemsFromTable();
	                    }
	                });
	            } catch (Exception exception) {
	                createAndShowDialog(exception, "Error");
	            }
	            return null;
	        }
	    }.execute();

//		TODO Comment out these lines to remove the in-memory store
	//toDoItemList.add(item);
//	if (item.isComplete()) {
//		usersAdapter.remove(item);
//	}
//		End of lines to comment out

}

/**
 * Add a new item
 *
 * @param view
 *            The view that originated the call
 */
public void addItem(View view) {

	// Create a new item
	final users item = new users();

	item.setText(mTextNewToDo.getText().toString());
	item.setComplete(false);

//		TODO Uncomment the the following code when using a mobile service
		// Insert the new item
		new AsyncTask<Void, Void, Void>() {

	        @Override
	        protected Void doInBackground(Void... params) {
	            try {
	                userTable.insert(item).get();
	                if (!item.isComplete()) {
	                    runOnUiThread(new Runnable() {
	                        public void run() {
	                            usersAdapter.add(item);
	                        }
	                    });
	                }
	            } catch (Exception exception) {
	                createAndShowDialog(exception, "Error");
	            }
	            return null;
	        }
	    }.execute();

//	    TODO Comment out these lines to remove the in-memory store
	//toDoItemList.add(item);
	//usersAdapter.add(item);
//		End of lines to comment out

	//mTextNewToDo.setText("");
}

/**
 * Refresh the list with the items in the Mobile Service Table
 */
private void refreshItemsFromTable() {

//		TODO Uncomment the the following code when using a mobile service
		// Get the items that weren't marked as completed and add them in the adapter
	    new AsyncTask<Void, Void, Void>() {

	        @Override
	        protected Void doInBackground(Void... params) {
	            try {
	                final MobileServiceList<users> result = userTable.where().field("complete").eq(false).execute().get();
	                runOnUiThread(new Runnable() {

	                    @Override
	                    public void run() {
	                        usersAdapter.clear();

	                        for (users item : result) {
	                            usersAdapter.add(item);
	                        }
	                    }
	                });
	            } catch (Exception exception) {
	                createAndShowDialog(exception, "Error");
	            }
	            return null;
	        }
	    }.execute();

//		TODO Comment out these lines to remove the in-memory store
//	usersAdapter.clear();
//	for (ToDoItem item : toDoItemList)
//	{
//		if (item.isComplete() == false)
//			usersAdapter.add(item);
//	}
//		End of lines to comment out



}

/**
 * Creates a dialog and shows it
 *
 * @param exception
 *            The exception to show in the dialog
 * @param title
 *            The dialog title
 */
private void createAndShowDialog(Exception exception, String title) {
	createAndShowDialog(exception.toString(), title);
}

/**
 * Creates a dialog and shows it
 *
 * @param message
 *            The dialog message
 * @param title
 *            The dialog title
 */
private void createAndShowDialog(String message, String title) {
	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	builder.setMessage(message);
	builder.setTitle(title);
	builder.create().show();
}

private class ProgressFilter implements ServiceFilter {

	@Override
	public ListenableFuture<ServiceFilterResponse> handleRequest(
		ServiceFilterRequest request, NextServiceFilterCallback next) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
			}
		});

		SettableFuture<ServiceFilterResponse> result = SettableFuture.create();
		try {
			ServiceFilterResponse response = next.onNext(request).get();
			result.set(response);
		} catch (Exception exc) {
			result.setException(exc);
		}

		dismissProgressBar();
		return result;
	}

	private void dismissProgressBar() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
			}
		});
	}
	}
//	TODO Uncomment the the following code when using a mobile service
}


/**refresh = (Button)findViewById(R.id.refresh);
 refresh.setOnClickListener(new View.OnClickListener() {

@Override
public void onClick(View v) {
// refreshItemsFromTable();
toastUtil.showToast("test");
}
});*/