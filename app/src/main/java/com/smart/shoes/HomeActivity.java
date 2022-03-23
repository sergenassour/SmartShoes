package com.smart.shoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.shoes.Fragments.FatigueScoreFragment;
import com.smart.shoes.Fragments.GoalFragment;
import com.smart.shoes.Fragments.MeasurementFragment;
import com.smart.shoes.Fragments.ProfileFragment;
import com.smart.shoes.Helper.Helper;
import com.smart.shoes.Models.UserModel;
import com.smart.shoes.SideActivity.HelpAndSupportActivity;
import com.smart.shoes.SideActivity.NotificationActivity;
import com.smart.shoes.SideActivity.SettingsActivity;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
        , NavigationView.OnNavigationItemSelectedListener{
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;


    ImageView imageView;
    DrawerLayout drawer;
    NavigationView navigationView;

    ImageView imageViewNotification;
    TextView textViewUserName;
    UserModel userModel=null;
    Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        helper=new Helper(this);
        initDB();
        initBottomNavigation();
        initSideNavigation();
        getUserName();
    }

    private void getUserName() {

     reference.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             if(snapshot.exists()){
                  userModel=snapshot.getValue(UserModel.class);
                 textViewUserName.setText(userModel.getUserName());
             }else {
                 textViewUserName.setText("NA");
             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });


    }
    private void initSideNavigation() {
        imageViewNotification=findViewById(R.id.imageViewNotification);
        textViewUserName=findViewById(R.id.textViewUserName);
        textViewUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userModel!=null){
                    openEditNameDialog();

                }else {
                    Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
                }
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView =findViewById(R.id.nav_side_view);
        navigationView.setNavigationItemSelectedListener(this);
        imageView=findViewById(R.id.imageViewSideNavigation);
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        imageViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
            }
        });

    }

    private void openEditNameDialog() {
        Dialog dialog=new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.edit_name_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editTextName;
        Button buttonCancel,buttonUpdate;

        editTextName=dialog.findViewById(R.id.editTextName);
        buttonCancel=dialog.findViewById(R.id.buttonCancel);
        buttonUpdate=dialog.findViewById(R.id.buttonUpdate);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=editTextName.getText().toString();
                if(name.equals("")){
                    Toast.makeText(getApplicationContext(), "Name required", Toast.LENGTH_SHORT).show();
                }else if(name.equals(userModel.getUserName())){
                    Toast.makeText(getApplicationContext(), "update name to proceed ", Toast.LENGTH_SHORT).show();
                }else {
                    Dialog dialogProgress=helper.openNetLoaderDialog();
                    reference.child("userName").setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialogProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialogProgress.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.dismiss();
                }
            }
        });
    }

    private void initDB() {
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(firebaseUser.getUid());

    }

    private void initBottomNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        navView.setOnNavigationItemSelectedListener(this);
        LoadFragment(new FatigueScoreFragment());
    }
    private void LoadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.nav_score) {
            LoadFragment(new FatigueScoreFragment());
        }else if(item.getItemId()==R.id.nav_measurement){
            LoadFragment(new MeasurementFragment());
        }else if(item.getItemId()==R.id.nav_goal) {
            LoadFragment(new GoalFragment());
        }else if(item.getItemId()==R.id.nav_profile) {
            LoadFragment(new ProfileFragment());
        }else if(item.getItemId()==R.id.nav_connect){
            drawer.closeDrawers();
            startActivity(new Intent(HomeActivity.this,MainActivity.class));
        }else if(item.getItemId()==R.id.nav_setting) {
            drawer.closeDrawers();
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        }else if(item.getItemId()==R.id.nav_help){
            drawer.closeDrawers();
            startActivity(new Intent(HomeActivity.this, HelpAndSupportActivity.class));
        }else if(item.getItemId()==R.id.nav_logout){
            drawer.closeDrawers();
            logout();
            return false;
        }
        return true;
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.logoutString))
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(firebaseUser!=null){
                            firebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        }

                    }
                })
                .show();
    }
}