package com.example.digitalwellness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private ArrayList<Friend> friendList;
    private FriendsAdapter.OnNoteListener mOnNoteListener;

    public FriendsAdapter(ArrayList<Friend> friendList, FriendsAdapter.OnNoteListener mOnNoteListener) {
        this.friendList = friendList;
        this.mOnNoteListener = mOnNoteListener;

    }

    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTxt;
        private ImageView profilePicture, friendStatus;
        FriendsAdapter.OnNoteListener onNoteListener;

        public FriendViewHolder(final View view, FriendsAdapter.OnNoteListener onNoteListener) {
            super(view);
            nameTxt = view.findViewById(R.id.usernamelist);
            profilePicture = view.findViewById(R.id.userlistpicutre);

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
    public FriendsAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlistlayout, parent, false);
        return new FriendsAdapter.FriendViewHolder(itemView, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.FriendViewHolder holder, int position) {
        String name = friendList.get(position).getName();
        holder.nameTxt.setText(name);
        Picasso.get().load(friendList.get(position).getUrl()).into(holder.profilePicture);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public interface OnNoteListener {
        void onNoteCLick(int position);
    }
}
