package com.smart.shoes.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.shoes.Adapters.GoalAdapter;
import com.smart.shoes.CalanderFragments.DatePickerFragment;
import com.smart.shoes.Helper.Helper;
import com.smart.shoes.Models.GoalModel;
import com.smart.shoes.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class GoalFragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    View view;
    Button buttonAdd;
    RecyclerView recyclerView;
    LinearLayout layoutEmpty;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    List<GoalModel> goalModels;
    List<String> goalIDs;
    GoalAdapter goalAdapter;

    Helper helper;
    TextView textViewEndDate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_goal, container, false);
        helper=new Helper(getContext());
        initDB();
        initUI();
        initRecyclerView();
        getAllGoals();
        return view;
    }

    private void getAllGoals() {
        Dialog dialogProgress=helper.openNetLoaderDialog();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialogProgress.dismiss();
                goalModels.clear();
                goalIDs.clear();
                for (DataSnapshot data:snapshot.getChildren()){
                    goalModels.add(data.getValue(GoalModel.class));
                    goalIDs.add(data.getKey());
                }
                if(goalModels.size()>0){
                    recyclerView.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                }else {
                    recyclerView.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
               goalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               dialogProgress.dismiss();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initRecyclerView() {
        goalIDs=new ArrayList<>();
        goalModels=new ArrayList<>();
        goalAdapter = new GoalAdapter(goalModels, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(goalAdapter);
        goalAdapter.setOnItemClickListener(new GoalAdapter.onItemClickListener() {
            @Override
            public void finish(int position) {
                GoalModel goalModel=goalModels.get(position);
                goalModel.setStatus("Finish");
                Dialog dialogProgress=helper.openNetLoaderDialog();

                reference.child(goalIDs.get(position)).setValue(goalModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"updated", Toast.LENGTH_SHORT).show();
                                dialogProgress.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialogProgress.dismiss();
                    }
                });


            }

            @Override
            public void delete(int position) {
                Dialog dialogProgress=helper.openNetLoaderDialog();
                reference.child(goalIDs.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        dialogProgress.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialogProgress.dismiss();
                    }
                });


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
                .child(user.getUid())
                .child("goals");
    }

    private void initUI() {
        buttonAdd=view.findViewById(R.id.buttonAdd);
        recyclerView=view.findViewById(R.id.recyclerView);
        layoutEmpty=view.findViewById(R.id.layoutEmpty);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAddGoal();
            }
        });

    }

    private void openDialogAddGoal() {
      Dialog dialog=new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
      dialog.setContentView(R.layout.dialog_add_goal);
      dialog.show();
        EditText editTextTitle;

        Button buttonCancel,buttonAdd;

        editTextTitle=dialog.findViewById(R.id.editTextTitle);
        textViewEndDate=dialog.findViewById(R.id.textViewEndDate);
        textViewEndDate.setText(helper.currentDate());
        buttonCancel=dialog.findViewById(R.id.buttonCancel);
        buttonAdd=dialog.findViewById(R.id.buttonAdd);
        textViewEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog  StartTime = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                       textViewEndDate.setText(dayOfMonth+"-"+monthOfYear+1+"-"+year);
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

               StartTime.show();

              /*  DialogFragment datePicker = new DatePickerFragment();
                datePicker.setTargetFragment(GoalFragment.this, 0);
                datePicker.show(getFragmentManager(), "date picker");*/
            }
        });



        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoalModel goalModel=new GoalModel();
                goalModel.setGoalTitle(editTextTitle.getText().toString());
                goalModel.setEndDate(textViewEndDate.getText().toString());
                goalModel.setStartDate(helper.currentDate());
                goalModel.setStatus("Active");
                if(goalModel.getGoalTitle().equals("")){
                    Toast.makeText(getContext(), "Title required", Toast.LENGTH_SHORT).show();
                }else {
                    Dialog dialogProgress=helper.openNetLoaderDialog();
                    reference.push().setValue(goalModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                            dialogProgress.dismiss();
                            Toast.makeText(getContext(), "Goal successfully added", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                             dialogProgress.dismiss();
                        }
                    });


                }

            }
        });



    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month+1);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
       textViewEndDate.setText(currentDateString);
    }
}