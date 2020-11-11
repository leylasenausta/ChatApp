package com.lyl.chattapp.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lyl.chattapp.MessageActivity;
import com.lyl.chattapp.Model.Chat;
import com.lyl.chattapp.Model.User;
import com.lyl.chattapp.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private  Context mContext;
    private List<User> mUsers;
    private boolean isChat;

    String theLastMessage;

    public UserAdapter(Context mContext,List<User> mUsers, boolean isChat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     final User user = mUsers.get(position);
     holder.user_name.setText(user.getUsername());
     if(user.getImageUrl().equals("default")){
         holder.profile_image.setImageResource(R.mipmap.ic_launcher);
     }
     else {
         Glide.with(mContext).load(user.getImageUrl()).into(holder.profile_image);
     }

     if(isChat){
         lastMessage(user.getId(),holder.last_msg);
     }
     else {
         holder.last_msg.setVisibility(View.GONE);
     }

     if(isChat){
         if(user.getStatus().equals("online")){
             holder.img_on.setVisibility(View.VISIBLE);
             holder.img_off.setVisibility(View.GONE);
         }
         else{
             holder.img_on.setVisibility(View.GONE);
             holder.img_off.setVisibility(View.VISIBLE);
         }
     }
     else{
         holder.img_on.setVisibility(View.GONE);
         holder.img_off.setVisibility(View.GONE);
     }

     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent intent = new Intent(mContext, MessageActivity.class);
             intent.putExtra("userId",user.getId());
             mContext.startActivity(intent);
         }
     });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView user_name ;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            profile_image = itemView.findViewById(R.id.profile_image);

            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);

            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    private void lastMessage(final String userId, final TextView last_msg){
        theLastMessage ="default";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                          chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }
                switch (theLastMessage){
                    case "default":
                        last_msg.setText("Mesaj yok");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
