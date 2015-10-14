package com.interview.movie_box;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GenresView extends Activity {
	
	//JSON Node Names
    private static final String TAG_GENRE = "genre";
    
    //GET Genres Webservice URL
    private static String getGenres_serverURL = "http://mobiletest.mazarin.lk/movieservice/rest/movies/genres";
    
	ListView list;
    TextView genre;
    ArrayList<HashMap<String, String>> genrelist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_genres_dashboard);
		
		//Up navigation on the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		genrelist = new ArrayList<HashMap<String, String>>();
        list=(ListView)findViewById(R.id.genresList);
        
        // check internet availability
        if(isConnected()){
        	//if internet available fetch data from the webservice
            new GetGenres().execute(getGenres_serverURL);
        }
        else{
        	Intent i = new Intent(GenresView.this, MainDashboard.class);
        	startActivity(i);
        	GenresView.this.finish();
            Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
        }

        //Gets the genre from the list view
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//            	Toast.makeText(GenresView.this, ""+genrelist.get(+position).get("genre"), Toast.LENGTH_SHORT).show();
            	
            	Intent i = new Intent(GenresView.this, ViewMovies.class);
            	i.putExtra("Genre", genrelist.get(+position).get("genre").toString());
            	startActivity(i);
            	GenresView.this.finish();
            }
        });
         
	}
	
	// Gets genres data from the webservice 
    private class GetGenres  extends AsyncTask<String, Void, Void> {
         
        private ProgressDialog Dialog = new ProgressDialog(GenresView.this);

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
                    	HashMap<String, String> map = new HashMap<String, String>();
                    	 
                        map.put(TAG_GENRE, jarray.getString(i));
     
                        genrelist.add(map);
     
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
            
            ListAdapter adapter = new SimpleAdapter(GenresView.this, genrelist,
                    R.layout.genre_list,
                    new String[] { TAG_GENRE }, new int[] {
                            R.id.movieGenre});

            list.setAdapter(adapter);
            
        }
         
    }
    
    //Method to check the internet conection
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;    
    }

    //Actiobar Buttons
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
			Intent intent = new Intent(GenresView.this, MainDashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            GenresView.this.finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	//Back button pressed
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{  
		if(keyCode==KeyEvent.KEYCODE_BACK)  
		{  
			Intent intent = new Intent(GenresView.this, MainDashboard.class);
			startActivity(intent); 
			GenresView.this.finish();
		}  
		return true;  
	} 
}
