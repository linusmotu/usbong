package usbong.android.community;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.io.File;

import usbong.android.R;
import usbong.android.utils.UsbongUtils;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SingleItemView extends YouTubeBaseActivity implements 
		YouTubePlayer.OnInitializedListener,
		YouTubePlayer.OnFullscreenListener {
	
	@SuppressWarnings("unused")
	private final static String TAG = "usbong.usbongcommunitydraft.SingleItemView";
	private String youtubeLink = "";
	FitsObject fitsObject = null;
	private DownloadTreeAsync downloadTask;
	private File savedTree;
	// YouTube player view
    private YouTubePlayerView youTubeView;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private boolean fullScreen = false;
    private YouTubePlayer mPlayer = null;
    private View otherViews;
    private LinearLayout baseLayout;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.singleitemview);
		
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Constants.YOUTUBE_API_KEY, this);

        baseLayout = (LinearLayout) findViewById(R.id.layout);
        otherViews = findViewById(R.id.other_views);
		ImageView imgphone = (ImageView) findViewById(R.id.icon);
		TextView uploader = (TextView) findViewById(R.id.uploadername);
		TextView fileName = (TextView) findViewById(R.id.filename);
		TextView downloadCount = (TextView) findViewById(R.id.downloadcount);
		TextView description = (TextView) findViewById(R.id.description);
		ImageView rating = (ImageView) findViewById(R.id.ratings);
		final Button download = (Button) findViewById(R.id.download);

		if(fitsObject == null) {
			Intent i = getIntent();
			// Get the intent from ListViewAdapter
			fitsObject = i.getExtras().getParcelable(Constants.BUNDLE);
		}
		//String icon_url = "http://" + Constants.HOSTNAME + "/usbong/icons/" + fitsObject.getICON();

		youtubeLink = UsbongUtils.parseYouTubeLink(fitsObject.getYOUTUBELINK());
	    
    	File folder = new File(Environment.getExternalStorageDirectory() + "/usbong");
    	if(!folder.exists())
    		folder.mkdir();
    	
		savedTree = new File(Environment.getExternalStorageDirectory().getPath()
        		+ "/usbong/usbong_trees/"
        		+ fitsObject.getFILEPATH());
		
		if(savedTree.exists()) {
			download.setText("Open Tree");
		} 
		
		fileName.setText(fitsObject.getFILENAME());
		uploader.setText("By: " + fitsObject.getUPLOADER());
		downloadCount.setText(fitsObject.getDOWNLOADCOUNT() + "");
		description.setText(fitsObject.getDESCRIPTION());
		
		switch(fitsObject.getRATING()) {
		default:
		case 0:
		case 1:
			rating.setImageResource(R.drawable.one);
			break;
		case 2:
			rating.setImageResource(R.drawable.two);
			break;
		case 3:
			rating.setImageResource(R.drawable.three);
			break;
		case 4:
			rating.setImageResource(R.drawable.four);
			break;
		case 5:
			rating.setImageResource(R.drawable.five);
			break;
		}
		
		ProgressDialog mProgressDialog;

		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Downloading: " + fitsObject.getFILEPATH());
		mProgressDialog.setTitle("Saving trees...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		downloadTask = new DownloadTreeAsync(this,  mProgressDialog);
		download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(savedTree.exists()) {
	            	Intent i = new Intent(SingleItemView.this, UsbongTreeDisplay.class);
	            	i.putExtra(Constants.UTREE_KEY, savedTree.getAbsolutePath());
	            	startActivity(i);
				} else {
//					Toast.makeText(SingleItemView.this, "Downloading: " + fitsObject.getFILEPATH() + " tree", Toast.LENGTH_SHORT).show();
					downloadTask.execute(fitsObject.getFILEPATH());
					download.setText("Open Tree");
				}
			}
		});
		
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    @Override
		    public void onCancel(DialogInterface dialog) {
		        downloadTask.cancel(true);
		    }
		});
 
		// Load image into the ImageView
//		imageLoader.DisplayImage(phone, imgphone);
		
		//Using UIL
		if(!ImageLoader.getInstance().isInited()) {
			UsbongUtils.initDisplayAndConfigOfUIL(this);
		}
		//http://stackoverflow.com/questions/2068344/how-do-i-get-a-youtube-video-thumbnail-from-the-youtube-api
		String icon_url = "http://img.youtube.com/vi/" + youtubeLink + "/hqdefault.jpg";
		ImageLoader.getInstance().displayImage(icon_url, imgphone);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
  
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("fitsObject", fitsObject);
		savedInstanceState.putBoolean("fullScreen", fullScreen);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		fitsObject = savedInstanceState.getParcelable("fitsObject");
		fullScreen = savedInstanceState.getBoolean("fullScreen");
	}

	@Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
            YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
 
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
            YouTubePlayer player, boolean wasRestored) {
    	this.mPlayer = player;
//		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
    	player.setShowFullscreenButton(false);
		player.setOnFullscreenListener(this);

        if (!wasRestored) {   	
            player.cueVideo(youtubeLink);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Constants.YOUTUBE_API_KEY, this);
        }
    }
 
    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }
	
	@Override
	public void onBackPressed() {
	    if (fullScreen){
	    	mPlayer.setFullscreen(false);
	    } else{
	        super.onBackPressed();
	    }
	}

	@Override
	public void onFullscreen(boolean _fullScreen) {
		fullScreen = _fullScreen;
		doLayout();
	}
	
	//TODO: Fix this! Solved for now by removing fullscreen button on youtube video
	  private void doLayout() {
		    LinearLayout.LayoutParams playerParams =
		        (LinearLayout.LayoutParams) youTubeView.getLayoutParams();
		    if (fullScreen) {
		      // When in fullscreen, the visibility of all other views than the player should be set to
		      // GONE and the player should be laid out across the whole screen.
		      playerParams.width = LayoutParams.MATCH_PARENT;
		      playerParams.height = LayoutParams.MATCH_PARENT;

		      otherViews.setVisibility(View.GONE);
		    } else {
		      // This layout is up to you - this is just a simple example (vertically stacked boxes in
		      // portrait, horizontally stacked in landscape).
		      otherViews.setVisibility(View.VISIBLE);
		      ViewGroup.LayoutParams otherViewsParams = otherViews.getLayoutParams();
		      if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		        playerParams.width = otherViewsParams.width = 0;
		        playerParams.height = WRAP_CONTENT;
		        otherViewsParams.height = MATCH_PARENT;
		        playerParams.weight = 1;
		        baseLayout.setOrientation(LinearLayout.VERTICAL);
		      } else {
		        playerParams.width = otherViewsParams.width = MATCH_PARENT;
		        playerParams.height = WRAP_CONTENT;
//		        playerParams.weight = 0;
		        otherViewsParams.height = WRAP_CONTENT;
		        baseLayout.setOrientation(LinearLayout.VERTICAL);
		      }
		    }
		  }
}