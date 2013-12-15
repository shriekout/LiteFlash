package com.github.shriekout.liteflash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	int mode = 1;	// mode1:OFF	mode2:ON
	
	Camera camera = null;
	Parameters parameters;
	
	Button switch_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		switch_button = (Button) findViewById(R.id.switch_button);

		switch_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onoff();
			}
		});
		
		flashOn();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void onoff() {
		if (mode == 1) {
			flashOff();
		} else {
			flashOn();
		}
	}
	
	private void flashOn() {
		camera = Camera.open();
		parameters = camera.getParameters();
		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(parameters);
	    
		switch_button.setText("ON");
		switch_button.setTextColor(Color.parseColor("#00FF00"));
		mode = 1;
	}
	
	private void flashOff() {
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(parameters);
		camera.release();
		camera = null;
		
		switch_button.setText("OFF");
		switch_button.setTextColor(Color.parseColor("#006633"));
		mode = 0;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int curId = item.getItemId();
		
		switch (curId) {
			case R.id.action_about:
				showAbout();
				break;
			
			default:
				break;
		}
		
		return true;
	}

	protected void showAbout() {
		TextView tv = new TextView(this);
		tv.setText(Html.fromHtml(getString(R.string.app_descrip)));
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		builder.setView(tv);
		tv.setPadding(20, 20, 20, 20);
		
		builder.setPositiveButton(R.string.about_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create();
		builder.show();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		if (camera != null) {
			flashOff();
		}
	}
}
