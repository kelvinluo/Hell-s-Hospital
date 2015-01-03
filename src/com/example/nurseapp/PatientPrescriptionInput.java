package com.example.nurseapp;

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
 * An GetPatientPrescription is a activity focused on what the user can do on the screen
 * that user can read the patient's prescription.
 * @author Administrator
 *
 */
public class PatientPrescriptionInput extends Activity {

	protected String medication = "";
	protected String instruction = "";
	/***
	 * Construct a user interface activity for displaying the patient description.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//inherit all instances from activity
		setContentView(R.layout.activity_get_patient_prescription);
		setupUI(findViewById(R.id.container));	///set the screen

		final EditText description_text = (EditText)findViewById(R.id.patientDescription);	///find the id of the patient's description
		final EditText instruction_text = (EditText)findViewById(R.id.get_instruction);	///find the id of the patient's description
		Button updateButton = (Button) findViewById(R.id.descriptionUpdate);	///set up the button  for starting up the looking for the description
		updateButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	medication  = description_text.getText().toString();//get user input
		    	instruction  = instruction_text.getText().toString();//get user input
		    	if (!medication.equals("") && !instruction.equals("")){
					Intent returnIntent = new Intent();
					returnIntent.putExtra("Medication", medication );//return the user input to the back end
					returnIntent.putExtra("Instruction", instruction );//return the user input to the back end
					setResult(RESULT_OK,returnIntent);
					finish();	///activity finish
		    	}
		    	else{
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, "", duration);
					toast = Toast.makeText(context, "Prescription must not be empty", duration);
					toast.show();
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

	        view.setOnClickListener(new OnClickListener() {//set what happen after clicking
				@Override
				public void onClick(View v) {	///set up the time when keyboard show up 
					// TODO Auto-generated method stub
					hideSoftKeyboard(PatientPrescriptionInput.this);
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
