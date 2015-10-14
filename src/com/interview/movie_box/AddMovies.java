package com.interview.movie_box;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddMovies extends Activity {

	//URLs to GET Genres from webservice and POST new movie details to server
	private static String getGenres_serverURL = "http://mobiletest.mazarin.lk/movieservice/rest/movies/genres";
	private static String postMovieDetails_serverURL = "http://mobiletest.mazarin.lk/movieservice/rest/movies";

	ArrayList<String> genreList;
	ArrayList<String> castList;
	private Button addCast,done;
	private EditText castTxt,nameTxt,yearTxt,scoreTxt;
	private LinearLayout container;
	private RadioGroup fictionRadGroup;
	private boolean isfiction;
	private String genre=null;
	String responseServer;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_movies);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		genreList=new ArrayList<String>();
		castList=new ArrayList<String>();
		addCast=(Button)findViewById(R.id.addcast);
		done=(Button)findViewById(R.id.donebtn);
		castTxt=(EditText)findViewById(R.id.castTxt);
		nameTxt=(EditText)findViewById(R.id.movieNametxt);
		yearTxt=(EditText)findViewById(R.id.movieYeartxt);
		scoreTxt=(EditText)findViewById(R.id.movieScoretxt);
		container = (LinearLayout)findViewById(R.id.container);
		fictionRadGroup = (RadioGroup) findViewById(R.id.fictionRadBtn);
		
		// check internet availability
        if(isConnected()){

    		new GetGenres().execute(getGenres_serverURL);
        }
        else{
        	Intent i = new Intent(AddMovies.this, MainDashboard.class);
        	startActivity(i);
        	AddMovies.this.finish();
            Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
        }

		addCast.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LayoutInflater layoutInflater = 
						(LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View addView = layoutInflater.inflate(R.layout.cast_item_list, null);
				final TextView textOut = (TextView)addView.findViewById(R.id.textout);
				if(!castTxt.getText().toString().equals(""))
				{
					textOut.setText(castTxt.getText().toString());
					castList.add(castTxt.getText().toString());
					//				System.out.println(""+castList);
					castTxt.setText("");

					Button buttonRemove = (Button)addView.findViewById(R.id.remove);
					buttonRemove.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							((LinearLayout)addView.getParent()).removeView(addView);

							castList.remove(textOut.getText().toString());
							//							System.out.println(""+castList);
						}});

					container.addView(addView);
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Please Enter A Name", Toast.LENGTH_LONG).show();
				}
				
			}
		});

		fictionRadGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// find which radio button is selected
				if(checkedId == R.id.truebtn) {
					isfiction=true;
					System.out.println(isfiction);
				} else if(checkedId == R.id.falsebtn) {
					isfiction=false;
					System.out.println(isfiction);
				} 
			}

		});

		done.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(nameTxt.getText().toString().equals("")||yearTxt.getText().toString().equals("")||scoreTxt.getText().toString().equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please Fill Empty Fields", Toast.LENGTH_LONG).show();
				}
				else
				{
					new SendData().execute();
				}
			}
		});
		
		scoreTxt.addTextChangedListener(new TextWatcher() {

	          public void afterTextChanged(Editable s) {}

	          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	          public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	  try {
					if(!(Integer.parseInt(scoreTxt.getText().toString())>=0 && Integer.parseInt(scoreTxt.getText().toString())<=10))
					  {
						  scoreTxt.setText("");
						  Toast.makeText(getApplicationContext(), "Invalid Score", Toast.LENGTH_LONG).show();
					  }
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	          }
	       });
	}

	private class GetGenres  extends AsyncTask<String, Void, Void> {

		private ProgressDialog Dialog = new ProgressDialog(AddMovies.this);

		protected void onPreExecute() {

			Dialog.setMessage("Loading...");
			Dialog.show();
		}

		protected Void doInBackground(String... urls) {
			try {

				HttpGet httppost = new HttpGet(urls[0]);
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity);
				JSONArray jarray = new JSONArray(data);

				for (int i = 0; i < jarray.length(); i++) {
					//                    	System.out.println(jarray.getString(i));
					genreList.add(jarray.getString(i).toString());

				}

			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void unused) {

			Dialog.dismiss();

			final Spinner genreSpinner=(Spinner)findViewById(R.id.genreSpinner);

			ArrayAdapter<String> genreAdapter = new ArrayAdapter<String>(AddMovies.this, android.R.layout.simple_spinner_dropdown_item, genreList);
			genreSpinner.setAdapter(genreAdapter);

			genreSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					String genreSpnrValue = genreSpinner.getSelectedItem().toString();

					//					Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
					genre=genreSpnrValue;
				}
				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
				}
			});
		}
	}

	//Send new movie data
	class SendData extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... voids) {
			InputStream inputStream = null;
			String result = "";
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(postMovieDetails_serverURL);

			try {

				String json = "";

				JSONObject jsonobj = new JSONObject();

				jsonobj.put("name", nameTxt.getText().toString());
				jsonobj.put("year", yearTxt.getText().toString());
				jsonobj.put("genre", genre);
				jsonobj.put("fiction", isfiction);
				jsonobj.put("cast", new JSONArray(castList));
				jsonobj.put("score", scoreTxt.getText().toString());

				json = jsonobj.toString();
				StringEntity se = new StringEntity(json);
				httppost.setEntity(se);

				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-type", "application/json");

				HttpResponse httpResponse = httpclient.execute(httppost);

				inputStream = httpResponse.getEntity().getContent();

				if(inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Error!";


			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			nameTxt.setText("");
			yearTxt.setText("");
			scoreTxt.setText("");
			Toast.makeText(getBaseContext(), "Succesfull!", Toast.LENGTH_LONG).show();
		}
	}

	private String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

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
		getMenuInflater().inflate(R.menu.add_movies, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_doneBtn) {
			if(nameTxt.getText().toString().equals("")||yearTxt.getText().toString().equals("")||scoreTxt.getText().toString().equals(""))
			{
				Toast.makeText(getApplicationContext(), "Please Fill Empty Fields", Toast.LENGTH_LONG).show();
			}
			else
			{
				new SendData().execute();
			}
			return true;
		}
		else if (id == android.R.id.home) {
			Intent intent = new Intent(AddMovies.this, MainDashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            AddMovies.this.finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}

	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{  
		if(keyCode==KeyEvent.KEYCODE_BACK)  
		{  
			Intent intent = new Intent(AddMovies.this, MainDashboard.class);
			startActivity(intent); 
			AddMovies.this.finish();
		}  
		return true;  
	} 
}
