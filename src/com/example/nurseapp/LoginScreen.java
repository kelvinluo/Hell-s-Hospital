package com.example.nurseapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * An LoginScreen is a activity focused on what the user can do on the screen
 * that user input the username and password.
 * @author Administrator
 *
 */
public class LoginScreen extends Activity {
	//create the file in the external memory
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Hells Hospital/";
	
	public String datePathDir;
	Time today = new Time(Time.getCurrentTimezone()); // get current time
	private String userType = "";
	private String name = "";
	/***
	 * Construct a new activity for the user login screen.
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//inherit from activity
        //set up the view
        setContentView(R.layout.activity_main);
        //set up the user interface
        setupUI(findViewById(R.id.logincontainer));
        
        File data_dir = new File(DATA_PATH);
        
        //If the storage directory of the program does not exist, create it
        if (!data_dir.exists()){
        	data_dir.mkdir();
        }
        
		today.setToNow(); // get current time
		
		Button button= (Button) findViewById(R.id.loginBut);
		//set up the button
		final EditText get_user_name = (EditText)findViewById(R.id.getUserName);
		final EditText get_password = (EditText)findViewById(R.id.getUserPass);
		//set what happen after clicking
		button.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {//set up what happen when click
		    	String user_name = get_user_name.getText().toString();
		    	String password = get_password.getText().toString();
		    	Context context = getApplicationContext();
		    	int duration = Toast.LENGTH_LONG;
		    	Toast toast = Toast.makeText(context, "Invalid Username/Password", duration);
				
		    	if (checkPassword(user_name, password)){
		    		int day = Integer.valueOf(today.monthDay);//get today's day
					int month = Integer.valueOf(today.month) + 1; // get today's month
					int year = Integer.valueOf(today.year);//get today's year
					int hour = Integer.valueOf(today.hour);//get hour
					int minutes = Integer.valueOf(today.minute);//get minute
					//save the login time in the file
			    	generateNoteOnSD(user_name + "  Login On: " + year + "/" + month + "/" + day + "/ " + hour + ":" + minutes);
			    	Intent intent = new Intent(LoginScreen.this, User.class);
					Bundle b = new Bundle();
					b.putString("User Type", userType);
					b.putString("User Name", user_name);
					intent.putExtras(b);
			    	startActivity(intent);
			    	toast = Toast.makeText(context, "Welcome, " + userType.toUpperCase() + " " + name, duration);
					toast.show();
					finish();
		    	}
		    	else{
		    		toast = Toast.makeText(context, "Invalid Username/Password", duration);
					toast.show();
		    	}
		    }
		});
	}
	
	/***
	 * Check whether the user has logged in before. If he did log in before,
	 * load his user information from file. If not, which mean application
	 * does not have this user's record, then create one for him.  
	 * @param sBody
	 */
	public void generateNoteOnSD(String sBody){
		File directory = new File(DATA_PATH);
		//check whether the file exists or not
		 if(!directory.exists()){  
             directory.mkdir();
         } 
		String file_dir = DATA_PATH + "userinfo.txt";	///it is saved at the external storage named Hell's Hospital.
		file_dir = file_dir.trim();
		File textFile = new File(file_dir);//find the file base on the directory
	    try
	    {	//if the file does not exist, create one
	        if (!textFile.exists()){
	        	textFile.createNewFile();
	        }
	        //open the file writer.
			FileWriter fw = new FileWriter(textFile.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(sBody + "\r\n");//update context to the file in the format showed before
			bw.close();//close the file writer
	    }
	    catch(IOException e)//if method catch a IOexception, do nothing
	    {
	    }
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
					hideSoftKeyboard(LoginScreen.this);
				}

	        });
	    }

	    //If a layout container, iterate over children and seed recursion.
	    if (view instanceof ViewGroup) {
	    	//If a layout container, iterate over children and seed recursion.
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	        	// a special method to cancel what the user has inputed 
	            View innerView = ((ViewGroup) view).getChildAt(i);
	            setupUI(innerView);
	        }
	    }
	}
	
	/**
	 *  Check the given password and see if it matches the record
	 * @param userName
	 * 		- the user name
	 * @param password
	 * 		- the password
	 */
	public boolean checkPassword(String userName, String password){
		try	
		{
			InputStream is = getAssets().open("Hells Hospital/passwords.txt");
			Scanner scanner = new Scanner(is);
			String line = ""; 
			
			ArrayList<String[]> all_info = new ArrayList<String[]>();
			while(scanner.hasNextLine()){ ///read all the lines in the file
				line = scanner.nextLine();
				String[] info = new String[3];	///split the line to a list
				info = line.split(",");
				all_info.add(info);
			}
			
			//checking whether the username and password
			for (String[] item : all_info){
				if (item[0].equals(userName) && item[1].equals(password)){
					userType = item[2];//if both username and password are correct
					name = item[0];
					return true;
				}
			}
			
		}
		catch(IOException e){//catch exception when there does not exist that file
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, e.getMessage(), duration);
			toast.show();//show the error on the screen
		}
		return false;
	}
	
}