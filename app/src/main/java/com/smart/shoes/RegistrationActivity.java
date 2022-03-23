package com.smart.shoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smart.shoes.Helper.Helper;
import com.smart.shoes.Models.UserModel;

public class RegistrationActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    
    Helper helper;
    EditText editTextName,editTextEmail,editTextPassword;
    Button buttonLogin;
    TextView textViewLoginHere;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        helper=new Helper(this);
        initDB();
        initUI();
    }

    private void initUI() {
        editTextName=findViewById(R.id.editTextName);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        buttonLogin=findViewById(R.id.buttonLogin);
        textViewLoginHere=findViewById(R.id.textViewLoginHere);
        
        textViewLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRegistration();
            }
        });
        
        
        
    }

    private void doRegistration() {
        UserModel userModel=new UserModel();
        userModel.setUserEmail(editTextEmail.getText().toString());
        userModel.setUserName(editTextName.getText().toString());
        String password=editTextPassword.getText().toString();
        
        
        if(userModel.getUserEmail().equals("")){
            Toast.makeText(getApplicationContext(), "Email required", Toast.LENGTH_SHORT).show();
        }else if(userModel.getUserName().equals("")){
            Toast.makeText(getApplicationContext(), "User Name required", Toast.LENGTH_SHORT).show();
        }else if(!userModel.getUserEmail().contains("@")){
            Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
        }else if(password.equals("")){
            Toast.makeText(getApplicationContext(), "Password required", Toast.LENGTH_SHORT).show();
        }else {
            Dialog progress=helper.openNetLoaderDialog();
            auth.createUserWithEmailAndPassword(userModel.getUserEmail(),password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
                  user=auth.getCurrentUser();
                  reference.child(user.getUid()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void unused) {
                          Toast.makeText(getApplicationContext(), "Data Uploaded", Toast.LENGTH_SHORT).show();
                          progress.dismiss();
                          startNewActivity();
                      }
                  }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                        progress.dismiss();
                          Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                  });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();     
                    progress.dismiss();
                }
            });
        }
    }

    private void startNewActivity() {
        Intent intent=new Intent(RegistrationActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users");
    }
}