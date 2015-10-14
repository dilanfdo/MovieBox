package com.interview.movie_box;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
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

public class ViewMovies extends Activity {
	
	//JSON Node Names
    private static final String TAG_NAME = "name";
    private static final String TAG_CAST = "cast";
    private static final String TAG_FICTION = "fiction";
    private static final String TAG_GENRE = "genre";
    private static final String TAG_SCORE = "score";
    private static final String TAG_YEAR= "year";
    
	//GET Movies Webservice URL
    private static String getMovies_serverURL = "http://mobiletest.mazarin.lk/movieservice/rest/movies";

	private String mMovieGenre;
	ListView list;
    TextView genre,selected_movie;
    ArrayList<HashMap<String, String>> movielist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_movies);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mMovieGenre= getIntent().getStringExtra("Genre");//Get the parameters sent from Genres Activity
		movielist = new ArrayList<HashMap<String, String>>();
        list=(ListView)findViewById(R.id.movieList);
		
		// check internet availability
        if(isConnected()){
        	new GetGenres().execute(getMovies_serverURL);
        }
        else{
        	Intent i = new Intent(ViewMovies.this, MainDashboard.class);
        	startActivity(i);
        	ViewMovies.this.finish();
            Toast.makeText(getApplicationContext(), "Internet Not Available", Toast.LENGTH_LONG).show();
        }
        
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//            	Toast.makeText(ViewMovies.this, ""+movielist.get(+position).get("genre"), Toast.LENGTH_SHORT).show();
            	
            	Intent i = new Intent(ViewMovies.this, MovieDetails.class);
            	i.putExtra("Name", movielist.get(+position).get(TAG_NAME).toString());
            	i.putExtra("Cast", movielist.get(+position).get(TAG_CAST).toString());
            	i.putExtra("Fiction", movielist.get(+position).get(TAG_FICTION).toString());
            	i.putExtra("Genre", movielist.get(+position).get(TAG_GENRE).toString());
            	i.putExtra("Year", movielist.get(+position).get(TAG_YEAR).toString());
            	i.putExtra("Score", movielist.get(+position).get(TAG_SCORE).toString());
            	startActivity(i);
            	ViewMovies.this.finish();
            }
        });
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
		getMenuInflater().inflate(R.menu.view_movies, menu);
		return true;
	}
	
	// Class with extends AsyncTask class
    private class GetGenres  extends AsyncTask<String, Void, Void> {
         
        private ProgressDialog Dialog = new ProgressDialog(ViewMovies.this);
         
        protected void onPreExecute() {

        	Dialog.setMessage("Loading..");
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
                    	
                    	JSONObject obj=new JSONObject(""+jarray.getJSONObject(i));
                    	
                    	String genre=obj.getString("genre");
                    	
                    	if(genre.equals(mMovieGenre))
                    	{
                    		HashMap<String, String> map = new HashMap<String, String>();

                    		map.put(TAG_NAME, obj.getString("name"));

                    		JSONArray cast_array = obj.getJSONArray("cast");
                    		ArrayList<String> castList = new ArrayList<String>();
                    		for (int j = 0; j < cast_array.length(); j++) {
                    			//                        		System.out.println(cast_array.getString(j));
                    			castList.add(cast_array.getString(j));
                    		}

                    		StringBuilder builder = new StringBuilder();
                    		for (String value : castList) {
                    			builder.append(value).append("\n");
                    		}
                    		String castString = builder.toString();

                    		map.put(TAG_CAST, castString);
                    		map.put(TAG_FICTION, obj.getString("fiction"));
                    		map.put(TAG_GENRE, obj.getString("genre"));
                    		map.put(TAG_YEAR, obj.getString("year"));
                    		map.put(TAG_SCORE, obj.getString("score"));

                    		movielist.add(map);
                    	}
                    	else
                    	{
                    		
                    	}
                    	
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
            // Close progress dialog
            Dialog.dismiss();
             
            ListAdapter adapter = new SimpleAdapter(ViewMovies.this, movielist,
                    R.layout.movies_list,
                    new String[] { TAG_NAME , TAG_CAST , TAG_FICTION , TAG_GENRE , TAG_YEAR , TAG_SCORE }, new int[] {
                            R.id.movieName,R.id.movieCast,R.id.movieFiction,R.id.movieGenre,R.id.movieYear,R.id.movieScore});

            list.setAdapter(adapter);
            
        }
         
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
//            NavUtils.navigateUpFromSameTask(ViewMovies.this);
			Intent intent = new Intent(ViewMovies.this, GenresView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            ViewMovies.this.finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{  
		if(keyCode==KeyEvent.KEYCODE_BACK)  
		{  
			Intent intent = new Intent(ViewMovies.this, GenresView.class);
			startActivity(intent); 
			ViewMovies.this.finish();
		}  
		return true;  
	} 
}
