 package com.example.itmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.itmanagement.progressbarslayout.ProgressBarLogin;
import com.example.itmanagement.progressbarslayout.Progressdesignbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

 public class Login extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText editEmailAddress, editPassword;
    private Button signIn;

    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

         getSupportActionBar().hide();

         progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        editEmailAddress = (EditText) findViewById(R.id.email);
        editPassword = (EditText) findViewById(R.id.password);

        forgotPassword = (TextView) findViewById(R.id.forgot_Password);
        forgotPassword.setOnClickListener(this);

    }



     @Override
     public void onBackPressed() {
         //super.onBackPressed();
     }

     @Override
     public void onClick(View v) {
         switch (v.getId()){
             case R.id.register:
                startActivity(new Intent(this, Registration.class));
                break;

             case R.id.signIn:
                 userLogin();
                 break;

             case R.id.forgot_Password:
                 startActivity(new Intent(this, ForgotPassword.class));
                 break;
         }
     }

     private void userLogin() {
         String email = editEmailAddress.getText().toString().trim();
         String password = editPassword.getText().toString().trim();

         if(email.isEmpty()){
             editEmailAddress.setError("Email is required");
             editEmailAddress.requestFocus();
             return;
         }

         if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
             editEmailAddress.setError("Please enter a valid email");
             editEmailAddress.requestFocus();
             return;
         }

         if(password.isEmpty()){
             editPassword.setError("Password is required");
             editPassword.requestFocus();
             return;
         }

         if(password.length() <6 ){
             editPassword.setError("minimum of 6 characters");
             editPassword.requestFocus();
             return;
         }

         final ProgressBarLogin progressBarLogin = new ProgressBarLogin(Login.this);
         progressBarLogin.showDialog();
         Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 progressBarLogin.dismissbar();

             }
         },4000);

         mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {

                 if (task.isSuccessful()) {
                     FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                     if (task.getResult().getAdditionalUserInfo().isNewUser()){

                         String email = user.getEmail();
                         String uid = user.getUid();

                         HashMap<Object, String> hashMap = new HashMap<>();

                         hashMap.put("email", email);
                         hashMap.put("uid", uid);
                         hashMap.put("onlineStatus", "online");
                         hashMap.put("typingTo", "noOne");
                         hashMap.put("firstName", "");
                         hashMap.put("lastName", "");
                         hashMap.put("image", "");
                         hashMap.put("cover", "");

                         FirebaseDatabase database = FirebaseDatabase.getInstance();
                         DatabaseReference reference = database.getReference("Users");
                         reference.child(uid).setValue(hashMap);
                     }

                     new Handler().postDelayed(new Runnable() {

                         @Override
                         public void run() {
                             Toast.makeText(Login.this, "User Log In successfully", Toast.LENGTH_SHORT).show();
                         }
                     }, 2000);

                 if(user.isEmailVerified()){
                     startActivity(new Intent(Login.this, Dashboard.class));

                 }else {
                     user.sendEmailVerification();
                     new Handler().postDelayed(new Runnable() {

                         @Override
                         public void run() {
                             Toast.makeText(Login.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                         }
                     }, 4000);

                 }
                     //progressBar.setVisibility(View.GONE);
                 }
                 else {
                     new Handler().postDelayed(new Runnable() {

                         @Override
                         public void run() {
                             Toast.makeText(Login.this, "Failed to Login! Please Check your credentials", Toast.LENGTH_LONG).show();
                         }
                     }, 4000);
                 }
               //  progressBar.setVisibility(View.GONE);
             }
         });
     }
     @Override
     public boolean onSupportNavigateUp() {
         onBackPressed();
         return super.onSupportNavigateUp();
     }

     @Override
     protected void onStart() {
         super.onStart();

         FirebaseUser user = mAuth.getCurrentUser();
         if(user != null){
             startActivity(new Intent(Login.this,Dashboard.class));
             finish();
         }
     }
 }