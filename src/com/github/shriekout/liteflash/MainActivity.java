package com.github.shriekout.liteflash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	int mode = 1;	// mode1:OFF	mode2:ON
	
	Camera camera = null;
	Parameters parameters;
	
	ImageButton switch_button;
	ImageView imageViewCompass;
	TextView tvHeading;
	
	private float currentDegree = 0f;
	
	private SensorManager mSensorManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int screenWidth;
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		
		switch_button = (ImageButton) findViewById(R.id.switch_button);
		imageViewCompass = (ImageView) findViewById(R.id.imageViewCompass);
		tvHeading = (TextView) findViewById(R.id.tvHeading);

		ViewGroup.LayoutParams switch_params = switch_button.getLayoutParams();
		switch_params.width = screenWidth / 2;
		switch_params.height = switch_params.width;
		
		switch_button.setLayoutParams(switch_params);

		switch_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onoff();
			}
		});
		
		ViewGroup.LayoutParams compass_params = imageViewCompass.getLayoutParams();
		compass_params.width = screenWidth / 3;
		compass_params.height = compass_params.width;
		
		imageViewCompass.setLayoutParams(compass_params);
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		ViewGroup.LayoutParams tvHeading_params = tvHeading.getLayoutParams();
		tvHeading_params.width = compass_params.width;
		tvHeading_params.height = tvHeading_params.width;
		tvHeading.setLayoutParams(compass_params);
		
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
		switch_button.setImageResource(R.drawable.on_power);
		mode = 1;
	}
	
	private void flashOff() {
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(parameters);
		camera.release();
		camera = null;
		switch_button.setImageResource(R.drawable.off_power);
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
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		int degree = (int) Math.round(event.values[0]);
		
		tvHeading.setText(Integer.toString(degree));
		
		RotateAnimation ra = new RotateAnimation(
				currentDegree,
				-degree,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		
		ra.setDuration(210);
		
		ra.setFillAfter(true);
		
		imageViewCompass.startAnimation(ra);
		currentDegree = -degree;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	protected void showAbout() {
		String version = "";
		String name_ver;
		
		TextView tv = new TextView(this);
		tv.setText(Html.fromHtml(getString(R.string.app_descrip)));
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		
		try {
			PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = i.versionName;
		} catch(NameNotFoundException e) {}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		name_ver = getString(R.string.app_name) + " " + version;
		builder.setTitle(name_ver);
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
	protected void onResume() {
		super.onResume();
		
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
				SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		if (camera != null) {
			flashOff();
		}
		
		mSensorManager.unregisterListener(this);
	}
}
