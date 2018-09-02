package com.example.praty.textytext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private DatabaseReference mReference;
    private String mDisplayName;
    private List<InstantMessage> mList;  //the list of the InstantMessage objects to be displayed

    //add a childeventlistener to retrieve data in the realtime database
    private ChildEventListener mListener= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.d("Google","onChildAdded: childrenCount:"+dataSnapshot.getChildrenCount());
            InstantMessage msg= dataSnapshot.getValue(InstantMessage.class);
            mList.add(msg);
            Log.d("Google","Datasnapshot added:"+dataSnapshot.toString());
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //constructor of the adapter class
    public MyAdapter( DatabaseReference mReference, String mDisplayName) {
        mList= new ArrayList<>();
        this.mReference = mReference.child("messages");
        this.mReference.addChildEventListener(mListener);
        this.mDisplayName = mDisplayName;

    }


    //create the first view in the recyclerview by inflating the layout activity_chat_row.xml
    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_row, parent,false);
        return new ViewHolder(view);
    }

    //bind the data set with the viewholder
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        holder.mAuthorName.setText(mList.get(position).getmAuthor());
        holder.mText.setText(mList.get(position).getmMessage());
        boolean isMe= mList.get(position).getmAuthor().equals(mDisplayName);
        setChatRowAppearance(isMe, holder);
    }

    //method to allign the messages according to the user
    private void setChatRowAppearance(boolean isMe, ViewHolder holder) {

        if(isMe){
            holder.params.gravity= Gravity.END;
            holder.mAuthorName.setTextColor(Color.BLACK);
            holder.mText.setBackgroundResource(R.drawable.bubble2);
        }
        else{
            holder.params.gravity=Gravity.START;
            holder.mAuthorName.setTextColor(Color.BLUE);
            holder.mText.setBackgroundResource(R.drawable.bubble1);
        }

        holder.mAuthorName.setLayoutParams(holder.params);
        holder.mText.setLayoutParams(holder.params);

    }


    @Override
    public int getItemCount() {
        Log.d("Google","Item count:"+mList.size());
        return mList.size();
    }

    //method to remove the childeventlistener
    public void CleanUp(){
        mReference.removeEventListener(mListener);
    }

    //subclass ViewHolder that overrides the onBindviewholder and oncreateviewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mAuthorName;
        TextView mText;
        LinearLayout.LayoutParams params;

        public ViewHolder(View itemView) {
            super(itemView);
            mAuthorName=(TextView) itemView.findViewById(R.id.Chatauthor);
            mText= (TextView) itemView.findViewById(R.id.Chatmessage);
            params=(LinearLayout.LayoutParams) mAuthorName.getLayoutParams();


        }
    }
}
