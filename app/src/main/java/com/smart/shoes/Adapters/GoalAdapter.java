package com.smart.shoes.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.smart.shoes.Models.GoalModel;
import com.smart.shoes.R;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.CustomViewHolder> {
    List<GoalModel> goalModels;
    Context context;

    private  onItemClickListener mListener;
      public  interface onItemClickListener{
          void  finish(int position);
          void  delete(int position);
        }
     public  void setOnItemClickListener(onItemClickListener listener){//item click listener initialization
          mListener=listener;
     }
     public static class  CustomViewHolder extends RecyclerView.ViewHolder{

          TextView textViewTitle,textViewStartDate,
               textViewEndDate,textViewStatus;
          Button buttonFinish,buttonDelete;

          public CustomViewHolder(View itemView, final onItemClickListener listener) {
             super(itemView);

              textViewTitle=itemView.findViewById(R.id.textViewTitle);
              textViewStartDate=itemView.findViewById(R.id.textViewStartDate);
              textViewEndDate=itemView.findViewById(R.id.textViewEndDate);
              textViewStatus=itemView.findViewById(R.id.textViewStatus);
              buttonFinish=itemView.findViewById(R.id.buttonFinish);
              buttonDelete=itemView.findViewById(R.id.buttonDelete);

              buttonFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.finish(position);
                        }
                    }
                }
            });
              buttonDelete.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if (listener!=null){
                          int position=getAdapterPosition();
                          if(position!= RecyclerView.NO_POSITION){
                              listener.delete(position);
                          }
                      }
                  }
              });

        }
    }
    public GoalAdapter(List<GoalModel> goalModels, Context context) {
        this.goalModels =goalModels;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position) {
            return R.layout.item_goal;
    }
    @Override
    public int getItemCount() {
        return  goalModels.size();
    }
    
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false),mListener);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
          holder.textViewEndDate.setText(goalModels.get(position).getEndDate());
          holder.textViewStartDate.setText(goalModels.get(position).getStartDate());
          holder.textViewTitle.setText(goalModels.get(position).getGoalTitle());
          holder.textViewStatus.setText(goalModels.get(position).getStatus());
          if(goalModels.get(position).getStatus().equals("Finish")){
              holder.buttonFinish.setEnabled(false);
          }


      }
}
