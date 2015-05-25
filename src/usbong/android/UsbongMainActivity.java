/*
 * Copyright 2012 Michael Syson
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package usbong.android;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import usbong.android.community.FitsListDisplay;
import usbong.android.utils.UsbongUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*
 * This is Usbong's Main Menu activity. 
 */
public class UsbongMainActivity extends Activity 
{	
	private Button startButton;
	private Button instructionsButton;
	private Button aboutButton;
	private Button communityButton;
	private Button settingsButton;
	private Button exitButton;
			
	private static UsbongMainActivity instance;
				
	public static String timeStamp;
	
//	private static Date startTime;	
	
	protected UsbongDecisionTreeEngineActivity myUsbongDecisionTreeEngineActivity;
	protected SettingsActivity mySettingsActivity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
//    	if (instance==null) { //comment this out, since the app seems to retain the instance even after we do a finish to GameActivity to close the app...
	        setContentView(R.layout.main);	   
	        instance = this;
//	    	startTime = new Date();
	    	
	        reset();
	        initMainMenuScreen();
			initUniversalImageLoader();
    }

	private void initUniversalImageLoader() {
		//TODO: Check how this works efficiently
		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.loading)
				.showImageOnFail(R.drawable.loading)
				.showImageOnLoading(R.drawable.loading)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
		config.memoryCache(new WeakMemoryCache());
		config.defaultDisplayImageOptions(options);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		ImageLoader.getInstance().init(config.build());
	}
    
    public static UsbongMainActivity getInstance() {
    	return instance;
    }
    
    /*
     * Initialize this activity
     */
    public void init()
    {    	
    }
    
    public void initMainMenuScreen()
    {
    	startButton = (Button)findViewById(R.id.start_button);
    	instructionsButton = (Button)findViewById(R.id.instructions_button);
    	aboutButton = (Button)findViewById(R.id.about_button);
    	communityButton = (Button)findViewById(R.id.community_button);
    	settingsButton = (Button)findViewById(R.id.settings_button);
    	exitButton = (Button)findViewById(R.id.exit_button);    	

		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				myUsbongDecisionTreeEngineActivity = new UsbongDecisionTreeEngineActivity();
				reset(); //generate new timestamp
				Intent toUsbongDecisionTreeEngineActivityIntent = new Intent().setClass(UsbongMainActivity.getInstance(), UsbongDecisionTreeEngineActivity.class);
				toUsbongDecisionTreeEngineActivityIntent.putExtra("currScreen","0"); //make currScreen=0; meaning very first screen				
				startActivity(toUsbongDecisionTreeEngineActivityIntent);				
			}
    	});


    	instructionsButton.setOnClickListener(new OnClickListener() {
    		@Override
			public void onClick(View v) {				
/*
    			TextView tv = (TextView) UsbongUtils.applyTagsInView(UsbongMainActivity.getInstance(),
    	    														 new TextView(UsbongMainActivity.getInstance()), 
    	    														 UsbongUtils.IS_TEXTVIEW,     	    														 
    	    														 UsbongUtils.readTextFileInAssetsFolder(UsbongMainActivity.getInstance(),"instructions.txt")); //don't add a '/', otherwise the file would not be found    	    	
*/
/*
    			TextView tv = new TextView(UsbongMainActivity.getInstance());
    			tv.setText(UsbongUtils.readTextFileInAssetsFolder(UsbongMainActivity.this,"instructions.txt"));
    			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -2);
    			LinearLayout layout = new LinearLayout(new Context(), params);
    			layout.addView(tv, params);
    			tv.setBackgroundResource(R.layout.dialog);
*/    			
//    	    	tv.setTextSize((getResources().getDimension(R.dimen.textsize)));

    	    	AlertDialog.Builder prompt = new AlertDialog.Builder(UsbongMainActivity.this);
    	    	prompt.setTitle("Instructions");
//    	    	prompt.setView(getLayoutInflater().inflate(R.layout.dialog, null));
/*
    	    	prompt.setView(layout); 
*/    	    	
//				prompt.setView(tv); 
				prompt.setMessage(UsbongUtils.readTextFileInAssetsFolder(UsbongMainActivity.this,"instructions.txt")); //don't add a '/', otherwise the file would not be found
    	    	prompt.setInverseBackgroundForced(true);
				prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				AlertDialog alert = prompt.create();
				alert.show();
			}
    	});

    	aboutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
/*
				TextView tv = (TextView) UsbongUtils.applyTagsInView(UsbongMainActivity.getInstance(),
						 new TextView(UsbongMainActivity.getInstance()), 
						 UsbongUtils.IS_TEXTVIEW,     	    														 
						 UsbongUtils.readTextFileInAssetsFolder(UsbongMainActivity.getInstance(),"credits.txt")); //don't add a '/', otherwise the file would not be found    	    	
    	    	tv.setTextSize((getResources().getDimension(R.dimen.textsize)));
*/
				AlertDialog.Builder prompt = new AlertDialog.Builder(UsbongMainActivity.this);
				prompt.setTitle("About");
//				prompt.setView(tv);
				prompt.setMessage(UsbongUtils.readTextFileInAssetsFolder(UsbongMainActivity.this,"credits.txt")); //don't add a '/', otherwise the file would not be found
				prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				prompt.show();
			}
    	});

    	communityButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
            	Intent i = new Intent(UsbongMainActivity.this, FitsListDisplay.class);
            	startActivity(i);		
			}
    	});
    	
    	settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
//				mySettingsActivity = new SettingsActivity();
				
				Intent toSettingsActivityIntent = new Intent().setClass(UsbongMainActivity.getInstance(), SettingsActivity.class);
				startActivity(toSettingsActivityIntent);				
			}
    	});

    	exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(UsbongMainActivity.this).setTitle("Exiting application...")
				.setMessage("Are you sure you want to exit application?")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						finish();
					}
				}).show();
			}
    	});
    }
  
    public void reset() {
    	UsbongUtils.generateDateTimeStamp(); //create a new timestamp for this "New Entry"
    }
/*
    private void showStatusDialog(String status)
	{
		AlertDialog.Builder prompt = new AlertDialog.Builder(UsbongMainActivity.this);
		prompt.setTitle("Notification");
		prompt.setMessage(status);
		prompt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		prompt.show();
	}
*/	
}