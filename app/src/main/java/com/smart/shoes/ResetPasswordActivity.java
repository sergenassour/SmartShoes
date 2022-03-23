package com.smart.shoes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smart.shoes.Helper.Helper;

public class ResetPasswordActivity extends AppCompatActivity {
     ImageView backArrow;
     EditText edEmail;
     Button buttonGetResetLink;
     Helper helper;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        helper=new Helper(this);
        initAuth();
        initViews();
    }

    private void initAuth() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
    }

    private void initViews() {
        backArrow=findViewById(R.id.backArrow);
        edEmail=findViewById(R.id.edEmail);
        buttonGetResetLink=findViewById(R.id.buttonGetResetLink);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonGetResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }

    private void resetPassword() {
        String email=edEmail.getText().toString();

        if(email.equals("")){
            Toast.makeText(getApplicationContext(), "Email required", Toast.LENGTH_SHORT).show();
        }else  if(!email.contains("@")){
            Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
        }else {
            Dialog dialogProgress=helper.openNetLoaderDialog();

          auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                  Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.resetLink), Toast.LENGTH_LONG).show();
                   dialogProgress.dismiss();
                   finish();
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull  Exception e) {
                dialogProgress.dismiss();
                  Toast.makeText(getApplicationContext(), "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();

              }
          });
        }



    }
}