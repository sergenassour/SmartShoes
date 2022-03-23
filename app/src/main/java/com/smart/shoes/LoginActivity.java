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
import com.smart.shoes.Helper.Helper;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail,editTextPassword;
    TextView textViewCreateAccount;
    Button buttonLogin;
    Helper helper;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView textViewForgotPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initAuth();
        initUI();


    }

    private void initAuth() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

    }

    private void initUI() {
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        textViewCreateAccount=findViewById(R.id.textViewCreateAccount);
        buttonLogin=findViewById(R.id.buttonLogin);
        textViewForgotPassword=findViewById(R.id.textViewForgotPassword);
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });
        textViewCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });


        helper=new Helper(this);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });



    }

    private void doLogin() {
        String email=editTextEmail.getText().toString();
        String password=editTextPassword.getText().toString();
        if(email.equals("")){
            Toast.makeText(getApplicationContext(), "Email Required", Toast.LENGTH_SHORT).show();
        }else if(!email.contains("@")){
            Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
        }else {
            Dialog dialogProgress=helper.openNetLoaderDialog();

            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    dialogProgress.dismiss();
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialogProgress.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });




        }




    }
}