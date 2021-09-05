package com.tricks.math_tricks.fragmentItems;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.tricks.math_tricks.R;

import java.io.File;

public class ContentFragmentPdf extends Fragment {

    private String topicPath = null;
    private static final String TAG = "ContentFragment";
    private boolean isNightMode = false;


    public ContentFragmentPdf() {
        super();
        //empty constructor
    }

    public ContentFragmentPdf(String topic_path) {
        this.topicPath = topic_path;
    }

    private ImageButton nightMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            topicPath = savedInstanceState.getString("url");
            isNightMode = savedInstanceState.getBoolean("night_mode");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", topicPath);
        outState.putBoolean("night_mode", isNightMode);
    }

    PDFView pdfView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (ViewGroup) inflater.inflate(R.layout.main_content_fragment_pdf, container, false);
        vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        nightMode = view.findViewById(R.id.night_mode_btn);
        pdfView = (PDFView) view.findViewById(R.id.pdfView);
        Log.d(TAG, "onCreateView: " + topicPath);
        pdfView.fromFile(new File(topicPath)).nightMode(isNightMode).fitEachPage(true).pageFitPolicy(FitPolicy.WIDTH).spacing(0).defaultPage(0).enableSwipe(true).swipeHorizontal(false).load();
        nightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView();
            }
        });

        return view;
    }

    Vibrator vibrator;
    public void toggleView(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(50);
        }
        if (isNightMode) {
            isNightMode = false;
            Toast.makeText(getContext(), "Night mode Off", Toast.LENGTH_SHORT).show();
            pdfView.fromFile(new File(topicPath)).nightMode(isNightMode).fitEachPage(true).pageFitPolicy(FitPolicy.WIDTH).spacing(0).defaultPage(0).enableSwipe(true).swipeHorizontal(false).load();
        }
        else {
            isNightMode = true;
            Toast.makeText(getContext(), "Night mode On", Toast.LENGTH_SHORT).show();
            pdfView.fromFile(new File(topicPath)).nightMode(isNightMode).fitEachPage(true).pageFitPolicy(FitPolicy.WIDTH).spacing(0).defaultPage(0).enableSwipe(true).swipeHorizontal(false).load();
        }
    }

}
