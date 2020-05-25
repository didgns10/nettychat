package com.example.nettychat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.nettychat.Model.FriendData;
import com.example.nettychat.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{

    private ArrayList<FriendData> mList;
    private Context context;

    public FriendAdapter(ArrayList<FriendData> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {

        Glide.with(context).load(mList.get(position).getF_profile())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.cimgv_fprofile);

        holder.tv_fname.setText(mList.get(position).getF_name());

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
}
