package com.example.nurseapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An PatientSelection is a activity focused on what the user can do on the screen
 * that user views the patient list, and he or she can select the patient's record
 * in order to read the information more detailed.
 * @author Administrator
 *
 */
public class PatientSelection extends Activity {

	private int actionCode = 0;
	private ArrayList<String[]> allPatientsInfo;
	private ArrayList<String[]> allPatientsInSearch;
	private ArrayList<String> allPatientNumber;
	private ListView listview;
	private Context context;
	private int duration = Toast.LENGTH_SHORT;
	@Override
	/***
	 * Construct a user interface activity for patient record selection during viewing
	 * the patient record list.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//inherit from the previous activity
		//set the view of patient selection
		setContentView(R.layout.activity_patient_selection);

		Bundle b = getIntent().getExtras();
		
		//initialize all instances for storing the information needed to set up the selection box 
		allPatientsInfo = new ArrayList<String[]>();
		allPatientsInSearch = new ArrayList<String[]>();
		ArrayList<String> allPatientName = b.getStringArrayList("NAMES");
		allPatientNumber = b.getStringArrayList("HCNUMBER");
		ArrayList<String> allPatientBirthDate = b.getStringArrayList("BIRTHDATE");
		ArrayList<String> allPatientInfo = b.getStringArrayList("INFO");
		actionCode = b.getInt("ActionCode");
		context = this;
		
		//collect every patients' information and store them into the instances above
		for (int count = 0; count < allPatientName.size(); count++){
			String[] info = new String[4];
			info[0] = allPatientName.get(count);
			info[1] = allPatientNumber.get(count);
			info[2] = allPatientBirthDate.get(count);
			info[3] = allPatientInfo.get(count);
			allPatientsInfo.add(info);
		}
		
		listview = (ListView) findViewById(R.id.patientsView);
		final EditText getHcNum = (EditText) findViewById(R.id.searchForPatient);
		Button searchButton = (Button) findViewById(R.id.search_but);
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			//set what happen when click the button
			public void onClick(View v) {
				if (allPatientNumber.contains(getHcNum.getText().toString())){
		    		Intent returnIntent = new Intent();
		    		returnIntent.putExtra("HealthCardNum", getHcNum.getText().toString());
		        	returnIntent.putExtra("ActionCode", actionCode);
					setResult(RESULT_OK,returnIntent);
					finish();///the activity is finish
				}
				else{
					Toast toast = Toast.makeText(getApplicationContext(), "Patient is not on the list.", duration);
					toast.show();
				}
			}
		});
		
		getHcNum.addTextChangedListener(new TextWatcher() {

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	onTextChanging(s);
	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {
	        	return ;
	        }
	        
			@Override
			public void afterTextChanged(Editable s) {
				return ;
			}
			
			public void onTextChanging(CharSequence s){
	        	allPatientsInSearch = new ArrayList<String[]>();
	        	ArrayList<String> allPatientsNumerInSearch = new ArrayList<String>();
	        	for (String[] info : allPatientsInfo){
	        		if (info[1].contains(s)){
	        			allPatientsInSearch.add(info);
	        			allPatientsNumerInSearch.add(info[1]);
	        		}
	        	}
	        	
	        	LayoutParams oldListViewLayout = (LayoutParams) listview.getLayoutParams();
	        	
	        	((ViewManager)listview.getParent()).removeView(listview);
	        	FrameLayout parentLayout = (FrameLayout) findViewById(R.id.container);
	    		//create a StableArrayAdapter for selection box
	    		final StableArrayAdapter adapter = new StableArrayAdapter(context,
	    				R.layout.custom_list_item, allPatientsNumerInSearch);
	    		adapter.setPatientsInfo(allPatientsInSearch);
	    		listview = new ListView(getApplicationContext());
	    		listview.setLayoutParams(oldListViewLayout);
	    		//create a list to contain all the box
	    		listview.setAdapter(adapter);
	    		parentLayout.addView(listview);
	    		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() 
	    		{
	    			@Override
	    			//set up the box and allow user to click on the box
	    			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) 
	    			{	
	    				//set up the information that will be showed on the box
	    				final String item = (String)parent.getItemAtPosition(position);
	    	    		Intent returnIntent = new Intent();
	    	        	returnIntent.putExtra("HealthCardNum", item);
	    	        	returnIntent.putExtra("ActionCode", actionCode);
	    				setResult(RESULT_OK,returnIntent);
	    				finish();///the activity is finish
	    			}

	    		});
			}
	    });
		
		//create a StableArrayAdapter for selection box
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				R.layout.custom_list_item, allPatientNumber);
		
		adapter.setPatientsInfo(allPatientsInfo);
		
		//create a list to contain all the box
		listview.setAdapter(adapter);

		//put those selection box into the list
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			@Override
			//set up the box and allow user to click on the box
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) 
			{	
				//set up the information that will be showed on the box
				final String item = (String)parent.getItemAtPosition(position);
	    		Intent returnIntent = new Intent();
	        	returnIntent.putExtra("HealthCardNum", item);
	        	returnIntent.putExtra("ActionCode", actionCode);
				setResult(RESULT_OK,returnIntent);
				finish();///the activity is finish
			}

		});
	}
	
	/**
	 * StableArrayAdapter is a class for creating the selection box of
	 *  a patient in the user interface.
	 * @author Administrator
	 *
	 */
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    Map<String, Integer> mIdMap = new TreeMap<String, Integer>();
	    Context context;
	    ArrayList<String[]> patientsInfo = new ArrayList<String[]>();
	    
	    /**
	     * constructor for the class
	     * @param context
	     * 			- a in stance storing the information show in the screen
	     * @param textViewResourceId
	     * 			- the id of the box in the screen
	     * @param objects
	     * 			- all information of the object 
	     */
	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      this.context = context;
	      for (int i = 0; i < objects.size(); ++i) {
	    	  mIdMap.put(objects.get(i), i);
	    	  }
	      }
	    
	    /**
	     * Get Arraylist that contained all the patient's information,
	     * and save them into the patientsInfo.
	     * @param getInfo
	     * 			- Arraylist that contained all the patient's information 
	     */
	    public void setPatientsInfo(ArrayList<String[]> getInfo){
	    	patientsInfo = getInfo;
	    }
	    

	    /**
	     * PatientInfoHolder is a class that for holding a single patient's
	     * information in form of text view
	     */
	    class PatientInfoHolder
	    {
	        TextView healthCardNum;
	        TextView name;
	        TextView birthDate;
	        TextView info;
	    }
	    
	    /**
	     * Get the position and view setting, return the final view
	     * of the selection box. 
	     * @param position
	     * 			- position of the box
	     * @param convertView
	     * 			- the view of the box
	     */
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	View row = convertView;
	    	PatientInfoHolder holder = null;
	    	LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	    	row = inflater.inflate(R.layout.custom_list_item, parent, false);
	    	holder = new PatientInfoHolder();

	    	//set the holder's view in the screen
	    	holder.healthCardNum = (TextView)row.findViewById(R.id.set_hc_num);
	    	holder.name = (TextView)row.findViewById(R.id.set_patient_name);
	    	holder.birthDate = (TextView)row.findViewById(R.id.set_birth_date);
	    	holder.info = (TextView)row.findViewById(R.id.additionalInfo);

	    	//get the patient's information 
	    	String[] info = new String[3];
	    	info = patientsInfo.get(position);

	    	//put all the information into the PatientInfoHolder
	    	holder.name.setText(info[0]);
	    	holder.healthCardNum.setText("Health Card Number: " + info[1]);
	    	holder.birthDate.setText("Birth Date: " + info[2]);
	    	holder.info.setText(info[3]);

	    	//give the holder a view tag.
	    	row.setTag(holder);
	    	return row;
	   
	  }
	    /**
	     * Get the position of the box, and return the ID in form of long.
	     * @param position
	     * 			-box's position
	     */
	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }
	}
}

