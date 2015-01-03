package com.example.nurseapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

/**
 * An PatientUpdate is a activity focused on what the user can do on the screen
 * that user tries to update the patient's information. Information include patient's
 * status, prescription, description.
 * @author Administrator
 *
 */
public class PatientUpdate extends Activity {
	
	//initialize a instance for storing user's input
	private String userType = "";
	private String healthCardNum = "";
	/***
	 * Construct a user interface activity for updating the patient's information.
	 * Information include: visit_record, status, description, prescription, has_seen_doctor
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//inherit from activity.
		//set the input box's view
		setContentView(R.layout.activity_patient_update);

		//create a new bundle to get user's input
		Bundle b = getIntent().getExtras();
		//use the built in method in bundle to allow the instance userType to get input from user
		userType = b.getString("User Type");
		healthCardNum = b.getString("HealthCardNum");
		String name = b.getString("NAME");
		String birthDate = b.getString("BIRTHDATE");
		
		Button seenDoctorButton = (Button) findViewById(R.id.seend_doctor_but);
		Button exitButton = (Button) findViewById(R.id.cancel_but);
		Button updateStatusButton = (Button) findViewById(R.id.updateStatus);
		Button updateDescriptionButton = (Button) findViewById(R.id.updateDescription);
		Button updateVisitButton = (Button) findViewById(R.id.visit);
		Button updatePrescriptionButton = (Button) findViewById(R.id.updatePresicription);
		
    	TextView healthCardNum_view = (TextView)findViewById(R.id.display_hc);
    	TextView name_view = (TextView)findViewById(R.id.display_name);
    	TextView birthDate_view = (TextView)findViewById(R.id.display_birth_date);
		
    	healthCardNum_view.setText("Health Card Number: " + healthCardNum);
    	name_view.setText(name);
    	birthDate_view.setText("Birth Date: " + birthDate);
    	
    	if (userType.equals("NURSE")){
    		((ViewManager)updatePrescriptionButton.getParent()).removeView(updatePrescriptionButton);
    	}
    	else if (userType.equals("PHYSICIAN")){
    		((ViewManager)updateVisitButton.getParent()).removeView(updateVisitButton);
    		((ViewManager)updateDescriptionButton.getParent()).removeView(updateDescriptionButton);
    		((ViewManager)updateStatusButton.getParent()).removeView(updateStatusButton);
    		((ViewManager)seenDoctorButton.getParent()).removeView(seenDoctorButton);
    	}
		
		
		//set up the button for reading the user's input
		updateStatusButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {	///check whether the patient_record exists
		    	try {
					updatePatientStatus();	///doing the checking
				} catch (FileNotFoundException e) {		///if the file does not exist, raise exception
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_SHORT;
		        	Toast toast = Toast.makeText(context, "The Patient does not exist", duration);
		        	toast.show();	///show the error information in screen
				}
		    }
		});
		
		///set up a button for update patient description
		updateDescriptionButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	try {	///check whether the patient file exists
					updatePatientDescription();
				} catch (FileNotFoundException e) {		///if the file does not exist, raise execption
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_SHORT;
		        	Toast toast = Toast.makeText(context, "The Patient does not exist", duration);
		        	toast.show();	///show the error information in screen
				}
		    }
		});
		
		//set up the button for updating the visit of a patient
		updateVisitButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {

		    		Intent returnIntent = new Intent();
		        	returnIntent.putExtra("Type", 4);//return the information to the back end
		        	returnIntent.putExtra("HealthCardNum", healthCardNum);
					setResult(RESULT_OK,returnIntent);
					finish();///the activity is finish
		    }
		});
		
		//set up the button for updating the prescription of patient.
		updatePrescriptionButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    		try {	///check whether the patient file exists
			    		updatePrescription();
					} catch (FileNotFoundException e) {		///if the file does not exist, raise exception
						Context context = getApplicationContext();	//get the user's input and save it into context
						int duration = Toast.LENGTH_SHORT;
			        	Toast toast = Toast.makeText(context, "The Patient does not exist", duration);
			        	toast.show();	///show the error information in screen
					}
		    }
		});
		
		//set up a button for nurse to mark that the patient has seen doctor
		seenDoctorButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {	//set what happen when the user click the button
		    	mark_seened_doctor();
		    }
		});
		
		//set up a button for exiting the screen.
		exitButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {	//set what happen when the user click the button
		    	Intent returnIntent = new Intent();
		    	setResult(RESULT_CANCELED,returnIntent);
				finish();///the activity is finish//update all statuses to the patient's file
		    }
		});
	}

	
	/***
	 * Update patient status into patient's file, when nurse thinks it is needed. Patient's
	 * statuses include his or her temperature, bpSystolic, bpDiastolic, and actual heart rate.
	 * Create a new patient file for patient, if the patient is new to the hospital.
	 * 
	 * @throws FileNotFoundException
	 * 			-if the patient file does not exist, which mean the patient is new to the hospital.
	 */
	public void updatePatientStatus() throws FileNotFoundException{
			Intent getStatus = new Intent(this, PatientStatusInput.class);
			startActivityForResult(getStatus, 1);
	}
	
	/***
	 *  Update a description of the patient into patient's file, when nurse thinks it is needed.
	 * @throws FileNotFoundException
	 * 			- if patient's description txt file does not exist 
	 */	
	public void updatePatientDescription() throws FileNotFoundException{
			Intent getStatus = new Intent(this, PatientDescriptionInput.class);
			startActivityForResult(getStatus, 2);
	}
	
	/***
	 *  Update a prescription of the patient into patient's file, when nurse thinks it is needed.
	 * @throws FileNotFoundException
	 * 			- if patient's prescription txt file does not exist 
	 */	
	public void updatePrescription() throws FileNotFoundException{
			Intent getStatus = new Intent(this, PatientPrescriptionInput.class);
			startActivityForResult(getStatus, 3);
	}
	
	/**
	 * Update the patient waiting list file, and remove the patient from the list
	 * according to his or her healthCardNum.
	 */
	public void mark_seened_doctor(){
		if (userType.equals("NURSE")){
			Intent returnIntent = new Intent();
			returnIntent.putExtra("Type", 5);
			returnIntent.putExtra("HealthCardNum", healthCardNum);
			setResult(RESULT_OK,returnIntent);
			finish();///the activity is finish//update all statuses to the patient's file
		}
		else{
    		//if the user is a nurse, prevent her from using this method
    		Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
        	Toast toast = Toast.makeText(context, "Only Nurse Can Mark Patient For Seeing The Doctor.", duration);
        	toast.show();	///show the error information in screen
		}
	}
	
	/***
	 * Get the all data needed for patient status, and process them and save them
	 * into patient's status file.
	 * @param requestCode
	 * 			- a integer that check whether the user is trying to input
	 * @param resultCode
	 * 			- a integer that tell the function that the user input is valid
	 * @param data
	 * 			- the information that user input
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	    		Context context = getApplicationContext();
	    		double[] status_date = new double[4];
	    		status_date[0] = Integer.parseInt(data.getStringExtra("Temperature")); ///allow user to input all the information
	    		status_date[1] = Integer.parseInt(data.getStringExtra("Systolic"));
	    		status_date[2] = Integer.parseInt(data.getStringExtra("Diastolic"));
				status_date[3] = Integer.parseInt(data.getStringExtra("Heart Rate"));
				Intent returnIntent = new Intent();
				returnIntent.putExtra("Type", 1);
				returnIntent.putExtra("Status", status_date);
				returnIntent.putExtra("HealthCardNum", healthCardNum);
				setResult(RESULT_OK,returnIntent);
				finish();///the activity is finish//update all statuses to the patient's file
	    		}
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
	    else if (requestCode == 2) {
	        if(resultCode == RESULT_OK){
	        	String patitentDescription = data.getStringExtra("Description");
	        	Intent returnIntent = new Intent();
	        	returnIntent.putExtra("Type", 2);
				returnIntent.putExtra("Description", patitentDescription);
				returnIntent.putExtra("HealthCardNum", healthCardNum);
				setResult(RESULT_OK,returnIntent);
				finish();///the activity is finish//update all statuses to the patient's file
	    		}
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
	    else if (requestCode == 3) {
	        if(resultCode == RESULT_OK){
	        	//store the doctor's medication and instruction into two instances
	        	String medication = data.getStringExtra("Medication");
	        	String instruction = data.getStringExtra("Instruction");
	        	Intent returnIntent = new Intent();
	        	//return the doctor's input the the back end.
	        	returnIntent.putExtra("Type", 3);
				returnIntent.putExtra("Medication", medication);
				returnIntent.putExtra("Instruction", instruction);
				returnIntent.putExtra("HealthCardNum", healthCardNum);
				setResult(RESULT_OK,returnIntent);
				finish();///the activity is finish//update all statuses to the patient's file
	    		}
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
	}//onActivityResult
	
}
