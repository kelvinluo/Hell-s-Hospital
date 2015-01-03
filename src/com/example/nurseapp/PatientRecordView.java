package com.example.nurseapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * An PatientRecordView is a activity focused on what the user can do on the screen
 * that user views the patient record.
 * @author Administrator
 *
 */
public class PatientRecordView extends Activity {
	//create instances for storing the information in the record
	ArrayList<String> latest_record = new ArrayList<String>();
	ArrayList<String> all_record = new ArrayList<String>();
	
	/***
	 * Construct a user interface activity for displaying the patient's record.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//inherit from activity.
		//set up the view
		setContentView(R.layout.activity_patient_record_view);

		//create a new bundle to get user's input
		Bundle b = getIntent().getExtras();
		//use the built in method in bundle to allow these two record to get data
		latest_record = b.getStringArrayList("Latest Record");
		all_record = b.getStringArrayList("All Records");
		
		//set up the text input box's view
		TextView nameView = (TextView) findViewById(R.id.display_name);
		TextView healthCardView = (TextView) findViewById(R.id.display_hc);
		TextView birthDateView = (TextView) findViewById(R.id.display_birth_date);
		nameView.setText(b.getString("Name"));
		healthCardView.setText(b.getString("Health Card Number"));
		birthDateView.setText(b.getString("Birth Date"));
		
		///set up a button for get patient record
		Button latestRecordButton = (Button) findViewById(R.id.view_latest_record);
		latestRecordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {//set up what happen when user click button
				Intent intent = new Intent(PatientRecordView.this, DisplayList.class);
				Bundle b = new Bundle();//create bundle for allowing app to get user input
				//give the user input to the variable.
				String[] list = latest_record.toArray(new String[latest_record.size()]);;
				b.putStringArray("patients", list);
				intent.putExtras(b);
				startActivity(intent);
			}
		});
		
		//set up the button for reading all record
		Button allRecordButton = (Button) findViewById(R.id.view_all_records);
		allRecordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {//set up what happen when click the button
				Intent intent = new Intent(PatientRecordView.this, DisplayList.class);
				Bundle b = new Bundle();
				//give the user input the specific variable
				String[] list = all_record.toArray(new String[all_record.size()]);;
				b.putStringArray("patients", list);
				intent.putExtras(b);
				startActivity(intent);
			}
		});
		
		//set up the button for exiting the screen.
		Button exitButton = (Button) findViewById(R.id.exit_button);
		exitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();//click then the activity ends.
			}
		});
	}
}
