package com.lyl.chattapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lyl.chattapp.MessageActivity;
import com.lyl.chattapp.Model.Chat;
import com.lyl.chattapp.Model.User;
import com.lyl.chattapp.R;

import java.util.List;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT  = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser fUser;

    private Context mContext;
    private List<Chat> mChats;
    private String imageUrl;

    public MessageAdapter(Context mContext,List<Chat> mChats, String imageUrl){
        this.mChats = mChats;
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

       Chat chat = mChats.get(position);
       holder.showMessage.setText(chat.getMessage());

       if(imageUrl.equals("default")){
           holder.profile_image.setImageResource(R.mipmap.ic_launcher);
       }
       else
       {
           Glide.with(mContext).load(imageUrl).into(holder.profile_image);
       }

       if(position == mChats.size()-1){
           if(chat.isIsseen()){
               holder.txt_seen.setText("Görüldü");
           }
           else{
               holder.txt_seen.setText("Gönderildi");
           }
       }

       else
       {
           holder.txt_seen.setVisibility(View.GONE);
       }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView showMessage ;
        public ImageView profile_image;

        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
       fUser= FirebaseAuth.getInstance().getCurrentUser();
       if(mChats.get(position).getSender().equals(fUser.getUid())){
           return MSG_TYPE_RIGHT;
       }
       else {
           return MSG_TYPE_LEFT;
       }
    }
}

