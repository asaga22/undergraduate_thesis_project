package com.elkasaga.undegraduatethesisproject.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.ChatMessage;
import com.elkasaga.undegraduatethesisproject.models.Participant;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ListedMessageAdapter extends RecyclerView.Adapter <ListedMessageAdapter.MyViewHolder>{

    Context context;
    ArrayList<ChatMessage> messageList;
    ArrayList<User> userList;

    public ListedMessageAdapter(Context c, ArrayList<ChatMessage> messages, ArrayList<User> users){
        context = c;
        messageList = messages;
        userList = users;
    }

    @NonNull
    @Override
    public ListedMessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListedMessageAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.snippet_chat_item_outgoing, viewGroup, false));

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ListedMessageAdapter.MyViewHolder myViewHolder, int i) {

        if(FirebaseAuth.getInstance().getUid().equals(messageList.get(i).getUser().getUid())){
            myViewHolder.chatWrapper.setBackgroundResource(R.drawable.outgoing_chatbubble);
            myViewHolder.sender.setTextColor(R.color.primaryBlue);
        }
        else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    80, context.getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            5, context.getResources().getDisplayMetrics()));

            myViewHolder.chatWrapper.setBackgroundResource(R.drawable.incoming_chatbubble);
            myViewHolder.chatWrapper.setRight(80);
            myViewHolder.chatWrapper.setLayoutParams(layoutParams);

            myViewHolder.sender.setTextColor(R.color.black);
            myViewHolder.sender.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            myViewHolder.message.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }

        myViewHolder.sender.setText(messageList.get(i).getUser().getUsername());
        myViewHolder.message.setText(messageList.get(i).getMessage());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView sender, message;
        LinearLayout chatWrapper;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sender = itemView.findViewById(R.id.sender);
            message = itemView.findViewById(R.id.message);
            chatWrapper = itemView.findViewById(R.id.chatWrapper);
        }
    }

}
