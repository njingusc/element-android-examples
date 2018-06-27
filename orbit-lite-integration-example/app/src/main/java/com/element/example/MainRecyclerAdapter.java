package com.element.example;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.element.utils.ElementSDKManager;

import java.util.List;

import static com.element.example.ElementSDKExampleApplication.LOG_TAG;

class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MyViewHolder> {

    List<DemoAppUser> data;
    private com.element.example.MainActivity mainActivity;

    MainRecyclerAdapter(List<DemoAppUser> data, com.element.example.MainActivity activity) {
        this.data = data;
        this.mainActivity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_data_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        myViewHolder.name.setText(data.get(position).name);
        myViewHolder.extraInfo.setVisibility(View.GONE);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.clickOnUserDataRow(data.get(position));
            }
        });

        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.v(LOG_TAG, "user info: " + data.get(position).name + " id = " + data.get(position).elementId);
                String elementId = data.get(position).elementId;
                ElementSDKManager.deleteUser(mainActivity, data.remove(position).elementId);
                notifyDataSetChanged();
                mainActivity.deleteUser(elementId);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView name;
        TextView extraInfo;
        TextView status;

        MyViewHolder(View view) {
            super(view);
            itemView = view;
            name = view.findViewById(R.id.name);
            extraInfo = view.findViewById(R.id.extraInfo);
            status = view.findViewById(R.id.status);
        }
    }
}

