package com.example.nettychat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.nettychat.MainActivity;
import com.example.nettychat.Model.AddFriendData;
import com.example.nettychat.Model.ChatRoomData;
import com.example.nettychat.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {


    private static String IP_ADDRESS="13.125.232.78/netty";  // 퍼블릭 IPv4 주소
    private static String TAG = "스토리";          // 로그에 사용할 태그
    private String mJsonString;                         // JSON 값을 저장할 String 변수

    private ArrayList<ChatRoomData> mList;
    private Context context;


    private SharedPreferences sf_idx;
    private String user_idx;

    public ChatRoomAdapter(ArrayList<ChatRoomData> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String join_total = mList.get(position).getJoin_total();
        int total = Integer.parseInt(join_total);

        if(total>2){
            holder.tv_name.setText(mList.get(position).getName().substring(1));
            holder.tv_content.setText(mList.get(position).getContent());
            holder.tv_time.setText(mList.get(position).getTime());

            holder.cimgv_image.setImageResource(R.drawable.ic_group);

        }else{
            holder.tv_name.setText(mList.get(position).getName());
            holder.tv_content.setText(mList.get(position).getContent());
            holder.tv_time.setText(mList.get(position).getTime());

            Glide.with(context).load(mList.get(position).getImage())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.cimgv_image);
        }
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("room_idx",mList.get(position).getRoom_idx());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name,tv_content,tv_time;
        public CircleImageView cimgv_image;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            cimgv_image = itemView.findViewById(R.id.cimgv_image);
        }
    }
}
