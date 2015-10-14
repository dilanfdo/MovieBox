package com.interview.movie_box;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainDashboard extends Activity {

	private Button mViewMoviesBtn,mAddMoviesBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_dashboard);
		
		mViewMoviesBtn=(Button)findViewById(R.id.viewbtn);
		mAddMoviesBtn=(Button)findViewById(R.id.addbtn);
		
//		SelectionPopup();
		
		//View movies button click
		mViewMoviesBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent_view=new Intent(MainDashboard.this,GenresView.class);
				startActivity(intent_view);
				MainDashboard.this.finish();
			}
		});
		
		//Add movies button click
		mAddMoviesBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent_add=new Intent(MainDashboard.this,AddMovies.class);
				startActivity(intent_add);
				MainDashboard.this.finish();
			}
		});
	}
	
//	public void SelectionPopup()
//	{
//		final Dialog d = new Dialog(MainDashboard.this);
//		d.setCancelable(false);
//		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		d.setContentView(R.layout.selection_pop_up);// custom layour for dialog.
//
//		d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//		Button mBtnviewmovies = (Button) d.findViewById(R.id.btnViewMovies);
//		Button mBtnaddmovies = (Button) d.findViewById(R.id.btnAddMovies);
//		d.show();
//
//		mBtnviewmovies.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				d.dismiss();
//				
//				Intent intent_view=new Intent(MainDashboard.this,GenresView.class);
//				startActivity(intent_view);
//				MainDashboard.this.finish();
//
//			}
//		});
//
//		mBtnaddmovies.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				d.dismiss();
//				
//				Intent intent_add=new Intent(MainDashboard.this,AddMovies.class);
//				startActivity(intent_add);
//				MainDashboard.this.finish();
//			}
//		});
//
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_dashboard, menu);
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
	
	//Exit confirmation on back button click
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{  
		if(keyCode==KeyEvent.KEYCODE_BACK)  
		{  
			 new AlertDialog.Builder(MainDashboard.this)
	          .setTitle("Exit App")
	          .setMessage("Are You Sure You Want To Exit?")
	          .setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
	          .setPositiveButton("Exit", new OnClickListener() {
	              @Override
	              public void onClick(DialogInterface dialog, int which) {
	            	// TODO Auto-generated method stub
	            	  finish();
	              }
	          }).create().show();   
		}  
		return true;  
	} 
}
