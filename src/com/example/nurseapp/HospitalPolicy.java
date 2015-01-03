package com.example.nurseapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import android.content.Context;

/**
 * It is a class for storing all kinds of hospital's policies, for example,
 * how the hospital calculate the urgency of a patient. It also includes the 
 * method of sorting the patient waiting list in the order of urgency point,
 * from the most urgent to the least urgent.
 * @author Administrator
 *
 */
public class HospitalPolicy
{

	private Context context;
	/**
	 * Constructor for HospitalPolicy
	 */
	public HospitalPolicy (Context context)
	{
		this.context = context;
	}


	/**
	 * Takes in a Map<String, Patient> and returns an arrayList by Urgency including their name, birthdate, and health card number.
	 *Most urgent to least urgent, if not seen by doctor
	 * @param patientsList
	 *              - Map of <health card number, Patient>
	 * @return
	 *              - returns a arraylist of string of Patients' names ordered by their urgency
	 * @throws FileNotFoundException 
	 */

	public ArrayList <String> getListByUrgency (Map < String, Patient > patientsList) throws FileNotFoundException{
		
		ArrayList <Patient> patientUrgencyOne = new ArrayList <Patient>();
		ArrayList <Patient> patientUrgencyTwo = new ArrayList <Patient>();
		ArrayList <Patient> patientUrgencyThree = new ArrayList <Patient>();
		ArrayList <String> patientUrgencyRecord = new ArrayList <String>();
		ArrayList <Patient> patientNoRecord = new ArrayList <Patient>();
		
		ArrayList < String> patientListUrgency = new ArrayList < String > ();
		String name = "";
		for (Patient patient: patientsList.values ())   
		{
			int urgency = getUrgency (patient);     ///calculate the urgency of the patient.
			name = patient.getName();
			if (urgency >= 3)		///check if the patient has urgency of 3
			{
				patientUrgencyThree.add(patient);
				patientUrgencyRecord.add(Integer.toString(urgency));
			}
			else if (urgency == 2)          ///check if the patient has urgency of 2
			{
				patientUrgencyTwo.add(patient);
			}

			else if (urgency == 1)          ///check if the patient has urgency of 1
			{
				patientUrgencyOne.add(patient);
			}
			else            ///check if the patient has urgency of 0
			{
				patientNoRecord.add(patient);
			}
		}

		///combine the separated list to single one

		for (int count = 0; count < patientUrgencyThree.size(); count++){    ///combine the urgency of 3 list
			name = patientUrgencyThree.get(count).getHealthCardNum() + ",Urgency: " + patientUrgencyRecord.get(count);
			patientListUrgency.add (name);
		}

		for (Patient patient : patientUrgencyTwo){      ///combine the urgency of 2 list
			name = patient.getHealthCardNum() + ",Urgency: 2";
			patientListUrgency.add (name);
		}

		for (Patient patient : patientUrgencyOne){      ///combine the urgency of 1 list        
			name = patient.getHealthCardNum() + ",Urgency: 1";
			patientListUrgency.add (name);
		}
		for (Patient patient : patientNoRecord){	//////combine the no status list
			name = patient.getHealthCardNum() + ",Urgency: 0";
			patientListUrgency.add (name);
		}
		return patientListUrgency;
	}

	/**
	 * Takes in a Patient object and returns its urgency.
	 * @param patient
	 *              - Patient object
	 * @return
	 *              - Urgency point which is an integer
	 */
	public int getUrgency (Patient patient)
	{
		int urgency = 0;
		String[] statusInfo = new String[5];
		
		///calculate the urgency of the patient
		try
		{       ///get the status through the method in Patient
			statusInfo = patient.getStatus ();
			if (statusInfo == null || Double.parseDouble (statusInfo[0]) == 0.0 || Double.parseDouble (statusInfo[1]) == 0.0 || Double.parseDouble (statusInfo[2]) == 0.0 || Double.parseDouble (statusInfo[3]) == 0.0){
				return -1;
			}
			urgency += checkAge (patient.getBirthDate ());  ///put patient's status into the list
		}
		catch (IOException e)  //if the patient isn't at the hospital (ie. the file doesn't exist)
		{       ///if the patient does not exist, return -1
			return -1;
		}
		///calculating urgency through methods in hospitalPolicy.
		urgency += checkBloodPressure (Double.parseDouble (statusInfo[1]), Double.parseDouble (statusInfo[2]));
		urgency += checkHeartRate (Double.parseDouble (statusInfo[3]));
		urgency += checkTemperature (Double.parseDouble (statusInfo[0]));
		return urgency;
	}


	/**
	 * Takes in a double type and double and double pressure and checks if blood pressure is urgent, return 1 if it is otherwise 0
	 * @param type
	 *              - A double to tell which type of blood pressure. If 0 then systolic and 1 then diastolic.
	 * @param pressure
	 *              - A double to tell the blood pressure.
	 * @return
	 *              - returns the an integer of urgency (0 or 1)
	 */
	public int checkBloodPressure (double getBPSystolic, double getBPDiatolic)
	{       ///if the bloodpressure is too high, give one point
		if (getBPSystolic >= 140)
			return 1;
		else if (getBPDiatolic >= 90)
			return 1;
		return 0;       ///if not, not give
	}


	/**
	 * Takes in double representing the heart rate and checks if heart rate is urgent, if it is return 1 otherwise 0
	 * @param heart_rate
	 *              - A double which is the heart rate
	 * @return
	 *              - return an int of urgency (0 or 1)
	 */
	public int checkHeartRate (double heart_rate)
	{
		///if the heart_rate is too high, give one point
		if (heart_rate >= 100 || heart_rate <= 50)
			return 1;
		return 0;///if not, give nothing
	}


	/**
	 * Takes in a string, representing the birthday. Checks if age <= 2. Return 1 if true, otherwise 0
	 * @param date
	 *              - String which represents the date. The format that it takes is "yyyy-mm-dd"
	 * @return
	 *              - the urgency if age < 2 (1 for urgency, 0 for no urgency)
	 * @throws FileNotFoundException
	 */
	public int checkAge (String date) throws FileNotFoundException
	{       ///get the birth date of the patient, and calculate the age
		Date twoYearsAgo = new Date ();
		twoYearsAgo.setYear (twoYearsAgo.getYear () - 2);
		String[] date1 = date.split ("-");
		Date birthDate = new Date (Integer.parseInt (date1 [0]), Integer.parseInt (date1 [1]), Integer.parseInt (date1 [2]));
		if (twoYearsAgo.after (birthDate))      ///if the age is too high, give one point
			return 1;
		return 0;       /// if not, give nothing
	}


	/**
	 * Takes in a double, representing temperature, and checks if critical. If urgent return 1 otherwise 0.
	 * @param temperature
	 *              - double which represents the temperature
	 * @return
	 *              - return an int if temperature is critical (0 for non-urgent and 1 for urgent)
	 */
	public int checkTemperature (double temperature)
	{
		if (temperature >= 39.0)        ///if the temperature is too high, give one point
			return 1;
		return 0;
	}
}
