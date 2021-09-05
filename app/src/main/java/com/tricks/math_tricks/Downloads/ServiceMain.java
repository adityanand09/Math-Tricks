package com.tricks.math_tricks.Downloads;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tricks.math_tricks.listeners.DownloadCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.tricks.math_tricks.App.UPDATE_VERSION;


public class ServiceMain extends Service {

    private static final String TAG = "ServiceMain";
    private static final String DB_NAME = "directory_listings";
    private StorageReference storageReference;
    DownloadCallback mCallback;

    public IBinder binder = new DownloadBinder();
    private int update_version;
    private int contentDownload = 0;
    private int totalContent = -1;
    private int imageDownloaded = 0;
    private int noOfChaps = -1;
    private boolean userVerified = false;

    public ServiceMain(){
    }

    public void setCallback(DownloadCallback callback){
        mCallback = callback;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private int i = -1;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");




        FirebaseDatabase.getInstance().getReference().child("total_pdfs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalContent = Integer.parseInt(dataSnapshot.getValue().toString());
                Log.d(TAG, "onDataChange: " + totalContent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("update_version").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (i == -1) {
                    i = 0;
                    update_version = Integer.parseInt(snapshot.getValue().toString());
                    Log.d(TAG, "onDataChange: server update version " + update_version);
                    Log.d(TAG, "onDataChange: system update version " + UPDATE_VERSION);
                    if (update_version == UPDATE_VERSION) {
                        imageDownloaded = noOfChaps;
                        contentDownload = totalContent;
                    }
                    else {
                        File folder = new File(getFilesDir().getAbsolutePath() + "/my_files");
                        delete(folder);
                        folder.mkdir();
                        CreateDirectories(DB_NAME, getFilesDir().getAbsolutePath() + "/my_files");
                        UPDATE_VERSION = update_version;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Runnable runnable = new Runnable() {


            @Override
            public void run() {
                while (imageDownloaded != noOfChaps && contentDownload != totalContent) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "run: " + totalContent + noOfChaps);
                mCallback.DownloadComplete(update_version);
            }
        };
        new Thread(runnable).start();

        return super.onStartCommand(intent, flags, startId);
    }




    public boolean delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null)
                for (File f : files) delete(f);
        }
        return file.delete();
    }


    private int m = 0;
    public void CreateDirectories(final String path, final String parentFolder){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (m==0){
                    noOfChaps = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                    Log.d(TAG, "onDataChange: " + noOfChaps);
                    m++;
                }
                for (DataSnapshot mSnapshot : snapshot.getChildren()){
                    String key = mSnapshot.getKey();
                    File folder = new File(parentFolder + "/" + key);
                    if (!folder.exists()) {
                        if (!folder.mkdir())
                            Log.d(TAG, "onDataChange: error creating folder");
                    }

                    if (key.equals("icon")) {
                        UrlToDrawable(mSnapshot.getValue().toString(), folder + "/icon.png");
                    }
                    else if (key.equals("description")) {
                        WriteText(mSnapshot.getValue().toString(), folder + "/description.txt");
                    }
                    else if (mSnapshot.hasChildren()) {
                        CreateDirectories(path + "/" + key, folder.getAbsolutePath());
                    }
                    else if (!mSnapshot.hasChildren()){
                        contentDownload(mSnapshot.getValue().toString(), folder + "/topic.pdf");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    public void WriteText(String description, String path){
        FileOutputStream oStream = null;
        try {
            oStream = new FileOutputStream(new File(path));
            oStream.write(description.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //isLoaded = true;
            if (oStream != null) {
                try {
                    oStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void UrlToDrawable(String referencePath, String path) {
        File localFile = new File(path);
        storageReference = FirebaseStorage.getInstance().getReference(referencePath);
        final File finalLocalFile = localFile;
        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: " + finalLocalFile.getAbsolutePath());
                imageDownloaded++;
            }
        });

    }

    public void contentDownload(String referencePath, String path){

        storageReference = FirebaseStorage.getInstance().getReference(referencePath);
        File localFile = new File(path);
        final File finalLocalFile = localFile;
        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: " + finalLocalFile.getAbsolutePath());
                contentDownload++;
            }
        });
    }

    public class DownloadBinder extends Binder {
        public ServiceMain getService(){
            return ServiceMain.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
