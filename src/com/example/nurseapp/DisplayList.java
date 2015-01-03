package com.example.nurseapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

/**
 * An displaylist is a activity focused on what the user can do on the screen
 * that displaying the list of patient.
 * @author Administrator
 *
 */
public class DisplayList extends Activity {

	
	@Override
	/***
	 * Construct a user interface activity for displaying the patient list. 
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_list);	///set up the screen

		Bundle b = getIntent().getExtras();
		String[] value = b.getStringArray("patients");
		TextView patientsList = new TextView(this); 
		patientsList = (TextView)findViewById(R.id.all_patients);	///read all the patient's record
		String display_text = "";
		patientsList.setMovementMethod(new ScrollingMovementMethod());
		for (int i = 0; i < value.length; i++){	///print every single record one by one on the screen
			display_text += value[i] + "\n";
		}
		patientsList.setText(display_text);
		
		// set up the button on the screen
		Button button= (Button) findViewById(R.id.exit_display);
		button.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {	///set up what happen when clicking the button
		    	finish();
		    }
		});
	}
}
