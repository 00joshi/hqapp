package org.chaotisch.hqapp;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
import android.content.Intent;

public class Settings extends Activity {
	private ListView SettingsListView;
	private ArrayAdapter<String> listAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
		// Find the ListView resource.
		SettingsListView = (ListView) findViewById(R.id.SettingsListView);

		// Create and populate a List of planet names.
		String[] planets = new String[] { "Schlüssel erzeugen", "Schlüssel importieren"};
		ArrayList<String> planetList = new ArrayList<String>();
		planetList.addAll(Arrays.asList(planets));

		// Create ArrayAdapter using the planet list.
		listAdapter = new ArrayAdapter<String>(this, R.layout.settingsrow,
				planetList);
		SettingsListView.setAdapter(listAdapter);
		
		SettingsListView
		.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listAdapter.getItem(position) == "Schlüssel erzeugen") {
					Intent myIntent0 = new Intent(getApplicationContext(), CreateKey.class);
					startActivityForResult(myIntent0, 0);
				} else if (listAdapter.getItem(position) == "Schlüssel importieren") {
					Intent myIntent1 = new Intent(getApplicationContext(), ImportKey.class);
					startActivityForResult(myIntent1, 0);					
				} 
			}

		});
    }

}
