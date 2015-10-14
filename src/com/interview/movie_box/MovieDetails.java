package com.interview.movie_box;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MovieDetails extends Activity {
	
	private TextView NameTxt,CastTxt,FictionTxt,GenreTxt,YearTxt,ScoreTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_details);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		NameTxt=(TextView)findViewById(R.id.nametxt);
		CastTxt=(TextView)findViewById(R.id.casttxt);
		FictionTxt=(TextView)findViewById(R.id.fictiontxt);
		GenreTxt=(TextView)findViewById(R.id.genretxt);
		YearTxt=(TextView)findViewById(R.id.yeartxt);
		ScoreTxt=(TextView)findViewById(R.id.scoretxt);
		
		// check internet availability
        if(isConnected()){
    		//Get parameters sent from the ViewMovies activity
    		NameTxt.setText(getIntent().getStringExtra("Name"));
    		CastTxt.setText(getIntent().getStringExtra("Cast"));
    		FictionTxt.setText(getIntent().getStringExtra("Fiction"));
    		GenreTxt.setText(getIntent().getStringExtra("Genre"));
    		YearTxt.setText(getIntent().getStringExtra("Year"));
    		ScoreTxt.setText(getIntent().getStringExtra("Score"));
        }
        else{
        	Intent i = new Intent(MovieDetails.this, MainDashboard.class);
        	startActivity(i);
        	MovieDetails.this.finish();
            Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
        }
		
	}

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;    
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.movie_details, menu);
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
		else if (id == android.R.id.home) {
			Intent intent = new Intent(MovieDetails.this, ViewMovies.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("Genre", GenreTxt.getText().toString());
            startActivity(intent);
            MovieDetails.this.finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	

	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{  
		if(keyCode==KeyEvent.KEYCODE_BACK)  
		{  
			Intent intent = new Intent(MovieDetails.this, ViewMovies.class);
			intent.putExtra("Genre", GenreTxt.getText().toString());
			startActivity(intent); 
			MovieDetails.this.finish();
		}  
		return true;  
	} 
}
