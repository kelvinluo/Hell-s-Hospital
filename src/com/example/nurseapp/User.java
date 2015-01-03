package com.example.nurseapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.lang.Object;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * An User is a activity focused on what the user can do on the screen
 * that after user login through input the right user name and right password.
 * In this activity, user can do many things, such as, reading the patient's
 * waiting list, reading a single patient's record, adding a patient to waiting
 * list, removing a patient from the list, updating patient's status, descriptions,
 * and prescription.
 * @author Administrator
 *
 */
public class User extends Activity {
	//initializing all instances that the activity needed
	protected String user_id;

	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Hells Hospital/";

	public static String time;

	///backstage
	private Map<String, Patient> patientsList;
	private Map<String, Patient> waitingList = new TreeMap<String, Patient>();
	private Map<String, Patient> urgencyList = new TreeMap<String, Patient>();
	private HospitalPolicy hospitalPolicy;
	private Map<String, ArrayList<Object>> waitingPatientsList;
	private Map<String, String> patientArrivalTimeList;
	private ArrayList<String> patientNumList = new ArrayList<String>();
	protected String waitingListFileDir = "";
	private String patients_dir = DATA_PATH+ "patient_records.txt";
	private String waiting_dir = DATA_PATH+ "waiting_list.txt";
	private String urgency_dir = DATA_PATH+ "urgency_list.txt";
	private String userType = "";
	private String userName = "";
	private Context context;
	private int duration = Toast.LENGTH_SHORT;
	/***
	 * Construct a new activity for Nurse. Check whether txt file "patient_records.txt"
	 * is in  the directory or not. If the file exists, read the content and save them
	 * into patientsList. Otherwise, create a new text file named "patient_records.txt"
	 * and create a new empty patientsList.
	 * 
	 * @param savedInstanceState
	 * 				- a bundle that for transfering data inside the android app. 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//inherit from activity
		super.onCreate(savedInstanceState);
		//set the view
		setContentView(R.layout.activity_nurse);
		
		//get the user input and store it into respective instance
		Bundle b = getIntent().getExtras();
		userType = b.getString("User Type").toUpperCase();
		userName = b.getString("User Name");

		TextView userPositionView = (TextView) findViewById(R.id.userProfession);
		TextView userNameView = (TextView) findViewById(R.id.userName);

		userPositionView.setText("Welcome, " + userType);
		userNameView.setText(userName);

		patients_dir = patients_dir.trim();
		waiting_dir = waiting_dir.trim();
		context = getApplicationContext();
		try {
			userInit();
		} catch (IOException e1) {

		}/// initialize variables 

		initButtonGetRecord();
		initButtonAddPatient();
		initButtonUpdatePatient();
		initButtonRankPatient();
	}

	/***
	 * Initialize hospitalPolicy, patientsList, allPatientsList, and patientArrivalTimeList.
	 * In addition, if the patient_records.txt does not exist in the directory, then create on.
	 * @throws IOException 
	 */
	private void userInit() throws IOException{
		///initialize variables
		hospitalPolicy = new HospitalPolicy(getApplicationContext());
		patientsList = new TreeMap<String, Patient>();
		waitingPatientsList = new TreeMap<String, ArrayList<Object>>();
		patientArrivalTimeList = new TreeMap<String, String>();

		String file_dir = DATA_PATH + "waiting_list.txt";	///it is saved at the external storage named Hell's Hospital.
		file_dir = file_dir.trim();
		File textFile = new File(file_dir);//find the file base on the directory
		if (!textFile.exists()){
			initInternalStorageSpace();
		}
		

		///it is saved at the external storage named Hell's Hospital.
		FileInputStream is = openFileInput("patient_records.txt");
		Scanner scanner = new Scanner(is);

		if (userType.equals("PHYSICIAN"))
		{
			ImageView icon = (ImageView) findViewById(R.id.position_icon);
			icon.setImageResource(R.drawable.doctor);
		}

		//******************try to add the path of waiting_patient_records.txt here
		try	/// checking whether the file exists or not
		{
			String line = ""; 
			ArrayList<String[]> all_info = new ArrayList<String[]>();
			while(scanner.hasNextLine()){ ///read all the lines in the file
				line = scanner.nextLine();
				String [] info = new String[3];	///split the line to a list
				info = line.split(",");
				all_info.add(info);
			}

			for (int i = 0; i < all_info.size(); i++){	///create a Patient for every patient
				Patient patient = new Patient(getApplicationContext(), all_info.get(i)[0], all_info.get(i)[2], all_info.get(i)[1]);
				patient.patientInit();
				patientNumList.add(all_info.get(i)[0]);
				patientsList.put(all_info.get(i)[0], patient); /// save info into the map
				patientArrivalTimeList.put(all_info.get(i)[0], this.getCurrentTime());
			}
			scanner.close();
		}
		catch(IOException e){	///raise exception if the file not exists
			Toast toast = Toast.makeText(context, e.getMessage(), duration);
			toast.show();
		}
		File waitingFile = new File(waiting_dir);//declare new file variables for each file
		if (!waitingFile.exists()){//create a new file for the visit if is doesn't exist
			waitingFile.createNewFile();
		}
		getPatientsOnWaitlingList();
		
		File urgencyFile = new File(urgency_dir);//declare new file variables for each file
		if (!urgencyFile.exists()){//create a new file for the visit if is doesn't exist
			urgencyFile.createNewFile();
		}
		getPatientsOnUrgencyList();
	}

	/**
	 * Initialize the internal storage space for the User class.
	 */
	private void initInternalStorageSpace(){
		try {
			//check if the patient record file exists or not by opening the file
			InputStream is;
			is = getAssets().open("Hells Hospital/patient_records.txt");//open the record file
			Scanner scanner = new Scanner(is);
			String line = "";
			//prepare to read the content inside file
			FileOutputStream outputStream = openFileOutput("patient_records.txt", MODE_PRIVATE);
			while(scanner.hasNextLine()){ ///read all the lines in the file
				line = scanner.nextLine() + "\n";
				outputStream.write(line.getBytes());//save line in the instance "line".
			}
			outputStream.close();
		} catch (IOException e) {
			//pop up the information for telling the user that the file not exists
			Toast toast = Toast.makeText(context, e.getMessage(), duration);
			toast.show();
		}

	}

	/**
	 * Get the string that contain the file name, and return True iff the
	 * file with that file name exists. 
	 * @param fname
	 * 			- a string that contained file name.
	 * @return
	 * 			- True iff the file exists
	 */
	public boolean fileExistance(String fname){
		PackageManager m = getPackageManager();//get the directory of the package
		String s = getPackageName();//save the directory to the instance s
		Toast toast;
		try {
			//check the package exist or not
			PackageInfo p = m.getPackageInfo(s, 0);
			s = p.applicationInfo.dataDir;	//save the directory
		} catch (NameNotFoundException e) {
			//pop up the error information
			toast = Toast.makeText(context, e.getMessage(), duration);
			toast.show();
		}
		//save the string directory in the format of file
		File file = new File(s + fname);
		toast = Toast.makeText(context, s, duration);
		toast.show();
		return file.exists();//return the boolean for checking the file existence.
	}

	/**
	 * Initialize the Button on the user interface.
	 */
	private void initButtonGetRecord(){
		///set up a button for get patient record
		Button getRecordButton = (Button) findViewById(R.id.getRecord);
		getRecordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			//set what happen when click the button
			public void onClick(View v) {
				try {
					selectPatient(1, patientsList, getPatientVisitRecords(), patientNumList);
				} catch (IOException e) {
					Toast toast = Toast.makeText(context, e.getMessage(), duration);
					toast.show();
				}
			}
		});
	}
	private void initButtonAddPatient(){
		//set the button for adding patient to the waiting list
		Button addPatientButton = (Button) findViewById(R.id.add_patient);
		addPatientButton.setOnClickListener(new View.OnClickListener() {
			@Override
			//set what happen when clicking
			public void onClick(View v) {
				//check whether the patient exists on the list
				Toast toast = Toast.makeText(context, "", duration);
				try {
					if (userType.equalsIgnoreCase("NURSE")){
						addPatient();
					}
					else{
						toast = Toast.makeText(context, "Only Nurse Can Add New Patient.", duration);
						toast.show();
					}
				} catch (Exception e) {
					toast = Toast.makeText(context, e.getMessage(), duration);
					toast.show();
				}
			}
		});
	}
	private void initButtonUpdatePatient(){

		//set the button for removing patient from the waiting list
		Button updateRecordButton = (Button) findViewById(R.id.updatePatientRecord);
		updateRecordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			//get user's input and store it into the instance
			public void onClick(View v) {
				try {//check the user's input is on the list or not
					selectPatient(2, patientsList, getPatientVisitRecords(), patientNumList);
				} catch (IOException e) {
					Toast toast = Toast.makeText(context, "Patient does not exist.", duration);
					toast.show();
				}
			}
		});
	}
	private void initButtonRankPatient(){
		///set up a button for return a patient list ranked by urgency
		Button getUrgenButton = (Button) findViewById(R.id.rankedByUrgen);
		getUrgenButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {	///check whether the method works properly
					getListOfPatientsByUrgency();
				} catch (IOException e) {		///raise exception, if the file does not exist
					Toast toast = Toast.makeText(context, "Type Error", duration);
					toast.show();
				}
			}
		});

		Button button= (Button) findViewById(R.id.logoutBut);	/// logout button interface setting
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	///set up the login button, link activities
				Intent intent = new Intent(User.this, LoginScreen.class);
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * Return the patient visit records, raise exception, if the patient
	 * record does not exist in the directory.
	 * @return
	 * 			- a map that is the patients' record, the key is the healthcard, and 
	 * the element is the patient
	 * @throws IOException
	 * 			- if the patient does not exist
	 */
	private Map<String, String> getPatientVisitRecords() throws IOException{
		//initialize a new map for later storing the patients' record
		Map<String, String> infos = new TreeMap<String, String>();
		for (String key : patientsList.keySet()){
			//for every patient's record, showing if it is on the doctor's waiting list.
			if (waitingList.containsKey(key)){
				//if the patient is on the waiting list, show on the screen that the patient is visiting
				infos.put(key, "Visiting");
			}
			else{
				//otherwise, show on the screen that the patient is not visited.
				infos.put(key, "Not Visited");
			}
		}
		return infos;
	}

	/**
	 * Get the string that contained the patient's health card number, and
	 * add the patient to doctor waiting list.
	 * @param health_card_number
	 * 			- a string that contained the patient's health card number
	 */
	private void addPatientToWaitingList(String health_card_number){
		generateNoteOnSD(health_card_number, "waiting_list.txt",DATA_PATH);
		waitingList.put(health_card_number, patientsList.get(health_card_number));
	}

	/**
	 * Add the patient to urgency list.
	 * @param health_card_number
	 * 			- a string that contained the patient's health card number
	 */
	private void addPatientToUrgencyList(String health_card_number){
		generateNoteOnSD(health_card_number, "urgency_list.txt",DATA_PATH);
		urgencyList.put(health_card_number, patientsList.get(health_card_number));
	}
	
	/**
	 * Add the patient to all patient list of the hospital.
	 */
	private void addPatient(){
		Intent addPatientAct = new Intent(this, PatientAddition.class);
		startActivityForResult(addPatientAct, 2);
	}

	/**
	 * Get the return code from the back end, the patient's status record list
	 * , and the patient's description record list, set up the selection box that
	 * can display patient's name, healthcard number, and birth date.
	 * @param returnCode
	 * 			- a integer that help the method to determine whose record it shoud display
	 * @param list
	 * 			- a map for giving the method patient's status
	 * @param additionalInfo
	 * 			- a map for giving the method patient's description and prescription.
	 * @throws FileNotFoundException
	 * 			- if the file not exists
	 */
	private void selectPatient(int returnCode, Map<String, Patient> list, Map<String, String> additionalInfo, ArrayList<String> ranking) throws FileNotFoundException{
		Intent getPatientAct = new Intent(this, PatientSelection.class);
		Bundle b = new Bundle();
		//collect the return code and replace bundle's actioncode
		b.putInt("ActionCode", returnCode);
		//initialize instances for storing pateinst's general information
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> hcNums = new ArrayList<String>();
		ArrayList<String> birthDates = new ArrayList<String>();
		ArrayList<String> infos = new ArrayList<String>();
		for (String hcNum : ranking){
			//store all the patient's information to respective arraylist.
			names.add(list.get(hcNum).getName());
			hcNums.add(hcNum);
			birthDates.add(list.get(hcNum).getBirthDate());
			infos.add(additionalInfo.get(hcNum));
		}
		//display the information for every patient in order below.
		b.putStringArrayList("NAMES", names);
		b.putStringArrayList("HCNUMBER", hcNums);
		b.putStringArrayList("BIRTHDATE", birthDates);
		b.putStringArrayList("INFO", infos);
		getPatientAct.putExtras(b);
		//push those instances to front end
		startActivityForResult(getPatientAct, 3);
	}

	/**
	 * Get a string that contained the patient's health card number,
	 * update the patient's status record according to what user did,
	 * throw exception, if the file does not exist
	 * @param healthCardNum
	 * 			- a string that contained the patient's health card number.
	 * @throws FileNotFoundException
	 * 			- file does exists
	 */	
	private void updatePatientRecord(String healthCardNum) throws FileNotFoundException{
		Intent getStatus = new Intent(this, PatientUpdate.class);
		Bundle b = new Bundle();
		b.putString("User Type", userType);
		b.putString("HealthCardNum", healthCardNum);
		b.putString("NAME", patientsList.get(healthCardNum).getName());
		b.putString("BIRTHDATE", patientsList.get(healthCardNum).getBirthDate());
		getStatus.putExtras(b);
		startActivityForResult(getStatus, 1);
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
		String healthCardNum = "";
		if(resultCode == RESULT_OK){
			//if the update checking pass, store the health card number of the patient that need file edition
			healthCardNum = data.getStringExtra("HealthCardNum");
		}
		//this update is for nurse to create and update a new patient for the hospital
		if (requestCode == 2) {
			addPatientOnFile(healthCardNum, resultCode, data);
		}
		else if (patientsList.containsKey(healthCardNum)){
			if (requestCode == 1) {
				updatePatientOnFile(healthCardNum, resultCode, data);
			}
			else if (requestCode == 3) {
				// this a update for removing patient
				Toast toast = Toast.makeText(context, "", duration);
				if(resultCode == RESULT_OK){
					int actionCode = data.getIntExtra("ActionCode", 0);
					if (actionCode == 1){
						//get the health card number of patient needed to be removed
						getPatientRecord(healthCardNum);
					}
					else if (actionCode == 2){
						try {
							//checking the health card number through updating it
							updatePatientRecord(healthCardNum);
						} catch (FileNotFoundException e) {
							//if the health card number not exist, pop up error information
							toast = Toast.makeText(context, e.getMessage(), duration);
							toast.show();
						}
					}
				}
			}
			else{
				Toast toast = Toast.makeText(context, "Action Cancelled", duration);
				toast.show();
			}
		}
		else{	///if patient is not in the list, give response to user in form of string
			Toast toast = Toast.makeText(context, "Action Cancelled", duration);
			toast.show();
		}
	}
	//onActivityResult

	/**
	 * Get the health card number and a integer that contained the feedback
	 * from other method, and update the what user want to respective patient
	 *  to the file.
	 * @param healthCardNum
	 * 			- a string that contained the health card of patient.
	 * @param resultCode
	 * 			- a integer that contained the feedback from other method
	 * @param data
	 * 			- intent that contained the information that user wants to update
	 */
	private void updatePatientOnFile(String healthCardNum, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			int updateType = data.getIntExtra("Type", 0);
			if (waitingList.containsKey(healthCardNum) && updateType != 4){	///check if the input on the list
				//check who is updating, nurse or physician
				//check whether the list contain that health card number
				
				//type 1 is status update 
				if (updateType == 1){
					double[] status = new double[4];
					status = data.getDoubleArrayExtra("Status");
					double[] bloodPressure = {status[1], status[2]};
					patientsList.get(healthCardNum).updateStatus(status[0], bloodPressure, getCurrentTime(), status[3]);
					Toast toast = Toast.makeText(context, "Patient status updated.", duration);
					toast.show();
				}
				//type 2 is description update
				else if (updateType == 2){
					String description = data.getStringExtra("Description");
					patientsList.get(healthCardNum).updateDescription(context, description, getCurrentTime());
					Toast toast = Toast.makeText(context, "Patient description updated.", duration);
					toast.show();
				}
				//type 3 prescription update
				else if (updateType == 3){
					String medication = data.getStringExtra("Medication");
					String instruction = data.getStringExtra("Instruction");
					patientsList.get(healthCardNum).updatePrescription(context, medication, instruction, getCurrentTime());
					Toast toast = Toast.makeText(context, "Patient prescription updated.", duration);
					toast.show();
				}
				//type 5 is removing from the waiting list updating
				else if (updateType == 5){
					if (!waitingList.containsKey(healthCardNum)){
						//when the patient never exist in the waiting list
						Toast toast = Toast.makeText(context, "Patient is not on the waiting list.", duration);
						toast.show();
					}
					else{
						//when the patient is still in the waiting list
						try {
							removePatientFromUrgencyList(healthCardNum);
						} catch (IOException e) {
							Toast toast = Toast.makeText(context, e.getMessage(), duration);
							toast.show();
						}
						patientsList.get(healthCardNum).updateSeenDoctor(getCurrentTime());
						Toast toast = Toast.makeText(context, "Patient " + healthCardNum + " is now seen the doctor.", duration);
						toast.show();
					}
				}
			}
			//type 4 is adding patient to waiting list updating
			else if (updateType == 4){
				patientsList.get(healthCardNum).updateVisit(getCurrentTime());
				if (!urgencyList.containsKey(healthCardNum)){
					//when the patient is not in the waiting list
					if (!urgencyList.containsKey(healthCardNum)){
						addPatientToWaitingList(healthCardNum);
					}
					addPatientToUrgencyList(healthCardNum);
					Toast toast = Toast.makeText(context, "Patient is now added on the waiting list.", duration);
					toast.show();
				}
				else{
					//when the patient is already in the waiting list
					Toast toast = Toast.makeText(context, "Patient is already on the waiting list.", duration);
					toast.show();
				}
			}
			else{	///if patient is not in the list, give response to user in form of string
				Toast toast = Toast.makeText(context, "Patient did not pay a visit yet.", duration);
				toast.show();
			}
		}
		//when the user click cancel, then end the method
		if (resultCode == RESULT_CANCELED) {
			Toast toast = Toast.makeText(context, "Update Cancelled", duration);
			toast.show();
		}
	}

	/**
	 * Get a string that contained the health card of patient, and remove that
	 * specific patient from waiting list, raise exception, if the waiting list
	 * file not exists.
	 * @param healthCardNum
	 * 			- a string that contained the health card of patient.
	 * @throws IOException
	 * 			- if the waiting list file not exists.
	 */
	private void removePatientFromUrgencyList(String healthCardNum) throws IOException{
		removeLine(healthCardNum, "urgency_list.txt", DATA_PATH);
		if (urgencyList.keySet().contains(healthCardNum)){
			urgencyList.remove(healthCardNum);
		}
	}
	
	/**
	 * Get the string that contained patient's healthcard number, a integer contained
	 * the feedback from other method, and the user's input, create a new txt file for that
	 * new patient, and save user's input into the file.
	 * @param healthCardNum
	 * 			- a string contained patient's health card number
	 * @param resultCode
	 * 			- a integer that contained the feedback from other method
	 * @param data
	 * 			- the user's input
	 */
	private void addPatientOnFile(String healthCardNum, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			//the patient in already on hospital's patient list
			Toast toast = Toast.makeText(context, "Patient is already exist!", duration);
			try {
				//checking if the inputs from user are valid or not, and if the patient on the list or not
				if (!patientsList.containsKey(healthCardNum) || healthCardNum.length() != 6 || !checkPatientOnList(healthCardNum)){	///if the patient in the list, update him to the waiting list
					//the patient is new to the hospital
					String birthDate = data.getStringExtra("Birth Date");//collect user's input
					String name = data.getStringExtra("Name");
					addPatientOnFile(healthCardNum + "," + name + "," + birthDate + "\n");
					//create a txt file for that patient
					Patient patient = new Patient(getApplicationContext(), healthCardNum, birthDate, name);
					patient.patientInit();
					//add this patient to the patientslist
					patientsList.put(healthCardNum, patient);
					patientNumList.add(healthCardNum);
					toast = Toast.makeText(context, "Patient is now added in record.", duration);
					toast.show();
				}
				else{	///if the patient is not on the list, then tell the user "Patient does not exist"
					if (healthCardNum.length() != 6){//if the error is due to the length of healthcard
						toast = Toast.makeText(context, "Invalid Health Card Number", duration);
					}
					toast.show();
				}
			} catch (IOException e) {
				//raise exception when the patient's file does not exist
				if (healthCardNum.length() != 6){
					toast = Toast.makeText(context, "Invalid Health Card Number", duration);
				}
				toast.show();
			}
		}
		//when user click back, the method stop
		else if (resultCode == RESULT_CANCELED) {
			Toast toast = Toast.makeText(context, "Add Patient Cancelled", duration);
			toast.show();
		}
		else{
			//checking who is adding, if physician, then tell him he can't create
			Toast toast = Toast.makeText(context, "Only nurse can create new patient record.", duration);
			toast.show();
		}
	}

	/***
	 *  Call the hospitalPolicy to measure the urgency of patient, and compare the 
	 *  urgency and  show the sorted list of patients in the user screen.
	 * @throws IOException 
	 */
	private void getListOfPatientsByUrgency() throws IOException{
		ArrayList<String> list = new ArrayList<String>();
		list = hospitalPolicy.getListByUrgency(urgencyList);
		Map<String, String> patientInfos = new TreeMap<String, String>();
		Map<String, Patient> patientRankedList = new TreeMap<String, Patient>();
		ArrayList<String> ranking = new ArrayList<String>();
		for (String hcNum : list){
			String[] info = new String[2];
			info = hcNum.split(",", 2);
			patientInfos.put(info[0], info[1]);
			patientRankedList.put(info[0], urgencyList.get(info[0]));
			ranking.add(info[0]);
		}
		selectPatient(1, patientRankedList, patientInfos, ranking);
	}

	/**
	 * Get a string that contained the patient's healthcard number, and display the
	 * patient with that healthcard number on screen.
	 * @param healthCardNum
	 * 			- a string that contained the patient's healthcard number 
	 */
	private void getPatientRecord(String healthCardNum){
		Context context = getApplicationContext();//collect user's input
		int duration = Toast.LENGTH_SHORT;
		if (patientsList.containsKey(healthCardNum)){//check if that healthcard number is on the list or not.
			Intent getVisit = new Intent(this, PatientRecordView.class);
			Bundle b = new Bundle();
			try {
				//set up the displaying view for get patientrecord method
				b.putStringArrayList("Latest Record", patientsList.get(healthCardNum).getLatestRecord());
				b.putStringArrayList("All Records", patientsList.get(healthCardNum).getPatientAllRecord());
				b.putString("Name", patientsList.get(healthCardNum).getName());
				b.putString("Health Card Number", patientsList.get(healthCardNum).getHealthCardNum());
				b.putString("Birth Date", patientsList.get(healthCardNum).getBirthDate());
				getVisit.putExtras(b);
				startActivity(getVisit);//display the view on the screen
			} catch (IOException e) {
				Toast toast = Toast.makeText(context, "", duration);
				toast = Toast.makeText(context, e.getMessage(), duration);
				toast.show();
			}

		}
		else{//pop up the error information if the patient does not exist
			Toast toast = Toast.makeText(context, "", duration);
			toast = Toast.makeText(context, "Patient does not exist.", duration);
			toast.show();
		}
	}

	/**
	 * Load all of the patients that are waiting to see doctor, raise exception,
	 * if the txt file for storing all the patient waiting for doctor not exists
	 * @throws IOException
	 * 			-if the doctor waiting list file does not exist
	 */
	private void getPatientsOnWaitlingList() throws IOException{
		File file = new File(waiting_dir);
		FileReader fileRead = new FileReader(file); 
		BufferedReader bufferRead = new BufferedReader(fileRead);	///read it line by line 
		String line = ""; 
		while((line = bufferRead.readLine()) != null){ ///read all the lines in the file
			line.replace("\n", "");
			waitingList.put(line, patientsList.get(line));
		}
	}

	/**
	 * Load all of the patients that are waiting to see doctor, raise exception,
	 * if the txt file for storing all the patient waiting for doctor not exists
	 * @throws IOException
	 * 			-if the doctor waiting list file does not exist
	 */
	private void getPatientsOnUrgencyList() throws IOException{
		File file = new File(urgency_dir);
		FileReader fileRead = new FileReader(file); 
		BufferedReader bufferRead = new BufferedReader(fileRead);	///read it line by line 
		String line = ""; 
		while((line = bufferRead.readLine()) != null){ ///read all the lines in the file
			line.replace("\n", "");
			urgencyList.put(line, patientsList.get(line));
		}
	}
	
	/***
	 * Update the information in sBody to the the file named "filename" in directory 
	 * named "directory"
	 * @param sBody
	 * 			- the string that contain the context that user want to put in txt file
	 * @param filename
	 * 			- the string that contain the file name of the file that needed
	 * @param directory
	 * 			- the string that contain the address of directory where that file is
	 */
	private void generateNoteOnSD(String sBody, String filename, String directory){
		String file_dir = directory + filename;
		file_dir = file_dir.trim();
		File textFile = new File(file_dir);//make a new file
		try
		{
			if (!textFile.exists()){	///if the file does not exist, create one
				textFile.createNewFile();
			}
			//write the text with FileWriter and BufferedReader
			FileWriter fw = new FileWriter(textFile.getAbsoluteFile(), true);	///prepare to write file
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(sBody + "\r\n");	///write line, and the line end with "\r\n"
			bw.close();	///close the file writer
		}
		catch(IOException e)	///raise exception, if the file does not exist
		{
		}
	}

	/**
	 * Get the string that needed to be updated to the patient's record, and then
	 * update this line of string to the file
	 * @param sBody
	 * 			- the string that needed to be updated to the patient's record
	 */
	private void addPatientOnFile(String sBody){
		try {
			FileOutputStream outputStream = openFileOutput("patient_records.txt", MODE_APPEND);
			outputStream.write(sBody.getBytes());
		} catch (IOException e) {
			Toast toast = Toast.makeText(context, e.getMessage(), duration);
			toast.show();
		}
	}

	Time today = new Time(Time.getCurrentTimezone()); // current time
	private static int day, month, year, hour, minutes;
	/***
	 * Return the current time in form of string
	 * @return
	 * 			- a string that contains current time
	 */
	private String getCurrentTime(){
		today.setToNow();
		year = Integer.valueOf(today.year);
		String[] date_info = new String[4];

		date_info[0] = Integer.toString(Integer.valueOf(today.month) + 1); // add 1 because 0 index
		date_info[1] = Integer.toString(Integer.valueOf(today.monthDay));
		date_info[2] = Integer.toString(Integer.valueOf(today.hour));
		date_info[3] = Integer.toString(Integer.valueOf(today.minute));
		for (int index = 0; index < 4; index++){
			if (date_info[index].length() < 2){
				date_info[index] = "0" + date_info[index];
			}
		}
		time = year + "/" + date_info[0] + "/" + date_info[1] + "/ " + date_info[2] + ":" + date_info[3];
		return time;
	}
	
	/**
	 * Base on the given string, find that string in the file and delete that line.
	 * 
	 * @param target
	 * 			- the string that contain the context that user want to remove from txt file
	 * @param file
	 * 			- the string that contain the file name of the file that needed
	 * @param directory
	 * 			- the string that contain the address of directory where that file is
	 * @throws IOException
	 * 			- if the file does not exists.
	 */
	private void removeLine(String target ,String file, String directory) throws IOException{
		File fileDir = new File(directory + file);
		FileReader fileRead = new FileReader(fileDir); 
		BufferedReader bufferRead = new BufferedReader(fileRead);	///read it line by line 
		String line = ""; 
		ArrayList<String> all_line = new ArrayList<String>();
		while((line = bufferRead.readLine()) != null){///read all the lines in the file
			//add the lines that not removed into instance line
			if (!line.substring(0, 6).equals(target)){
				all_line.add(line);
			}
		}
		PrintWriter writer = new PrintWriter(fileDir);
		//open the file writer
		writer.print("");
		writer.close();
		for (String item : all_line){
			//put all the lines in "line" into the file
			generateNoteOnSD(item, file, directory);
		}

	}

	/**
	 * Get a string that contained the patient's healthcard number, return true iff
	 * the patient is on the patient_records txt file. Raise exception, if the txt
	 * record file does not exist.
	 * @param healthCardNum
	 * 			- a string that contained the patient's healthcard number
	 * @return
	 * 			- true iff the patient is on the patient_records txt file
	 * @throws IOException
	 * 			- if the txt record file does not exist
	 */
	private boolean checkPatientOnList(String healthCardNum) throws IOException{
		File fileDir = new File(DATA_PATH + "patient_records.txt");
		FileReader fileRead = new FileReader(fileDir); 
		BufferedReader bufferRead = new BufferedReader(fileRead);	///read it line by line 
		String line = ""; 
		ArrayList<String> all_line = new ArrayList<String>();
		while((line = bufferRead.readLine()) != null){///read all the lines in the file
			String currentHCN = line.split(",")[0];
			if (currentHCN.equals(healthCardNum)){
				return true;
			}
		}
		return false;
	}
}
