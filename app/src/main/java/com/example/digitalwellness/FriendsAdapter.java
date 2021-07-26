package com.example.digitalwellness;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

        private TextView nameTxt, emailTxt;
        private ImageView profilePicture, yesButton, noButton;
        FriendsAdapter.OnNoteListener onNoteListener;


        public FriendViewHolder(final View view, FriendsAdapter.OnNoteListener onNoteListener) {
            super(view);
            nameTxt = view.findViewById(R.id.usernamelist);
            profilePicture = view.findViewById(R.id.userlistpicutre);
            yesButton = view.findViewById(R.id.yesButton);
            noButton = view.findViewById(R.id.friendStatus);
            emailTxt = view.findViewById(R.id.emaillist);

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
        holder.emailTxt.setText(" ");
        Picasso.get().load(friendList.get(position).getUrl()).into(holder.profilePicture);
        Picasso.get().load(R.drawable.tick).into(holder.yesButton);
        Picasso.get().load(R.drawable.cross).into(holder.noButton);
        FirebaseHelper firebaseHelper = new FirebaseHelper();

        holder.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.addFriend(
                        firebaseHelper.getUid(),
                        firebaseHelper.getUser().getDisplayName(),
                        friendList.get(position).getUid(),
                        friendList.get(position).getName());
                        removeRequest(firebaseHelper.getUid(),
                                friendList.get(position).getUid());
                        friendList.remove(position);
                Activity activity = (Activity) v.getContext();
                activity.finish();
                activity.overridePendingTransition( 0, 0);
                activity.startActivity(activity.getIntent());
                activity.overridePendingTransition( 0, 0);
            }
        });

        holder.noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRequest(firebaseHelper.getUid(), friendList.get(position).getUid());
                Toast.makeText(v.getContext(), "removing" + friendList.get(position).getUid(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void removeRequest(String myUid, String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("Requests").child(myUid).child(uid);

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", String.valueOf(databaseError.toException()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public interface OnNoteListener {
        void onNoteCLick(int position);
    }
}
