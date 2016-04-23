package com.chat.bluetooth.activity;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.chat.bluetooth.ignore.Ignore;
import com.chat.bluetooth.test.users;
import com.chat.bluetooth.test.usersAdapter;
import com.chat.bluetooth.R;
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

/**
 * Gets a list of GPS coordinates from azure mobile services.
 */
public class AzureMobileServicesActivity extends Activity {

	//create references to the mobile service client and table
	private MobileServiceClient mClient;
	private MobileServiceTable<users> gpsTable;

	private com.chat.bluetooth.test.usersAdapter gpsAdaptor;
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

			try {
				// Create the Mobile Service Client instance, using the provided
				// Mobile Service URL and key
				Ignore ignore = new Ignore();
				mClient = new MobileServiceClient(
					ignore.getUrl(),
					ignore.getAppKey(),
					this).withFilter(new ProgressFilter());

				//	 Get the Mobile Service Table instance to use
				gpsTable = mClient.getTable(users.class); /*******************************************************************************/
			} catch (MalformedURLException e) {
				createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
				toastUtil.showToast("error url");
			}

		// Create an adapter to bind the items with the view
		gpsAdaptor = new usersAdapter(this, R.layout.row_list_to_do);
		ListView gpsList = (ListView) findViewById(R.id.listViewToDo);
		gpsList.setAdapter(gpsAdaptor);

		// Load the items from the Mobile Service
		//refreshItemsFromTable();
		refresh = (Button)findViewById(R.id.refresh);
		refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				 refreshItemsFromTable();
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
	 * @param users
	 *            The item to mark
	 */
	public void checkItem(final users users) {

	// Set the item as completed and update it in the table
	users.setComplete(true);

	    new AsyncTask<Void, Void, Void>() {

	        @Override
	        protected Void doInBackground(Void... params) {
	            try {
	                gpsTable.update(users).get();
	                runOnUiThread(new Runnable() {
	                    public void run() {
	                        if (users.isComplete()) {
	                            gpsAdaptor.remove(users);
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
	}

	/**
	 * Refresh the list with the items in the Mobile Service Table
	 */
	private void refreshItemsFromTable() {

	    new AsyncTask<Void, Void, Void>() {

	        @Override
	        protected Void doInBackground(Void... params) {
	            try {

					final MobileServiceList<users> result = gpsTable.select("text").execute().get();

	                runOnUiThread(new Runnable() {
	                    @Override
	                    public void run() {
	                        gpsAdaptor.clear();

	                        for (users gps : result) {
	                            gpsAdaptor.add(gps);
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
	                    }
	                });
	            } catch (Exception exception) {
	                createAndShowDialog(exception, "Error");
	            }
	            return null;
	        }
	    }.execute();
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

		private void dismissProgressBar()
		{
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
				}
			});
		}
	}//end inner class progressFilter
}
