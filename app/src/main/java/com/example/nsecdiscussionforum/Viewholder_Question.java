package com.example.nsecdiscussionforum;

import android.app.Application;
import android.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Viewholder_Question extends RecyclerView.ViewHolder {

     ImageView imageView;
     TextView time_result,name_result,ques_result,dltbtn,ansbtn, ansbtn1;
     ImageButton save_btn;
     DatabaseReference save;
     FirebaseDatabase database =FirebaseDatabase.getInstance();


    public Viewholder_Question(@NonNull View itemView) {
        super(itemView);
    }
    public void setitem(FragmentActivity activity,String name, String url,String uid,String key,String question,String time){

        imageView = itemView.findViewById(R.id.iv_que_item);
        time_result = itemView.findViewById(R.id.time_que_item);
        name_result = itemView.findViewById(R.id.name_que_item);
        ques_result = itemView.findViewById(R.id.que_item);
        ansbtn = itemView.findViewById(R.id.ans);

        Picasso.get().load(url).into(imageView);
        time_result.setText(time);
        name_result.setText(name);
        ques_result.setText(question);
    }

    public void save_checker(String postkey) {
        save_btn = itemView.findViewById(R.id.save);
        save = database.getReference("Saved");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        save.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postkey).hasChild(uid))
                {
                    save_btn.setImageResource(R.drawable.ic_baseline_turned_in_24);
                }
                else{
                    save_btn.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setitemSaved(Application activity, String name, String url, String uid, String key, String question, String time){

       TextView timetv = itemView.findViewById(R.id.saved_time_que_item);
       TextView nametv = itemView.findViewById(R.id.saved_name_que_item);
       TextView quetv = itemView.findViewById(R.id.saved_que_item);
       ImageView imageView = itemView.findViewById(R.id.saved_iv_que_item);
       ansbtn1 = itemView.findViewById(R.id.saved_ans);

       Picasso.get().load(url).into(imageView);
       nametv.setText(name);
       timetv.setText(time);
       quetv.setText(question);


    }
    public void setitemdelete(Application activity, String name, String url, String uid, String key, String question, String time){

        TextView timetv = itemView.findViewById(R.id.delete_time_que_item);
        TextView nametv = itemView.findViewById(R.id.delete_name_que_item);
        TextView quetv = itemView.findViewById(R.id.delete_que_item);
        ImageView imageView = itemView.findViewById(R.id.delete_iv_que_item);
        dltbtn = itemView.findViewById(R.id.delete);

        Picasso.get().load(url).into(imageView);
        nametv.setText(name);
        timetv.setText(time);
        quetv.setText(question);


    }
}
