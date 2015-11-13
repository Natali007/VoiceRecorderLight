package com.mysite.voicerecorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	Button button_record; 
	Button button_play;
	Button button_stop;
	TextView caption;
	Chronometer myTimer;
	private MediaRecorder myRecorder;
	private String FileName=null;
	private String  calend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//set the date and time format
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		
		//get the system date and time
		Calendar c = Calendar.getInstance();
		
		//get data in established format
		calend = dateFormat.format(c.getTime());
		
		button_record=(Button)findViewById(R.id.button_rec);
		button_play=(Button)findViewById(R.id.button_play);
		button_stop=(Button)findViewById(R.id.button_stop);
		caption=(TextView)findViewById(R.id.caption);
		myTimer=(Chronometer)findViewById(R.id.timer);
		
		button_play.setEnabled(false);
		button_stop.setEnabled(false);
		
		

		/*Checking creating a folder or the success its existence */		
		if(!createDirIfNotExists("Records/")){
				Toast.makeText(getApplicationContext(), "Error with directory!", Toast.LENGTH_SHORT).show();
				button_play.setEnabled(false);
				button_stop.setEnabled(false);
				button_record.setEnabled(false);
			} 
		
		//listener the record button
		button_record.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//watching an error
				try {
					
					//call the method to create a new media object, preparation and launch voice recorder
					startRecording();
					myTimer.setBase(SystemClock.elapsedRealtime());
					myRecorder.prepare();
					myRecorder.start();
					myTimer.start();
				} 
				
				//processing error
				catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				button_record.setEnabled(false);
				button_stop.setEnabled(true);
				button_play.setEnabled(false);
				caption.setText(R.string.text_rec);
			}
		});
		
		//listener the stop button
		button_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//stop, free the resources associated with the object and indicates its absence 
				myTimer.stop();
				myRecorder.stop();
				myRecorder.release(); 
				myRecorder = null;
				
				button_play.setEnabled(true);
				button_stop.setEnabled(false);
				button_record.setEnabled(true);
				caption.setText(R.string.text_stop);
			}
		});
		
		//listener the play button
		button_play.setOnClickListener(new View.OnClickListener() {
			@Override
			
			//lists the types of exceptions
			public void onClick(View v)  throws IllegalArgumentException, SecurityException, IllegalStateException{
			
				//watching an error
				MediaPlayer mp = new MediaPlayer();
			try {
				
				//data source
				mp.setDataSource(FileName);
			}
			
			//processing error
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mp.prepare();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				//launch 
			    myTimer.setBase(SystemClock.elapsedRealtime());
				mp.start();
				myTimer.start();
				caption.setText(R.string.text_play);
				button_play.setEnabled(false);
				button_stop.setEnabled(false);
				button_record.setEnabled(false);
				
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						myTimer.stop();
						mp.stop();
						button_stop.setEnabled(false);
						button_record.setEnabled(true);
						button_play.setEnabled(true);
					}
				});
			}
		});		
		
		
	}
	
		private void startRecording(){
			
			//assign new name for audio 
			FileName=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Records"+"/rec_"+calend+".3gp";
			
			//create the object
			myRecorder = new MediaRecorder();
			
			//source for record 
			myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			
			//output file format
			myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			
			//audio encoder to be used for recording
			myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			
			//path to the output file
			myRecorder.setOutputFile(FileName);
		}
	
	//method for checking creation / existence of directory
	public static boolean createDirIfNotExists(String path) {
	    boolean ret = true;
	    // create an object of class File
	    File FoldRec = new File(Environment.getExternalStorageDirectory(), path);
	    //if directory there is not and cannot be created then return false
	    if (!FoldRec.exists()) {
	        if (!FoldRec.mkdirs()) {
	            ret = false;
	        }
	    }
	    return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
