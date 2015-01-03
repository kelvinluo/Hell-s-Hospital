package com.example.nurseapp;

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
/***
 * Adds a new Patient to the list of all Patients in the hospital. 
 */
public class PatientAddition extends Activity {

	//initialize all instances for storing patient's personal information
	private String birthYear = "";
	private String birthMonth = "";
	private String birthDay = "";
	private String name = "";
	
	/***
	 * Construct a user interface activity for asking for new patient's information.
	 */
	@Override
	//set up the user interface for addPatience.
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_patient);
		setupUI(findViewById(R.id.container));	///set the screen

		//read the user inputs and store them in respective instances
		final EditText birthdate_year = (EditText)findViewById(R.id.patient_year);
		final EditText birthdate_month = (EditText)findViewById(R.id.patient_month);
		final EditText birthdate_day = (EditText)findViewById(R.id.patient_day);
		final EditText name_input = (EditText)findViewById(R.id.get_patient_name);
		final EditText hc_input = (EditText)findViewById(R.id.getPatientHCNum);
		
		Button updateButton = (Button) findViewById(R.id.descriptionUpdate);	///set up the button  for starting up the looking for the description
		updateButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    //set up what happen when click the button
		    public void onClick(View v) {
		    	if (checkValidInput(birthdate_year, birthdate_month, birthdate_day, name_input, hc_input)){
		    		String birthDate = birthdate_year.getText().toString() + "-" + birthdate_month.getText().toString() + "-" + birthdate_day.getText().toString();///collect the user input and treat it as health card number
					String name = name_input.getText().toString();///collect the user input and treat it as health card number
			    	String hcNum = hc_input.getText().toString();
					Intent returnIntent = new Intent();
			    	//return the data collected to back end of the app
					returnIntent.putExtra("Birth Date", birthDate);
					returnIntent.putExtra("Name", name);
					returnIntent.putExtra("HealthCardNum", hcNum);
					setResult(RESULT_OK,returnIntent);
					finish();	///activity finish
		    	}
		    }
		});
	}
	
	/**
	 * Get the new patient's birthdate_year, birthdate_month, birthdate_day,
	 * name_input, hc_input, return true, iff all of the inputs are correct.
	 * @param birthdate_year
	 * 			- birth year of the patient
	 * @param birthdate_month
	 * 			- birth month of the patient
	 * @param birthdate_day
	 * 			- birth day of the patient
	 * @param name_input
	 * 			- name of the patient
	 * @param hc_input
	 * 			- the health card number of patient
	 * @return
	 * 			- true iff all those inputs are correct
	 */
	private boolean checkValidInput(EditText birthdate_year, EditText birthdate_month, EditText birthdate_day, EditText name_input, EditText hc_input){
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, "", duration);//create a toast
		int year, month, day;
		try{
			year = Integer.parseInt(birthdate_year.getText().toString());
			month = Integer.parseInt(birthdate_month.getText().toString());
			day = Integer.parseInt(birthdate_day.getText().toString());
		}catch(NumberFormatException nfe){
    		toast = Toast.makeText(context, "Birth Date Invalid.", duration);//set the toast information
			toast.show();//pop up the toast.
			return false;
		}
		
		if (birthdate_year.getText().toString().length() != 4){
    		toast = Toast.makeText(context, "Birth Date Invalid.", duration);//set the toast information
			toast.show();//pop up the toast.
			return false;
		}
		else if (birthdate_month.getText().toString().length() != 2 || month > 12 || month == 0){
    		toast = Toast.makeText(context, "Birth Date Invalid.", duration);//set the toast information
			toast.show();//pop up the toast.
			return false;
		}
		else if (birthdate_day.getText().toString().length() != 2 || day > 31 || day == 0){
    		toast = Toast.makeText(context, "Birth Date Invalid.", duration);//set the toast information
			toast.show();//pop up the toast.
			return false;
		}
		else if (name_input.getText().toString().equals("") || name_input.getText().toString().contains(",")){
    		toast = Toast.makeText(context, "Name Invalid.", duration);//set the toast information
			toast.show();//pop up the toast.
			return false;
		}
		else if (hc_input.getText().toString().equals("") || hc_input.getText().toString().contains(",") || hc_input.getText().toString().length() != 6){
    		toast = Toast.makeText(context, "Health Card Number Invalid.", duration);//set the toast information
			toast.show();//pop up the toast.
			return false;
		}
		return true;
	}
	
	/**
	 * Construct a hideable SoftKeyboard on the user interface.
	 * @param activity
	 * 		-the activity for get patient description
	 */
	private static void hideSoftKeyboard(Activity activity) {
		///set up the keyboard
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	/**
	 *  Set up the User Interface
	 * @param view
	 * 		- the view that contains the user view.
	 */
	private void setupUI(View view) {

	    //Set up touch listener for non-text box views to hide keyboard.
	    if(!(view instanceof EditText)) {
	    	//Set up touch listener for non-text box views to hide keyboard.
	        view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					hideSoftKeyboard(PatientAddition.this);
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
