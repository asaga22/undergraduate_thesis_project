package com.elkasaga.undegraduatethesisproject.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elkasaga.undegraduatethesisproject.R;
import com.elkasaga.undegraduatethesisproject.models.ChatMessage;
import com.elkasaga.undegraduatethesisproject.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
                .inflate(R.layout.snippet_chat_item, viewGroup, false));

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ListedMessageAdapter.MyViewHolder myViewHolder, int i) {
        if (messageList != null){

            if(FirebaseAuth.getInstance().getUid().equals(messageList.get(i).getUser().getUid())){
                myViewHolder.outRel.setVisibility(View.VISIBLE);
                myViewHolder.inRel.setVisibility(View.GONE);

                myViewHolder.messageOut.setText(messageList.get(i).getMessage());
                if (messageList.get(i).getTimestamp() != null){
                    myViewHolder.dateOut.setText(String.valueOf(DateConvert.timestammpToChatDate(messageList.get(i).getTimestamp())));
                }
            }
            else{
                myViewHolder.outRel.setVisibility(View.GONE);
                myViewHolder.inRel.setVisibility(View.VISIBLE);

                myViewHolder.senderIn.setText(messageList.get(i).getUser().getFullname());
                myViewHolder.messageIn.setText(messageList.get(i).getMessage());
                if (messageList.get(i).getTimestamp() != null){
                    myViewHolder.dateIn.setText(String.valueOf(DateConvert.timestammpToChatDate(messageList.get(i).getTimestamp())));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView messageOut, dateOut, senderIn, messageIn, dateIn;
        RelativeLayout inRel, outRel;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            inRel = itemView.findViewById(R.id.incomingRel);
            outRel = itemView.findViewById(R.id.outgoingRel);

            messageOut = itemView.findViewById(R.id.messageOut);
            dateOut = itemView.findViewById(R.id.dateOut);

            senderIn = itemView.findViewById(R.id.senderIn);
            messageIn = itemView.findViewById(R.id.messageIn);
            dateIn = itemView.findViewById(R.id.dateIn);

        }
    }

}
