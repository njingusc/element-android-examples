package com.element.example;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MyViewHolder> {

    List<DemoAppUser> data;
    private UserListActionListener listener;

    MainRecyclerAdapter(UserListActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<DemoAppUser> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_data_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
        final DemoAppUser user = data.get(position);

        myViewHolder.name.setText(user.name);
        myViewHolder.extraInfo.setVisibility(View.GONE);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickUser(user);
            }
        });

        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClickUser(user, myViewHolder.getAdapterPosition());
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
        TextView name;
        TextView extraInfo;
        TextView status;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            extraInfo = view.findViewById(R.id.extraInfo);
            status = view.findViewById(R.id.status);
        }
    }

    interface UserListActionListener {
        void onClickUser(DemoAppUser user);
        void onLongClickUser(DemoAppUser user, int position);
    }
}

