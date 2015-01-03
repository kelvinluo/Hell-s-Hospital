package com.example.nurseapp;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

/**
 * An GetPatientStatus is a activity focused on what the user can do on the screen
 * that user can read the patient's status.
 * @author Administrator
 *
 */
public class PatientStatusInput extends Activity {
	//create instances for status
	protected String temperature = "";
	protected String bpSystolic = "";
	protected String bpDiastolic = "";
	protected String heartRate = "";
	
	/***
	 * Construct a user interface activity for displaying the patient's status.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	//inherit from activity.
		//set up the view
		setContentView(R.layout.activity_get_patient_status);

		//set up the user interface
		setupUI(findViewById(R.id.logincontainer));
		///collecting all the key information of status
		final EditText temperature_input = (EditText)findViewById(R.id.getTemp);
		final EditText bpSys = (EditText)findViewById(R.id.getBPSYS);
		final EditText bpDia = (EditText)findViewById(R.id.getBPDIA);
		final EditText heart_rate = (EditText)findViewById(R.id.getHR);
		
		Button updateButton = (Button) findViewById(R.id.descriptionUpdate);
		updateButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	///save the user input into the respective variables
				temperature = temperature_input.getText().toString();
				bpSystolic = bpSys.getText().toString();
				bpDiastolic = bpDia.getText().toString();
				heartRate = heart_rate.getText().toString();
				///if the input is empty, then ask user to input again
				if (!temperature.equals("") && !bpSystolic.equals("") 
						&& !bpDiastolic.equals("") && !heartRate.equals("")){
					Intent returnIntent = new Intent();
					//return all the key data to the back end
					returnIntent.putExtra("Temperature", temperature);
					returnIntent.putExtra("Systolic", bpSystolic);
					returnIntent.putExtra("Diastolic", bpDiastolic);
					returnIntent.putExtra("Heart Rate", heartRate);
					setResult(RESULT_OK,returnIntent);
					finish();///the activity is finish
		    	}
				else{
					///if input is invalid, tell the user "Information is imcomplete"
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_SHORT;
	            	Toast toast = Toast.makeText(context, "Information is imcomplete", duration);//set up the toast
	            	toast.show();///show up the information on the screen
				}
		    }
		});
	}
	
	/**
	 * Construct a hideable SoftKeyboard on the user interface.
	 * @param activity
	 * 		-the activity for get patient description
	 */
	public static void hideSoftKeyboard(Activity activity) {
		///set up the keyboard
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	/**
	 *  Set up the User Interface
	 * @param view
	 * 		- the view that contains the user view.
	 */
	public void setupUI(View view) {

	    //Set up touch listener for non-text box views to hide keyboard.
	    if(!(view instanceof EditText)) {
	    	//Set up touch listener for non-text box views to hide keyboard.
	        view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					hideSoftKeyboard(PatientStatusInput.this);
				}

	        });
	    }

	    //If a layout container, iterate over children and seed recursion.
	    if (view instanceof ViewGroup) {
	    	// a special method to cancel what the user has inputed 
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	        	// go back the initial view of that activity.
	            View innerView = ((ViewGroup) view).getChildAt(i);

	            setupUI(innerView);
	        }
	    }
	}
}
