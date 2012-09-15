package org.chaotisch.hqapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	//public int anaus;
//	public String varphrase;
	//Button button;
	Session session;
	Channel channel;
	private ListView mainListView;
	private ArrayAdapter<String> listAdapter;
	private JSch sshobj;

	final static String PRIVKEYFILE = "private.key";
	final static String PUBKEYFILE = "public.key";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.sshobj = new JSch();
		// Find the ListView resource.
		mainListView = (ListView) findViewById(R.id.mainListView);

		// Create and populate a List of planet names.
		String[] planets = new String[] { "Summer", "Tür", "Alarm",
				"buntes Licht", "Ufo Lichtstrahler", "Beamer", "Sound",
				"großes Licht" };
		ArrayList<String> planetList = new ArrayList<String>();
		planetList.addAll(Arrays.asList(planets));

		// Create ArrayAdapter using the planet list.
		listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow,
				planetList);

		// Add more planets. If you passed a String[] instead of a List<String>
		// into the ArrayAdapter constructor, you must not add more items.
		// Otherwise an exception will occur.
		// listAdapter.add("Ceres");

		// Set the ArrayAdapter as the ListView's adapter.
		mainListView.setAdapter(listAdapter);

		mainListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (listAdapter.getItem(position) == "Alarm") {
							final Runnable r = new Runnable() {
								public void run() {
									onSSH("31", "strom");
									Log.i(this.getClass().getName(),
											"Rundumleuchte wurde ausgelöst");
								}
							};
							performOnBackgroundThread(r);

						} else if (listAdapter.getItem(position) == "Summer") {
							final Runnable r = new Runnable() {
								public void run() {
									onSSH("", "sumsum");
									Log.i(this.getClass().getName(),
											"Summer wurde ausgelöst");
								}
							};
							performOnBackgroundThread(r);
						} else {
							onListItemClick(mainListView, view, position, id);
						}

						// Toast.makeText(getApplicationContext(),
						// "Click ListItem Number " + position,
						// Toast.LENGTH_LONG).show();

						// mainListView.getSelectedItemPosition();
						// Log.i(this.getClass().getName(), "Clicked");
					}

				});
		registerForContextMenu(mainListView);
		checkkeyexists();
		
		
		
		//try to read file without passphrase
				File file = getFileStreamPath(PRIVKEYFILE);

				KeyPair kpair;
				
				try {
					kpair = KeyPair.load(sshobj, file.getAbsolutePath());
				

					Log.i(this.getClass().getName(),"key has "+(kpair.isEncrypted()?"been ":"not been ")+"encrypted");
					if(!kpair.isEncrypted())
					{
						sshobj.addIdentity(file.getAbsolutePath(), "");
					}
					else
					{
						askPassphrase();
					}
					
			      /*String passphrase="";
			      while(kpair.isEncrypted()){
			    	  
			    	  askPassphrase();
			    	  //PASSPHRASE =
			    		if(!kpair.decrypt(passphrase)){
			    			Log.i(this.getClass().getName(),"failed to decrypt key");
			    		}
			    		else{
			    			Log.i(this.getClass().getName(),"key is decrypted.");
			    		} 
			    		return;
			      }*/
				} catch (JSchException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
	}

	public boolean askPassphrase() {
		Button OkButton;
		final Dialog dialogPass = new Dialog(this);
		dialogPass.setContentView(R.layout.dialog);
		dialogPass.setTitle("Enter Passphrase");
		OkButton = (Button) dialogPass.findViewById(R.id.dialogButtonOK);
		OkButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String StrPassphrase;
				File file = getFileStreamPath(PRIVKEYFILE);
				final EditText fldpassphrase = (EditText) dialogPass
						.findViewById(R.id.editText1);
				StrPassphrase = fldpassphrase.getText().toString();
				try {
					sshobj.addIdentity(file.getAbsolutePath(), StrPassphrase);
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					Log.i(this.getClass().getName(),
							"Key konnte nicht geladen worde");
				}
				dialogPass.dismiss();
			}
		});
		dialogPass.show();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mymenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.send_key:
			sharekey();
			return true;
		case R.id.about:
			Intent myIntent0 = new Intent(getApplicationContext(), About.class);
			startActivityForResult(myIntent0, 0);
			return true;
		case R.id.settings:
			Intent myIntent1 = new Intent(getApplicationContext(),
					Settings.class);
			startActivityForResult(myIntent1, 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int schalter = 0;
		String strschalter;
		final String schaltcom;
		AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		long deineId = menuInfo.id;
		int intId = (int) deineId;
		Log.i(this.getClass().getName(), String.valueOf(deineId));
		listAdapter.getItem(intId);

		if (listAdapter.getItem(intId) == "Tür") {
			switch (item.getItemId()) {
			case R.id.on:
				final Runnable r = new Runnable() {
					public void run() {
						Log.i(this.getClass().getName(), "Tür geöffnet");
						onSSH("", "open");
					}
				};
				performOnBackgroundThread(r);
				// r.run();
				return true;
			case R.id.off:
				final Runnable r1 = new Runnable() {
					public void run() {
						Log.i(this.getClass().getName(), "Tür geschlossen ");
						onSSH("", "close");
					}
				};
				performOnBackgroundThread(r1);
				return true;
			}
		} else {

			if (listAdapter.getItem(intId) == "buntes Licht") {
				schalter = 10;
			} else if (listAdapter.getItem(intId) == "Ufo Lichtstrahler") {
				schalter = 20;
			} else if (listAdapter.getItem(intId) == "Beamer") {
				schalter = 40;
			} else if (listAdapter.getItem(intId) == "Sound") {
				schalter = 50;
			} else if (listAdapter.getItem(intId) == "großes Licht") {
				schalter = 60;
			}
			switch (item.getItemId()) {
			case R.id.on:
				schalter++;
				break;
			case R.id.off:
				schalter = schalter + 2;
				break;
			}

			strschalter = String.valueOf(schalter);
			if (listAdapter.getItem(intId) == "Beamer") {
				strschalter = strschalter + "y";
			}

			schaltcom = strschalter;
			final Runnable r = new Runnable() {
				public void run() {
					Log.i(this.getClass().getName(), "fertig SSHed "
							+ schaltcom);
					onSSH(schaltcom, "strom");
				}
			};
			performOnBackgroundThread(r);
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		// menu.setHeaderTitle(String.valueOf(info.position));
		menu.setHeaderTitle(listAdapter.getItem(info.position));
		MenuInflater inflater = getMenuInflater();
		if (listAdapter.getItem(info.position) == "Tür") {
			inflater.inflate(R.menu.menu_openclose, menu);
		} else {
			inflater.inflate(R.menu.menu_onoff, menu);
		}
	}

	public boolean onListItemClick(ListView l, View v, int position, long id) {
		// registerForContextMenu(mainListView);
		l.showContextMenuForChild(v);
		// unregisterForContextMenu(mainListView);
		return true;
	}

	public void checkkeyexists() {
		File file = getFileStreamPath(PRIVKEYFILE);
		if (!file.exists()) {
			Log.i(this.getClass().getName(), "No keyfile found");
			Intent myIntent0 = new Intent(getApplicationContext(),
					Settings.class);
			startActivityForResult(myIntent0, 0);
		}
	}

	public void onSSH(String myaction, String username) {
		// String username = "strom";
		// String password = "testpassword";
		String host = "192.168.2.10"; // sample ip address 192.168.2.10
		try {

			session = this.sshobj.getSession(username, host, 22);
			// session.setPassword(password);
			Properties properties = new Properties();
			properties.put("StrictHostKeyChecking", "no");
			session.setConfig(properties);
			session.connect(30000);
			channel = session.openChannel("shell");
			channel.setOutputStream(System.out);
			PrintStream shellStream = new PrintStream(channel.getOutputStream());
			channel.connect();

			shellStream.println(myaction);
			shellStream.flush();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			channel.disconnect();
			session.disconnect();
		} catch (JSchException e) {
			final String mye = e.getMessage();
			// TODO Auto-generated catch block
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext(), mye,
							Toast.LENGTH_LONG).show();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sharekey() {
		// ##### Read the file back in #####

		/*
		 * We have to use the openFileInput()-method the ActivityContext
		 * provides. Again for security reasons with openFileInput(...)
		 */
		try {
			FileInputStream fIn = openFileInput(PUBKEYFILE);
			BufferedReader fInBuffer = new BufferedReader(
					new InputStreamReader(fIn));

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(android.content.Intent.EXTRA_TEXT,
					fInBuffer.readLine());
			startActivity(Intent.createChooser(intent,
					"Select an action for sharing"));
			fInBuffer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			public void run() {
				runnable.run();
			}
		};
		t.start();
		return t;

	}
}