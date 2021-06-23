package com.example.nsecdiscussionforum;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AnsViewholder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nametv, timetv, anstv, upvotetv, votenotv;
    int vote_count;
    DatabaseReference reference;
    FirebaseDatabase database;

    public AnsViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setAnswer(Application application,String name,String ans,String uid,String time,String url){

        imageView = itemView.findViewById(R.id.imageview_ans);
        nametv = itemView.findViewById(R.id.tv_name_ans);
        timetv = itemView.findViewById(R.id.tv_time_ans);
        anstv = itemView.findViewById(R.id.tv_ans);

        nametv.setText(name);
        timetv.setText(time);
        anstv.setText(ans);
        Picasso.get().load(url).into(imageView);

    }

    public void  upvote_check(String postkey)
    {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("votes");

        upvotetv = itemView.findViewById(R.id.tv_vote_ans);
        votenotv = itemView.findViewById(R.id.tv_vote_no);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postkey).hasChild(currentuid))
                {
                    upvotetv.setText("VOTED");
                    vote_count = (int)snapshot.child(postkey).getChildrenCount();
                    votenotv.setText(Integer.toString(vote_count)+"-VOTES");
                }
                else
                {
                    upvotetv.setText("UPVOTE");
                    vote_count = (int)snapshot.child(postkey).getChildrenCount();
                    votenotv.setText(Integer.toString(vote_count)+"-VOTES");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
