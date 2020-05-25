package com.example.nettychat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.nettychat.Model.FriendData;
import com.example.nettychat.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddChatAdapter extends RecyclerView.Adapter<AddChatAdapter.ViewHolder>{

    private ArrayList<FriendData> mList;
    private Context context;

    public AddChatAdapter(ArrayList<FriendData> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_add,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Glide.with(context).load(mList.get(position).getF_profile())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.cimgv_fprofile);

        holder.tv_fname.setText(mList.get(position).getF_name());

        holder.checkBox_invite.setTag(mList.get(position));

        holder.checkBox_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                FriendData friendData = (FriendData) cb.getTag();

                friendData.setF_invite(cb.isChecked());
                mList.get(position).setF_invite(cb.isChecked());
                Toast.makeText(
                        v.getContext(),
                        "Clicked on Checkbox: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_fname;
        public CircleImageView cimgv_fprofile;
        public CheckBox checkBox_invite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_fname = (TextView) itemView.findViewById(R.id.tv_fname);
            cimgv_fprofile = itemView.findViewById(R.id.cimgv_fprofile);
            checkBox_invite = itemView.findViewById(R.id.checkBox_invite);


        }
    }

    // method to access in activity after updating selection
    public ArrayList<FriendData> getFriendData(){
        return mList;
    }
}