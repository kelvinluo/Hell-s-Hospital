package com.example.nurseapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/***
 * Stores all the information about the patient (name, health card number, prescription, 
 * description, status, blood pressure, blood pressure type, 
 * temperature, heart rate, and whether they've seen a doctor or not) and stores this
 * information in variables, as well as writing it to text files (so the data is preserved 
 * even when the app is closed), which are stored in a folder unique to each patient. 
 * It can also retrieve previous data which has been stored in the files.
 * @author Administrator
 */
public class Patient {
	/***
	 * The path where the patient data is stored. This needs to be public so other classes
	 * can access it.
	 */
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Hells Hospital/";
	
	//class variables which store the latest data for the patient

	/***
	 * The Patient's health card number.
	 */
	private String healthCardNum = "";
	 
	private boolean seenDoctor = false;
	
	/***
	 * The Patient's name.
	 */
	private String patientName = "";
	
	/***
	 * The Patient's birth date.
	 */
	private String birthdate = "";
	
	/***
	 * The unique directory used to store all text files for the Patient.
	 */
	private String directory = "";
	
	/***
	 * The description for the Patient.
	 */
	private String description = "";
	
	/***
	 * The time when the Patient's description has been updated.
	 */
	private String descriptionUpdateTime = "";
	
	/***
	 * The status of the Patient when they visit.
	 */
	private String visitStatus = "";
	
	/***
	 * The time when the Patient arrived at the hospital.
	 */
	private String arrivalTime = "";
	
	/***
	 * The array which stores the Patient's status information 
	 * (temperature, blood pressure, blood pressure type, and heart rate).
	 */
	private String [] statusInfo = new String[4];	
	
	/***
	 * The Patient's temperature.
	 */
	private double temperature = 0.0;
	
	/***
	 * The Patient's blood pressure (if they have systolic blood pressure).
	 */
	private double bloodPressSys = 0.0;
	
	/***
	 * The Patient's blood pressure (if they have diatolic blood pressure).
	 */
	private double bloodPressDia = 0.0;
	
	/***
	 * The Patient's heart rate.
	 */
	private double heartrate = 0.0;
	
	/***
	 * The time when the Patient's status has been updated.
	 */
	private String statusUpdateTime = "";
	
	/***
	 * The time when a prescription has been given to a Patient.
	 */
	private String prescriptionTime = "";
	
	/***
	 * The Patient's prescribed medication.
	 */
	private String medication = "";
	
	/***
	 * The Patient's instruction for their prescription.
	 */
	protected String instruction = "";
	
	/***
	 * The context to be used to write files.
	 */
	private Context context;
	
	//filenames for the text files
	 
	/***
	* The visit text filename.
	*/
	private String visitFileDir = "";
	
	/***
	* The description text filename.
	*/
	private String descriptionFileDir = "";
	
	/***
	* The status text filename.
	*/
	private String statusFileDir = "";
	
	/***
	* The prescription text filename.
	*/
	private String prescriptionFileDir = "";
	
	/***
	* The filename for the text file which denotes whether a Patient has seen a doctor or not.
	*/
	private String seenDoctorFileDir = "";
	
	/***
	 * Constructs a Patient with names, healthCardNum, birthDate, and arrivalTime. Create
	 * a new activity context for this patient.
	 * @param get_context
	 * 			- a context that used for patient's information input
	 * @param name
	 * 			- name of the patient
	 * @param healthCardNum
	 * 			- health card number of patient
	 * @param birthDate
	 * 			- birth date of patient
	 * @param arrivalTime
	 * 			- arrival time of the patient to the hospital
	 * @throws IOException 
	 * 			- if the file does not exist
	 */
	public Patient (Context getContext, String gethealthCardNum, String getBirthDate, String getName){
		context = getContext;
	    birthdate = getBirthDate;
		healthCardNum = gethealthCardNum;
		patientName = getName;
		directory = DATA_PATH + healthCardNum + "/";//the unique patient directory
		visitFileDir = directory + "patients_visit.txt";//visit file
		statusFileDir = directory + "patients_status.txt";//status file
		descriptionFileDir = directory + "patients_description.txt";//visit file
		prescriptionFileDir = directory + "patients_prescription.txt";//prescription file
		seenDoctorFileDir = directory + "patients_doctor_record.txt";
	}
	/***
	 * Initialize all necessary variables in Patient. Check whether patient files
	 * are in the directory, if not, raise  IOException.
	 * @throws IOException
	 * 			- if patient's files do not exist.
	 */
	public void patientInit() throws IOException{
		//make a new directory unique to the Patient object being created
		int duration = Toast.LENGTH_LONG;
		File patient_dir = new File(directory);

		if(!patient_dir.exists()){
			patient_dir.mkdir();//makes the directory for the patient
		} 
		
		String visit_dir = DATA_PATH + healthCardNum + "/" + "patients_visit.txt";
		String status_dir = DATA_PATH + healthCardNum + "/" + "patients_status.txt";
		String description_dir = DATA_PATH + healthCardNum + "/" + "patients_description.txt";
		String prescription_dir = DATA_PATH + healthCardNum + "/" + "patients_prescription.txt";
		
		File visitFile = new File(visit_dir);//declare new file variables for each file
		File statusFile = new File(status_dir);
		File descriptionFile = new File(description_dir);
		File prescriptionFile = new File(prescription_dir);
		
		if (!visitFile.exists()){//create a new file for the visit if is doesn't exist
			visitFile.createNewFile();
		}
		initVisitInfo();

		if (!statusFile.exists()){//create a new file for the status if is doesn't exist
			statusFile.createNewFile();
		}
		initStatus();//call the method to get an ArrayList with the status information
		
		if (!descriptionFile.exists()){//create a new file for the status if is doesn't exist
			descriptionFile.createNewFile();
		}
		initDescription();//call the method to get an ArrayList with the status information
		
		if (!prescriptionFile.exists()){//create a new file for the prescription if is doesn't exist
			prescriptionFile.createNewFile();
		}
		initPrescription();
	}
	
	/***
	 * Take the doctor-seeing time and visit-condition, and update
	 * those information into the patient's file.
	 * @param time
	 * 			- a string that contains the doctor-seeing time.
	 * @param visit
	 * 			- a string that contains the patient's visit condition(visited, not visited, waiting, not arrived, seen by doctor)
	 */
	public void updateVisit(String time){
		String filename = "patients_visit.txt";
		String visitInfo = time;		///the format of saving date
		generateNoteOnSD (visitInfo, filename, directory);		///call generateNoteOnSDto update data
		arrivalTime = time;
	}
	
	/***
	 * Helper function for the constructor. It is used to load old data if the patient
	 * has already been registered in the system.
	 * @return 
	 * 			- Array of String which is in the format [time, visit]
	 * @throws IOException
	 * 			- if the file reading fail
	 */
	public void initVisitInfo() throws IOException{
		
		File file = new File(visitFileDir);		///read file
	    FileReader fileRead = new FileReader(file); 	
		BufferedReader bufferRead = new BufferedReader(fileRead); ///read the whole file 
		String line =  ""; 
		String last_line = "";
		//read the file
		while((line = bufferRead.readLine())!=null){ 	///read line by line
				last_line = line.substring(3, line.length());
		}

		String result = "";
		
		if (last_line == ""){//if the patient hasn't visited
			result = "00:00:00";		///record time
		}
		else{
			result = last_line;	///split the line
		}
		arrivalTime = result;
		bufferRead.close();
	}

	/***
	 * Return a ArrayList that contains all patient's statuses. If the statuses file
	 * does not exist then raise FileNotFoundException.
	 * @return 
	 * 			- a list contains patient's status
	 * @throws IOException 
	 * 			- if the statuses file does not exist
	 */

	public String[] getStatus() throws IOException{
		return statusInfo;
	}
	
	/***
	 * Update the status to the patient's status file.
	 * @param temp
	 * 			- temperature of the patient
	 * @param bloPress
	 * 			- bloodPress of the patient
	 * @param time
	 * 			- recording time of the status
	 */
		
	public void updateStatus(double temp, double[] bloPress, String time, double heartRate){
		String filename = "patients_status.txt";//status file
		generateNoteOnSD (Double.toString(temp), filename, directory);
		generateNoteOnSD(Double.toString(bloPress[0]),filename ,directory);//write the blood pressure type
		generateNoteOnSD(Double.toString(bloPress[1]),filename, directory);//blood pressure value
		generateNoteOnSD (Double.toString(heartRate),filename, directory);//heart rate
		generateNoteOnSD (time, filename, directory);
		//update global data with the latest information
		temperature = temp;
		bloodPressSys = bloPress[1];
		bloodPressDia = bloPress[0];
		statusUpdateTime = time;
		heartrate = heartRate;
		//At index 0 is temperature
		statusInfo = new String[]{Double.toString(temperature), Double.toString(bloodPressSys), Double.toString(bloodPressDia), Double.toString(heartRate), statusUpdateTime};
	}
	
	/***
	 * Read the previous status file (for a saved Patient object) when the application is being loaded again
	 * and store the information in the class variables.
	 * 
	 * @throws IOException
	 * 		- if the directory or files does not exist
	 */
	private void initStatus() throws IOException{
		
		File file = new File(statusFileDir);
		
		FileReader fileRead = new FileReader(file); 
		BufferedReader bufferRead = new BufferedReader(fileRead); 
		String line = ""; 
		statusInfo = new String[5];
		ArrayList<String> getStatusInfo = new ArrayList<String>();
		
		//read all the contents of the file until the end
		while((line = bufferRead.readLine())!=null){ 
			getStatusInfo.add(line);
		}
		
		if (getStatusInfo.size() >= 5){
			//At index 0 is temperature
			statusInfo[0] = getStatusInfo.get(getStatusInfo.size() - 5);
			//At index 1 is blood pressure systolic
			statusInfo[1] = getStatusInfo.get(getStatusInfo.size() - 4);
			//At index 2 is bllod pressure diatolic
			statusInfo[2] = getStatusInfo.get(getStatusInfo.size() - 3);
			//At index 2 is rate heart
			statusInfo[3] = getStatusInfo.get(getStatusInfo.size() - 2);
			//At index 2 is update time
			statusInfo[4] = getStatusInfo.get(getStatusInfo.size() - 1);
		}
		else{
			for (int a = 0; a < 4; a++){
				statusInfo[a] = "0";
			}
			statusInfo[4] = "0000/00/00 00:00";	///initialize time
		}
		///initialize the statuses of patient
		heartrate = Double.parseDouble(statusInfo[0]);
		bloodPressSys = Double.parseDouble(statusInfo[1]);
		bloodPressDia = Double.parseDouble(statusInfo[2]);
		temperature = Double.parseDouble(statusInfo[3]);
		statusUpdateTime = statusInfo[4];
		bufferRead.close();
	}
	
	//these get methods just return the class variables
	/***
	 * Return the string that contains the description of the patient. If the 
	 * description file does not exist, raise FileNotFoundException.
	 * @return
	 * 			- a string that contains the description of the patient.
	 */
	public String getDescription(){
		return description;
	}
	
	/***
	 * Update the description of the patient of the patient's description file.
	 * @param context
	 * 			- a context that used for patient's information input
	 * @param description
	 * 			- a string description that describe the patient's condition
	 * @param time
	 * 			- a string that contain the recording time 
	 */
	public void updateDescription(Context context, String getDescription, String time){
		String filename = "patients_description.txt";//description file
		generateNoteOnSD (time, filename, directory);	///call generateNoteOnSD to update data to file
		generateNoteOnSD (getDescription, filename, directory);
		description = getDescription;
		descriptionUpdateTime = time;	///get system time as the description update time
		
	}
	
	public void updateSeenDoctor(String time){
		generateNoteOnSD(time, "patients_doctor_record.txt", directory);
	}
	
	/***
	 * Read the previous description file (for a saved Patient object) when the application is being loaded again
	 * and store the information in the class variables.
	 * 
	 * @throws IOException
	 * 		- if the directory or files does not exist
	 */
	private void initDescription() throws IOException{
		File file = new File(descriptionFileDir);
		
	    FileReader fileRead = new FileReader(file); 
		BufferedReader bufferRead = new BufferedReader(fileRead); 	///read whole file
		String line =  ""; 
		ArrayList<String> last_line = new ArrayList<String>();
		//read the file
		while((line = bufferRead.readLine())!=null){ 	///read line by line
			last_line.add(line);
		}
		
		if (last_line.size() < 2){//if the patient hasn't visited
			description = "";
			descriptionUpdateTime = "";
		}
		else{	///get visitStatus and descriptionUpdateTime through checking the number of line in txt file
			description = last_line.get(last_line.size() - 2);
			descriptionUpdateTime = last_line.get(last_line.size() - 1);
		}
		bufferRead.close();
	}
	
	/***
	 * Update the prescription of the patient of the patient's prescription file.
	 * @param prescription
	 * 			- a string prescription that describe the patient's condition
	 */
	public void updatePrescription(Context context, String getMedication, String getInstruction, String time){
		String filename = "patients_prescription.txt";//description file
		generateNoteOnSD (time, filename, directory);
		generateNoteOnSD (getMedication, filename, directory);
		generateNoteOnSD (getInstruction, filename, directory);
		prescriptionTime = time;
		medication = getMedication;
		instruction = getInstruction;
	}
	
	/***
	 * Read the previous prescription file (for a saved Patient object) when the application is being loaded again
	 * and store the information in the class variables.
	 * 
	 * @throws IOException
	 * 		- if the directory or files does not exist
	 */
	private void initPrescription() throws IOException{
		File file = new File(prescriptionFileDir);
	    FileReader fileRead = new FileReader(file); 
		BufferedReader bufferRead = new BufferedReader(fileRead); 	///read whole file
		String line =  ""; 
		ArrayList<String> all_line = new ArrayList<String>();
		//read the file
		while((line = bufferRead.readLine())!=null){ 	///read line by line
			all_line.add(line);
		}
		
		if (all_line.size() >= 3){
			prescriptionTime = all_line.get(all_line.size() - 3);
			medication = all_line.get(all_line.size() - 2);
			instruction = all_line.get(all_line.size() - 1);
		}
		else{
			prescriptionTime = "0000/00/00 00:00";
			medication = "";
			instruction = "";
		}
		
		bufferRead.close();
	}
	
	/***
	 * Return an ArrayList of type String that contains all information for the Patient.
	 * @return
	 * 		- an ArrayList<String> of all information for the Patient
	 * @throws IOException 
	 */
	public ArrayList<String> getLatestRecord() throws IOException{
		ArrayList<String> result = new ArrayList<String>();
		
		///setting the saving format of data
		result.add("Health Card Number: " + healthCardNum);
		result.add("Name: " + patientName);
		result.add("Birthdate: " + birthdate);
		result.add("Latest Arrival Time: " + arrivalTime);

		
		result.add("\n");
		result.add("Latest Status Updated On " + statusUpdateTime);
		result.add("Temperature: " + temperature);
		result.add("Systolic Blood Pressure: " + bloodPressSys);
		result.add("Diastolic Bllod Pressure: " + bloodPressDia);
		result.add("Heart Rate: " + heartrate + " /minutes");
		
		result.add("\n");
		result.add("Latest Description Updated On " + descriptionUpdateTime);
		result.add("Description:" + description);
		
		result.add("\n");
		result.add("Latest Prescription Updated On " + prescriptionTime);
		result.add("Medication:" + medication);
		result.add("Instruction:" + instruction);
		return result;
	}
	
	/***
	 * Return a string that contains the arrival time of the patient. If the file does
	 * not exist, raise exception. 
	 * @return
	 * 			- a string that contains the arrival time of the patient.
	 * @throws FileNotFoundException
	 * 			- if the file does not exist.
	 */
	public String getArrivalTime() throws FileNotFoundException{
		return arrivalTime;
	}
	
	/***
	 * Return the name of the patient.
	 * @return
	 * 			- a string that contains the name.
	 * @throws FileNotFoundException
	 * 			- if the file does not exist.
	 */
	public String getName() throws FileNotFoundException{//need to throw this for HospitalPolicy
		return patientName;
	}
	
	/***
	 * Return the health card number of the patient.
	 * @return
	 * 			- a string contains the health card number
	 */
	public String getHealthCardNum(){
		return healthCardNum;
	}
	
	/***
	 * Return the birth date of the patient.
	 * @return
	 * 			- a string contains the birth date
	 */
	public String getBirthDate(){
		return birthdate;
	}
	
	/***
	 * Return a string that contains the patient status into the patient
	 * file in the format [health card number, name, birth date, temperature, heart rate, visit time, blood pressure, blood pressure type, description].
	 * showed below.
	 * @return
	 * 		- a String array which contains all the patient information in the format [health card number, name, birth date, temperature, heart rate, visit time, blood pressure, blood pressure type, description]
	 * @throws IOException 
	 */
	public ArrayList<String> getPatientAllRecord() throws IOException{
		ArrayList<String> result = new ArrayList<String>();
		
		///setting the saving format of data
		result.add("Health Card Number: " + healthCardNum);
		result.add("Name: " + patientName);
		result.add("Birthdate: " + birthdate);
		result.add("Latest Arrival Time: " + arrivalTime);
		result.add("\n");
		
		File statusFile = new File(statusFileDir);
		File descriptionFile = new File(descriptionFileDir);
		File visitFile = new File(visitFileDir);
		File prescriptionFile = new File(prescriptionFileDir);
		
		FileReader fileRead = new FileReader(visitFile);
		BufferedReader bufferRead = new BufferedReader(fileRead); 
		String line = ""; 
		
		result.add("Visiting History:");
		
		while((line = bufferRead.readLine())!=null){ 
			result.add("Patient Visited On: " + line);
		}
		
		result.add("\n");

		fileRead = new FileReader(statusFile);
		bufferRead = new BufferedReader(fileRead); 
		line = ""; 
		
		int count = 0;
		//read all the contents of the file until the end
		while((line = bufferRead.readLine())!=null){ 
			if (count == 0){
				result.add("Temperature: " + line);
			}
			else if (count == 1){
				result.add("Blood Pressure (Systolic): " + line);
			}
			else if (count == 2){
				result.add("Blood Pressure (Diatolic): " + line);
			}
			else if (count == 3){
				result.add("Heart rate: " + line);
			}
			else if (count == 4){
				result.add("Status Update Time: " + line);
				result.add("\n");
				count = -1;
			}
			count++;
		}
		
		bufferRead.close();
		count = 0;
		fileRead = new FileReader(descriptionFile);
		bufferRead = new BufferedReader(fileRead); 
		line = ""; 
		//read all the contents of the file until the end
		while((line = bufferRead.readLine())!=null){ 
			if (count == 0){
				result.add("Description Update Time: " + line);
				count++;
			}else{
				result.add("Description: " + line);
				result.add("\n");
				count = 0;
			}
		}
		
		result.add("\n");
		
		bufferRead.close();
		fileRead = new FileReader(prescriptionFile);
		bufferRead = new BufferedReader(fileRead); 
		line = ""; 
		count = 0;
		//read all the contents of the file until the end
		while((line = bufferRead.readLine())!=null){
			if (count == 0){
				result.add("Prescription Updated On " + line);
				count++;
			}
			else if (count == 1){
				result.add("Medication: " + line);
				count++;
			}
			else if (count == 2){
				result.add("Instruction: " + line);
				result.add("\n");
				count = 0;
			}
		}
		bufferRead.close();
		fileRead.close();
		return result;
	}
	/***
	 * Remove the directory (and all the files inside of it)
	 * for the Patient object.
	*/
	public void removePatientFiles(){
		File dir = new File(directory);
		if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            new File(dir, children[i]).delete();
	        }
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
	public void generateNoteOnSD(String sBody, String filename, String directory){
		int duration = Toast.LENGTH_SHORT;
		
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

	
	
}
