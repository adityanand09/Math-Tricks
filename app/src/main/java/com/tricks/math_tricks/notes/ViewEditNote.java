package com.tricks.math_tricks.notes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tricks.math_tricks.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import static com.tricks.math_tricks.App.ADD_NOTE;
import static com.tricks.math_tricks.App.FOLDER_NAME;
import static com.tricks.math_tricks.App.VIEW_NOTE;

public class ViewEditNote extends AppCompatActivity {

    private static final String TAG = "ViewEditNote";
    private static boolean START_MODE = true;

    private EditText editNote;
    private ImageButton edit, done;
    private boolean isEditMode = true;
    private String folderPath = null;
    Intent intent;

    Handler mHandler;
    ProgressDialog progressDialog;
    private boolean textEmpty = true;
    private boolean textChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_view_edit);
        editNote = findViewById(R.id.edit_note);
        editNote.addTextChangedListener(new TWatcher());
        edit = findViewById(R.id.edit_note_button);
        done = findViewById(R.id.add_note_button);
        intent = getIntent();
        mHandler = new Handler();
        folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER_NAME;
        START_MODE = intent.getBooleanExtra("start_mode", true);

        if(START_MODE == ADD_NOTE){
            setToEditMode();
        }
        else if (START_MODE == VIEW_NOTE){
            setToViewMode();
            ViewNote(getFile());
        }
    }

    public class TWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty()){
                textEmpty = true;
            }
            else {
                textEmpty = false;
            }
            textChanged = true;
        }
    }

    public String getFile(){
        return getIntent().getStringExtra("file_path");
    }

    public void setToEditMode(){
        isEditMode = true;
        editNote.setEnabled(true);
        edit.setVisibility(View.GONE);
        edit.setEnabled(false);
        done.setVisibility(View.VISIBLE);
        done.setEnabled(true);
    }

    public void setToViewMode(){
        isEditMode = false;
        editNote.setEnabled(false);
        edit.setVisibility(View.VISIBLE);
        edit.setEnabled(true);
        done.setVisibility(View.GONE);
        done.setEnabled(false);
    }

    public void cancel(View view) {
        close();
    }

    public void close(){

        if (isEditMode && textChanged && !textEmpty){
            Log.d(TAG, "cancel: " + textChanged + " " + textEmpty);
            AlertDialog dialog;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.notes_title));
            builder.setMessage(getResources().getString(R.string.notes_exit_message));
            builder.setCancelable(false);
            builder.setPositiveButton("Save & Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    if (START_MODE == ADD_NOTE)
                        SaveAndExitDialog(folderPath, editNote.getText().toString());
                    else if (START_MODE == VIEW_NOTE)
                        SaveAndExit(getFile(), editNote.getText().toString());
                }
            })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
            dialog = builder.create();
            dialog.show();
        }
        else {
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        close();
    }

    public void done(View view){

        if (START_MODE == ADD_NOTE) {
            SaveAndExitDialog(folderPath, editNote.getText().toString());
        }
        else if (START_MODE == VIEW_NOTE && textChanged){
            SaveAndExit(getFile(), editNote.getText().toString());
        }
        else {
            finish();
        }
    }

    public void SaveAndExitDialog(final String file_path, final String text){
        final Dialog dialog = new Dialog(ViewEditNote.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.note_name_dialog);

        TextView mTitle = (TextView) dialog.findViewById(R.id.title);
        final EditText mFileName = (EditText) dialog.findViewById(R.id.file_name);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        mTitle.setText("Enter note name");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file_base_name = mFileName.getText().toString();
                Log.d(TAG, "onClick: " + file_base_name);
                if (file_base_name.isEmpty())
                    Toast.makeText(getBaseContext(), "File name can't be empty", Toast.LENGTH_SHORT).show();
                else if (file_base_name.contains("\n"))
                    Toast.makeText(getBaseContext(), "File name can't contain newline character", Toast.LENGTH_SHORT).show();
                else
                    SaveAndExit(file_path + "/" + mFileName.getText().toString() + ".txt", text);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void SaveAndExit(final String file_path, final String text){
        progressDialog = new ProgressDialog(ViewEditNote.this);
        progressDialog.setMessage("Saving file....");
        progressDialog.show();
        WriteNote saveNote = new WriteNote(file_path, text);
        new Thread(saveNote).start();
        finish();
    }

    public void edit(View view) {
        setToEditMode();
    }

    public void ViewNote(String file){
        progressDialog = new ProgressDialog(ViewEditNote.this);
        progressDialog.setMessage("Loading note ....");
        progressDialog.show();
        try {
            ReadNote readNote = new ReadNote(file);
            new Thread(readNote).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    class WriteNote implements Runnable{

        final String text;
        final String file_path;

        public WriteNote(String fPath, String txt){
            this.file_path = fPath;
            this.text = txt;
        }

        @Override
        public void run() {
            FileOutputStream oStream = null;
            try {
                oStream = new FileOutputStream(new File(file_path));
                oStream.write(text.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (oStream != null) {
                        oStream.close();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                                Toast.makeText(getBaseContext(), "Notes Saved", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                                Toast.makeText(getBaseContext(), "Failed to save note", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ReadNote implements Runnable{

        FileReader fileReader = null;
        LineNumberReader lineReader = null;
        StringBuilder sBuilder = new StringBuilder();
        String text;

        public ReadNote(String filePath) throws FileNotFoundException {
            fileReader = new FileReader(filePath);
            lineReader = new LineNumberReader(fileReader);
        }

        private void read() throws IOException {
            while((text = lineReader.readLine()) != null){
                sBuilder.append(text).append("\n");
            }
        }

        @Override
        public void run() {
            try {
                read();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        editNote.setText(sBuilder.toString());
                        progressDialog.cancel();
                    }
                });
                try {
                    if (lineReader != null) lineReader.close();
                    if (fileReader != null) fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}