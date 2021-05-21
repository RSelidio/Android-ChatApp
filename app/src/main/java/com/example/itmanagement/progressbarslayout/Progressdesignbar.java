package com.example.itmanagement.progressbarslayout;

import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itmanagement.R;

public class Progressdesignbar {

    AppCompatActivity activity;
    AlertDialog alertDialog;

    public Progressdesignbar(AppCompatActivity thisactivity){
        activity = thisactivity;
    }
    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progress_registration,null));
        alertDialog = builder.create();
        alertDialog.show();

    }
    public void dismissbar(){
        alertDialog.dismiss();
    }
}
