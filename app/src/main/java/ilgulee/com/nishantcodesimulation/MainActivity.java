package ilgulee.com.nishantcodesimulation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ConnectionStatusListener{
    private static final String TAG = "MainActivity";

    private static final String UPLOAD_VIDEO_URL = "upload_video_url";

    //Identifier for the video capture request
    public static final int REQUEST_VIDEO_CAPTURE = 1;

    //Identifier for the permission request
    public static final int PERMISSION_READ_WRITE_STORAGE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // In an actual app, you'd want to request a permission when the user performs an action
        // that requires that permission.
        getPermissionToWriteExternalStorage();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Simulate clicking phone camera on navigation bar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                dispatchTakeVideoIntent();
            }
        });
    }

    // Open camera intent to record video
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            String fileUri = FileUtils.getPath(MainActivity.this, videoUri);
            Log.d(TAG, "CapturedVideoUri: " + fileUri);

            //SessionUserInfo should be set depending on developer's SSH Server environment
            SessionUserInfo sessionUserInfo = new SessionUserInfo("wendy", "192.168.2.10", "lig219", 22);
            SessionController.getSessionController().setUserInfo(sessionUserInfo);
            SessionController.getSessionController().setVideoUrl(fileUri);
            SessionController.getSessionController().setConnectionStatusListener(this);
            SessionController.getSessionController().connect();
        }
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "SFTP Disconnected");
    }

    @Override
    public void onConnected(final String videoUrl) {
        Log.d(TAG, "SFTP Connected");
        // sftp the file
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SftpProgressDialog progressDialog = new SftpProgressDialog(MainActivity.this, 0);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                File[] arr = {new File(videoUrl)};
                SessionController.getSessionController().uploadFiles(arr, progressDialog);
            }
        });
    }

    private void getPermissionToWriteExternalStorage() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_READ_WRITE_STORAGE);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original permission request
        if (requestCode == PERMISSION_READ_WRITE_STORAGE) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Write external storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
