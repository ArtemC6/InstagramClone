package com.example.instagram.Adapter;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.ModelMessage;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    private ArrayList<ModelMessage> messageList;
    private Context context;
    private String recId;

    private int SENDER_VIEW_TYPE = 1;
    private int RECEIVER_VIEW_TYPE = 2;


    public ChatAdapter(ArrayList<ModelMessage> messageList, Context context, String recId) {
        this.messageList = messageList;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.simpl_sender, parent, false);
            return new SenderHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.simple_receiver, parent, false);
            return new ReceiverHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelMessage message = messageList.get(position);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new AlertDialog.Builder(context)
//                        .setTitle("Удалить сообщение ?")
//                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                // recId рандом
//                                String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
//                                database.getReference().child("chats").child(senderRoom)
//                                        .child(massegeMod.getMassageId()).removeValue();
//
//                            }
//                        }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//            }
//
//        });


        if (holder.getClass() == SenderHolder.class) {
            ((SenderHolder) holder).text_sender_message.setText(message.getMessage());
            Date date = new Date(message.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String strDate = simpleDateFormat.format(date);
            ((SenderHolder) holder).text_sender_tame.setText(strDate.toString());
        } else {
            ((ReceiverHolder) holder).text_receiver_message.setText(message.getMessage());
            Date date = new Date(message.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String strDate = simpleDateFormat.format(date);
            ((ReceiverHolder) holder).text_receiver_tame.setText(strDate.toString());

        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ReceiverHolder extends RecyclerView.ViewHolder {
        TextView text_receiver_message, text_receiver_tame;

        public ReceiverHolder(@NonNull View itemView) {
            super(itemView);

            text_receiver_message = itemView.findViewById(R.id.text_receiver_message);
            text_receiver_tame = itemView.findViewById(R.id.text_receiver_tame);
        }
    }

    public class SenderHolder extends RecyclerView.ViewHolder {
        TextView text_sender_message, text_sender_tame;

        public SenderHolder(@NonNull View itemView) {
            super(itemView);
            text_sender_message = itemView.findViewById(R.id.text_sender_message);
            text_sender_tame = itemView.findViewById(R.id.text_sender_tame);
        }
    }
}