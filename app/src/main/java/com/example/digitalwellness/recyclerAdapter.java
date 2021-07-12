package com.example.digitalwellness;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {

    private ArrayList<User> usersList;
    private OnNoteListener mOnNoteListener;

    public recyclerAdapter(ArrayList<User> usersList, OnNoteListener mOnNoteListener) {
        this.usersList = usersList;
        this.mOnNoteListener = mOnNoteListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameTxt, emailTxt;
        private ImageView profilePicture, friendStatus;
        OnNoteListener onNoteListener;

        public MyViewHolder(final View view, OnNoteListener onNoteListener) {
            super(view);
            nameTxt = view.findViewById(R.id.usernamelist);
            emailTxt = view.findViewById(R.id.emaillist);
            profilePicture = view.findViewById(R.id.userlistpicutre);
            friendStatus = view.findViewById(R.id.friendStatus);

            this.onNoteListener = onNoteListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteCLick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public recyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlistlayout, parent, false);
        return new MyViewHolder(itemView, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapter.MyViewHolder holder, int position) {
        String name = usersList.get(position).getUsername();
        String email = usersList.get(position).getEmail();
        holder.nameTxt.setText(name);
        holder.emailTxt.setText(email);
        Picasso.get().load(usersList.get(position).getUrl()).into(holder.profilePicture);

        if (usersList.get(position).isFriend()) {
            holder.friendStatus.setImageResource(R.drawable.friend);
        } else {
            holder.friendStatus.setImageResource(R.drawable.addfriend);
        }

        if (!usersList.get(position).isFriend()) {
            holder.friendStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.friendStatus.setImageResource(R.drawable.friend);
                    usersList.get(position).setFriend(true);
                    //Toast.makeText(v.getContext(), usersList.get(position).getUid(), Toast.LENGTH_SHORT).show();
                    FirebaseHelper firebaseHelper = new FirebaseHelper();
                    firebaseHelper.setFriend(usersList.get(position).getUid());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public interface OnNoteListener {
        void onNoteCLick(int position);
    }
}
