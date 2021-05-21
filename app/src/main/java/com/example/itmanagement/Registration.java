package com.example.itmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itmanagement.progressbarslayout.Progressdesignbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    private TextView title, registerbutton,alreadyregistered;
    private EditText editfirstName, editlastName, editEmailAddress, editPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        getSupportActionBar().hide();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Register User...");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        title = (TextView) findViewById(R.id.title);
        title.setOnClickListener(this);

        alreadyregistered = (TextView) findViewById(R.id.alreadtregistered);
        alreadyregistered.setOnClickListener(this);

        registerbutton = (Button) findViewById(R.id.registerbutton);
        registerbutton.setOnClickListener(this);

        editfirstName = (EditText) findViewById(R.id.firstName);
        editlastName = (EditText) findViewById(R.id.lastName);
        editEmailAddress = (EditText) findViewById(R.id.email);
        editPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title:
            case R.id.alreadtregistered:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.registerbutton:
                registerbutton();
                break;
        }

    }

    private void registerbutton() {
        String firstName = editfirstName.getText().toString().trim();
        String lastName = editlastName.getText().toString().trim();
        String email = editEmailAddress.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if(firstName.isEmpty()){
            editfirstName.setError("Field is required");
            editfirstName.requestFocus();
            return;
        }

        if(lastName.isEmpty()){
            editlastName.setError("Field is required");
            editlastName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editEmailAddress.setError("Field is required");
            editEmailAddress.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmailAddress.setError("Please provide valid email");
            editEmailAddress.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editPassword.setError("Field is required");
            editPassword.requestFocus();
            return;
        }

        if (password.length() < 6){
            editPassword.setError("Minimum of 6 characters!");
            editPassword.requestFocus();
            return;
        }

        final Progressdesignbar progressdesignbar = new Progressdesignbar(Registration.this);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(firstName, lastName, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {


                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                Toast.makeText(Registration.this, "User has been registered successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }, 4000);
                                        progressdesignbar.showDialog();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdesignbar.dismissbar();

                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                    String email = user.getEmail();
                                                    String uid = user.getUid();

                                                    HashMap<Object, String> hashMap = new HashMap<>();

                                                    hashMap.put("email", email);
                                                    hashMap.put("uid", uid);
                                                    hashMap.put("onlineStatus", "online");
                                                    hashMap.put("typingTo", "noOne");
                                                    hashMap.put("firstName", firstName);
                                                    hashMap.put("lastName", lastName);
                                                    hashMap.put("image", "");
                                                    hashMap.put("cover", "");

                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                    DatabaseReference reference = database.getReference("Users");
                                                    reference.child(uid).setValue(hashMap);
                                                }

                                        },4000);

                                    } else {
                                        Toast.makeText(Registration.this, "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        } else {
                            Toast.makeText(Registration.this, "Failed to register! Try again!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}