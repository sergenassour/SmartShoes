package com.smart.shoes.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.shoes.Helper.Helper;
import com.smart.shoes.Models.BodyModel;
import com.smart.shoes.Models.UserModel;
import com.smart.shoes.R;


public class ProfileFragment extends Fragment {
    View view;
    ImageView imageViewProfileImage;
    TextView textViewName,textViewAge,textViewHeight,textViewWeight;
    Button buttonUpdate;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    UserModel userModel=null;
    BodyModel bodyModel=null;

    Helper helper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_profile, container, false);
        helper=new Helper(getContext());
        initDB();
        initUI();
        getUserData();
        return view;
    }
    private void getUserData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                     userModel=snapshot.getValue(UserModel.class);
                    assert userModel != null;
                    textViewName.setText(userModel.getUserName());
                    if(snapshot.hasChild("bodyData")){
                         bodyModel=snapshot.child("bodyData").getValue(BodyModel.class);
                        assert bodyModel != null;
                        textViewAge.setText(bodyModel.getAge()+" Years");
                        textViewHeight.setText(bodyModel.getHeight()+" cm");
                        textViewWeight.setText(bodyModel.getWeight()+" kg");

                    }else {
                        textViewAge.setText("");
                        textViewHeight.setText("");
                        textViewWeight.setText("");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(user.getUid());
    }

    private void initUI() {
        imageViewProfileImage=view.findViewById(R.id.imageViewProfileImage);
        textViewName=view.findViewById(R.id.textViewName);
        textViewAge=view.findViewById(R.id.textViewAge);
        textViewHeight=view.findViewById(R.id.textViewHeight);
        textViewWeight=view.findViewById(R.id.textViewWeight);
        buttonUpdate=view.findViewById(R.id.buttonUpdate);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditDialog();
            }
        });

    }

    private void openEditDialog() {
        Dialog dialog=new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_update_profile);
        dialog.setCancelable(false);
        dialog.show();

        EditText editTextAge,editTextHeight
                ,editTextWeight;

        Button buttonCancel,buttonUpdate;
        editTextAge=dialog.findViewById(R.id.editTextAge);
        editTextHeight=dialog.findViewById(R.id.editTextHeight);
        editTextWeight=dialog.findViewById(R.id.editTextWeight);
        buttonCancel=dialog.findViewById(R.id.buttonCancel);
        buttonUpdate=dialog.findViewById(R.id.buttonUpdate);
        if(bodyModel!=null){
            editTextAge.setText(bodyModel.getAge());
            editTextHeight.setText(bodyModel.getHeight());
            editTextWeight.setText(bodyModel.getWeight());
        }
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BodyModel bm=new BodyModel();
                bm.setAge(editTextAge.getText().toString());
                bm.setHeight(editTextHeight.getText().toString());
                bm.setWeight(editTextWeight.getText().toString());
                Dialog dialogProgress=helper.openNetLoaderDialog();
                reference.child("bodyData").setValue(bm).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        dialogProgress.dismiss();
                        Toast.makeText(getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogProgress.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}