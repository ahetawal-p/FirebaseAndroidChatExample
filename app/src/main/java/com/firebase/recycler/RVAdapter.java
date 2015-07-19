package com.firebase.recycler;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.androidchat.Chat;
import com.firebase.androidchat.FirebaseListAdapter;
import com.firebase.androidchat.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ahetawal on 7/15/15.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ChatViewHolder> {

    private List<Chat> chats;
    private Map<String, Chat> chatMap;
    private ChildEventListener mListener;
    private Query mRef;


    public RVAdapter(Query query) {
        chats = new ArrayList<>();
        chatMap =  new HashMap<>();
        mRef = query;

        mListener = this.mRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Chat model = dataSnapshot.getValue(Chat.class);
                chatMap.put(dataSnapshot.getKey(), model);

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    chats.add(0, model);
                } else {
                    Chat previousModel = chatMap.get(previousChildName);
                    int previousIndex = chats.indexOf(previousModel);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == chats.size()) {
                        chats.add(model);
                    } else {
                        chats.add(nextIndex, model);
                    }
                }

                notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // One of the mModels changed. Replace it in our list and name mapping
                String modelName = dataSnapshot.getKey();
                Chat oldModel = chatMap.get(modelName);
                Chat newModel = dataSnapshot.getValue(Chat.class);
                int index = chats.indexOf(oldModel);

                chats.set(index, newModel);
                chatMap.put(modelName, newModel);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // A model was removed from the list. Remove it from our list and the name mapping
                String modelName = dataSnapshot.getKey();
                Chat oldModel = chatMap.get(modelName);
                chats.remove(oldModel);
                chatMap.remove(modelName);
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    //NONE
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });

    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mRef.removeEventListener(mListener);
        chats.clear();
        chatMap.clear();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_message, viewGroup, false);
        ChatViewHolder cvh = new ChatViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder chatViewHolder, int i) {
        chatViewHolder.author.setText(chats.get(i).getAuthor());
        chatViewHolder.message.setText(chats.get(i).getMessage());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }






    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView author;
        TextView message;


        ChatViewHolder(View itemView) {
            super(itemView);
            author = (TextView)itemView.findViewById(R.id.author);
            message = (TextView)itemView.findViewById(R.id.message);
        }
    }


}
