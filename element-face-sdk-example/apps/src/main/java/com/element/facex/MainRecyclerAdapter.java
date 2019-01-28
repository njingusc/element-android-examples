package com.element.facex;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.element.camera.ElementFaceSDK;
import com.element.camera.UserInfo;

import java.util.ArrayList;
import java.util.List;

class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MyViewHolder> {

    private List<UserInfo> userInfoList = new ArrayList<>();

    private MainActivity mainActivity;

    public MainRecyclerAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    void addAll(List<UserInfo> list) {
        userInfoList.clear();
        userInfoList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_data_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        final UserInfo userInfo = userInfoList.get(position);

        String fullName = userInfo.name + " " + userInfo.name2;
        myViewHolder.name.setText(fullName);

        if (userInfo.extra.size() == 0) {
            myViewHolder.extraInfo.setVisibility(View.GONE);
        } else {
            StringBuilder sb = new StringBuilder();
            for (String key : userInfo.extra.keySet()) {
                String value = userInfo.extra.get(key);
                if (TextUtils.isEmpty(value)) {
                    continue;
                }
                String str = key + ": " + value + "\n";
                sb.append(str);
            }
            String str = sb.toString().trim();
            if (str.length() > 0) {
                myViewHolder.extraInfo.setText(sb.toString().trim());
                myViewHolder.extraInfo.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.extraInfo.setVisibility(View.GONE);
            }
        }

        if (ElementFaceSDK.isEnrolled(userInfo.userId)) {
            myViewHolder.status.setVisibility(View.GONE);
        } else {
            myViewHolder.status.setVisibility(View.VISIBLE);
            myViewHolder.status.setText(R.string.continue_enrollment);
        }

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.clickOnUserRow(userInfo.userId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
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