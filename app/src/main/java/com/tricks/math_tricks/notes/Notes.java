package com.tricks.math_tricks.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.tricks.math_tricks.R;
import com.tricks.math_tricks.listAdapters.NotesAdapter;
import com.tricks.math_tricks.listeners.OnNotesInteractionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.tricks.math_tricks.App.ADD_NOTE;
import static com.tricks.math_tricks.App.FOLDER_NAME;
import static com.tricks.math_tricks.App.VIEW_NOTE;

public class Notes extends AppCompatActivity implements OnNotesInteractionListener {

    private static final int STORAGE_REQUEST_CODE = 1;
    private static final String TAG = "Notes";
    private String folderName = null;

    private RecyclerView recyclerView;
    private List<File> notes_list = null;
    private NotesAdapter adapter;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    private boolean listEmpty = true;

    ImageView empty_note_img;
    TextView empty_note_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_notes);
        empty_note_img = findViewById(R.id.empty_note_img);
        empty_note_text = findViewById(R.id.empty_note_text);
        recyclerView = findViewById(R.id.notes_list);
    }

    public void displayNotesList(){
        getFilesToList();
        Log.d(TAG, "displayNotesList: " + listEmpty);
        listEmpty = notes_list.size() <= 0;
        if (listEmpty) {
            recyclerView.setVisibility(View.GONE);
            recyclerView.setEnabled(false);
            empty_note_text.setVisibility(View.VISIBLE);
            empty_note_text.setEnabled(true);
            empty_note_img.setVisibility(View.VISIBLE);
            empty_note_img.setEnabled(true);
        }
        else {
            empty_note_text.setVisibility(View.GONE);
            empty_note_text.setEnabled(false);
            empty_note_img.setVisibility(View.GONE);
            empty_note_img.setEnabled(false);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setEnabled(true);
        }
        adapter = new NotesAdapter(notes_list, this);
        recyclerView.setLayoutManager(layoutManager(getResources().getConfiguration()));
        recyclerView.setAdapter(adapter);
    }

    public GridLayoutManager layoutManager(Configuration configuration){
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            return new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        else
            return new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Permitted()){
            try {
                CreateDirectory();
                displayNotesList();
            }catch (Exception e){
                Log.d(TAG, "onRequestPermissionsResult: " + e.getMessage());
            }
        }
        else if (!shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "No permission to store notes", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Allow permission from settings", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (!Permitted()) {
            showPermissionDialog();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!Permitted())
            RequestPermission();
        else {
            CreateDirectory();
            displayNotesList();
        }
    }

    public void showPermissionDialog(){
        builder = null;
        dialog = null;
        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Permission Required");
        builder.setMessage("Storage permission is required to save your notes");
        builder.setPositiveButton("GRANT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                RequestPermission();
            }
        });
        builder.setNegativeButton("DENY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void RequestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
    }

    public void add_note(View view) {
        Intent addNote = new Intent(this, ViewEditNote.class);
        addNote.putExtra("start_mode", ADD_NOTE);
        startActivity(addNote);
    }

    public void CreateDirectory(){
        if (Permitted()) {
            folderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER_NAME;
            Log.d(TAG, "CreateDirectory: " + folderName);
            File folder = new File(folderName);
            if (!folder.exists())
                if (!folder.mkdir()) {
                    Log.d(TAG, "CreateDirectory: failed");
                    Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show();
                } else
                    Log.d(TAG, "CreateDirectory: success");
        }
    }

    public void getFilesToList(){
        notes_list = new ArrayList<>();
        File notes = new File(folderName);
        File[] file_list;
        if (notes.exists()){
            file_list = notes.listFiles();
            for(File file: file_list){
                if (file.isFile())
                    notes_list.add(file);
                Log.d(TAG, "getFilesToList");
            }
            if (notes_list.size() > 0) listEmpty = false;
        }
        else listEmpty = true;
    }

    public boolean Permitted(){
        int p_read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int p_write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        return p_read == PackageManager.PERMISSION_GRANTED && p_write == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void OnNotesItemClicked(File file) {
        Intent viewEditNote = new Intent(this, ViewEditNote.class);
        viewEditNote.putExtra("start_mode", VIEW_NOTE);
        viewEditNote.putExtra("file_path", file.getAbsolutePath());
        String file_name = file.getName();
        viewEditNote.putExtra("file_base_name", file_name.substring(0, file_name.lastIndexOf(".")));
        startActivity(viewEditNote);
    }

    @Override
    public void OnNotesItemLongClicked(final File file) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Note")
                .setMessage("Are you sure want to delete selected note")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        file.delete();
                        displayNotesList();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog = builder.create();
                dialog.show();
    }

    public void note_back(View view) {
        finish();
    }
}
