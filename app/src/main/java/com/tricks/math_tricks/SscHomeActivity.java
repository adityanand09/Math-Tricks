package com.tricks.math_tricks;

import static com.tricks.math_tricks.App.EDITOR;
import static com.tricks.math_tricks.App.IS_FIRST_TIME;
import static com.tricks.math_tricks.App.IS_UPDATE_AVAILABLE;
import static com.tricks.math_tricks.App.NETWORK_INFO;
import static com.tricks.math_tricks.App.NEW_UPDATE_AVAILABLE;
import static com.tricks.math_tricks.App.NO_NEW_UPDATE_OR_NETWORK_ERROR;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tricks.math_tricks.Downloads.ServiceMain;
import com.tricks.math_tricks.contentStructure.SscChapObject;
import com.tricks.math_tricks.contentStructure.SscTopicObject;
import com.tricks.math_tricks.fragmentItems.ContentFragmentPdf;
import com.tricks.math_tricks.fragmentItems.ItemFragment;
import com.tricks.math_tricks.fragmentItems.SubItemFragment;
import com.tricks.math_tricks.listeners.DownloadCallback;
import com.tricks.math_tricks.listeners.OnChapFragmentInteractionListener;
import com.tricks.math_tricks.listeners.OnSucTopicFragmentInteractionListener;
import com.tricks.math_tricks.notes.Notes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SscHomeActivity extends AppCompatActivity implements OnChapFragmentInteractionListener, OnSucTopicFragmentInteractionListener, DownloadCallback {

    private static final String TAG = "SscHomeActivity";
    private int clicks = 0;

    ProgressDialog progressDialog = null;
    Intent intent;
    Intent updateIntent;

    private ServiceMain serviceMain;
    private boolean isBounded = false;
    private File main_folder;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceMain.DownloadBinder binder = (ServiceMain.DownloadBinder)service;
            serviceMain = binder.getService();
            serviceMain.setCallback(SscHomeActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private boolean isInProgress = false;

    private boolean isAdShowing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ssc_home_activity);
        Log.d(TAG, "onCreate");
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }, 0, 3, TimeUnit.MINUTES);

        main_folder = new File(getFilesDir() + "/my_files");
        if (savedInstanceState == null) {

            intent = new Intent(SscHomeActivity.this, ServiceMain.class);
            updateIntent = getIntent();
            int isUpdateAvailable = updateIntent.getIntExtra(IS_UPDATE_AVAILABLE, NO_NEW_UPDATE_OR_NETWORK_ERROR);
            if (!isBounded) {
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                isBounded = true;
                Log.d(TAG, "onCreate: service bounded");
            }

            if (IS_FIRST_TIME) {
                showProgressDialog();
                if (NETWORK_INFO != null && NETWORK_INFO.isConnected()) {
                    Log.d(TAG, "onCreate");
                    Toast.makeText(getApplicationContext(), "Downloading may take some time", Toast.LENGTH_SHORT).show();
                    startService(intent);
                } else {
                    cancelProgressDialog();
                    Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                if (isUpdateAvailable == NEW_UPDATE_AVAILABLE && NETWORK_INFO != null && NETWORK_INFO.isConnected()) {
                    CheckUpdate();
                } else {
                    DownloadComplete(-1);
                }
            }
        }
        else {
            isInProgress = savedInstanceState.getBoolean("is_progress_showing");
            if (isInProgress){
                showProgressDialog();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_progress_showing", isInProgress);
    }

    public void CheckUpdate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("New contents has been uploaded");
        builder.setCancelable(false);
        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog();
                EDITOR.putBoolean("IS_FIRST_TIME", true);
                EDITOR.putInt("UPDATE_VERSION", -1);
                EDITOR.commit();
                Toast.makeText(getApplicationContext(), "Downloading may take some time", Toast.LENGTH_SHORT).show();
                startService(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownloadComplete(-1);
            }
        });
        AlertDialog updateDialog = builder.create();
        updateDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (isBounded) {
            unbindService(mConnection);
            isBounded = false;
            Log.d(TAG, "onStop: service unbounded");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }

    public List<SscChapObject> GetChapList(){
        List<String> chapters = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<Drawable> icons = new ArrayList<>();
        List<SscChapObject> chapList = new ArrayList<>();
        int count=0;

        for(File file : main_folder.listFiles()){
            if (file.isDirectory()) {
                chapters.add(file.getName());
                count++;
            }
        }
        java.util.Collections.sort(chapters);
        for (int i=0; i<count; i++)
            for (File file : (new File(main_folder + "/" + chapters.get(i))).listFiles()){
                if (file.getName().equals("description")){
                    description.add(ReadText(file.getAbsolutePath() + "/description.txt"));
                }else if (file.getName().equals("icon")){
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath() + "/icon.png");
                    icons.add(new BitmapDrawable(Resources.getSystem(), bitmap));
                }
            }

        try {
            for (int i = 0; i < count; i++)
                chapList.add(new SscChapObject(
                        icons.get(i),
                        chapters.get(i),
                        description.get(i)));
        }catch (Exception e){

        }

        cancelProgressDialog();
        return chapList;
    }

    public String ReadText(String path) {
        StringBuilder json = new StringBuilder();
        String text = null;
        FileReader fileReader;
        LineNumberReader lineNumberReader;
        try {
            fileReader = new FileReader(new File(path));
            lineNumberReader = new LineNumberReader(fileReader);
            while ((text = lineNumberReader.readLine()) != null){
                json.append(text);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public void openTopic(List<SscChapObject> mList){
        ItemFragment itemFragment = new ItemFragment(mList);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.add(R.id.main_frame, itemFragment).addToBackStack(null).commit();
    }

    public void openSubTopic(List<SscTopicObject> topicList){
        FragmentManager fragmentManager = getSupportFragmentManager();
        SubItemFragment fragment = new SubItemFragment(topicList);
        if (fragmentManager.findFragmentById(R.id.main_frame) != null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, fragment).addToBackStack(null).commit();
        }
    }

    public void openContentPdf(String path){

        FragmentManager fragmentManager = getSupportFragmentManager();
        ContentFragmentPdf topicItem = new ContentFragmentPdf(path);
        if (fragmentManager.findFragmentById(R.id.main_frame) != null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, topicItem).addToBackStack(null).commit();
        }
    }


    @Override
    public void onChapItemListener(SscChapObject item) {
        clicks++;
        TopicDirectories(main_folder + "/" + item.getChapName());
    }

    @Override
    public void onTopicItemListener(String path) {
        clicks++;
        Log.d(TAG, "onTopicItemListener: " + clicks);
        TopicDirectories(path);
    }

    @Override
    public void onBackPressed() {
       
        Log.d(TAG, "onBackPressed: ");
        if(isAdShowing) {
            Log.d(TAG, "onBackPressed");
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        int i = fragmentManager.getBackStackEntryCount();
        if(i == 1 ) {
            finish();
        }
        else if (i > 1)
            fragmentManager.popBackStack();
        else
            super.onBackPressed();
    }


    public void toNotes(View view) {
        Intent notes = new Intent(this, Notes.class);
        startActivity(notes);
    }

    @Override
    public void DownloadComplete(int update_version) {
        if (update_version != -1) {
            EDITOR.putBoolean("IS_FIRST_TIME", false);
            EDITOR.putInt("UPDATE_VERSION", update_version);
            EDITOR.commit();
        }
        cancelProgressDialog();
        List<SscChapObject> mList = GetChapList();
        openTopic(mList);
    }

    public List<SscTopicObject> GetTopicList(String path){
        List<SscTopicObject> mList = new ArrayList<>();
        List<String> pList = new ArrayList<>();
        File currFolder = new File(path);
        for(File file : currFolder.listFiles()){
            if (file.isDirectory() && !(file.getName().equals("icon") || file.getName().equals("description"))) {
                pList.add(file.getName());
            }
        }
        java.util.Collections.sort(pList);
        for (int j=0; j<pList.size(); j++)
            mList.add(new SscTopicObject(this.getResources().getDrawable(R.drawable.ic_flare_24dp), pList.get(j), path));
        return mList;

    }

    public void TopicDirectories(String path){

        File currFolder = new File(path);
        List<SscTopicObject> mList = GetTopicList(currFolder.getAbsolutePath());
        if (mList.size() != 0) {
            openSubTopic(mList);
        }
        else {
            Log.d(TAG, "TopicDirectories");
            for (final File file : currFolder.listFiles()) {
                Log.d(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
                if (file.isFile()) {
                    clicks++;
                    openContentPdf(file.getAbsolutePath());
                }
            }
        }
    }

    public void showProgressDialog(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading content...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        isInProgress = true;
    }

    public void cancelProgressDialog(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
        isInProgress = false;
    }

}
