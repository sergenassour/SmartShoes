package com.smart.shoes.SideActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smart.shoes.Helper.Helper;
import com.smart.shoes.Models.SupportModel;
import com.smart.shoes.R;

public class HelpAndSupportActivity extends AppCompatActivity {

    ImageView backArrow;
    EditText editTextTitle,editTextDescription;
    Button buttonSubmit;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);
        helper=new Helper(this);
        initDB();
        initUI();
    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("supportMessages");
    }

    private void initUI() {
        backArrow=findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        editTextTitle=findViewById(R.id.editTextTitle);
        editTextDescription=findViewById(R.id.editTextDescription);
        buttonSubmit=findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFeedback();
            }
        });

    }

    private void submitFeedback() {
        SupportModel sm=new SupportModel();
        sm.setTitle(editTextTitle.getText().toString());
        sm.setDescription(editTextDescription.getText().toString());
        sm.setUserEmail(user.getEmail());
        if(sm.getTitle().equals("")){
            Toast.makeText(getApplicationContext(), "Title required", Toast.LENGTH_SHORT).show();
        }else if(sm.getDescription().equals("")){
            Toast.makeText(getApplicationContext(), "Description required", Toast.LENGTH_SHORT).show();
        }else {
            Dialog dialogProgress=helper.openNetLoaderDialog();

            reference.push().setValue(sm).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "Send Successfully", Toast.LENGTH_SHORT).show();
                    dialogProgress.dismiss();
                    editTextDescription.setText("");
                    editTextTitle.setText("");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   dialogProgress.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }




    }
}