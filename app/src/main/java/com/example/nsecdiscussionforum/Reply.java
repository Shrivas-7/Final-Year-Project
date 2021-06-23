package com.example.nsecdiscussionforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Reply extends AppCompatActivity {

    String uid,question,post_key;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference, reference2;

    TextView nametv,questiontv, tvreply;
    RecyclerView recyclerView;
    ImageView imageViewQue, imageViewUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference voteref, Allquestion;
    Boolean vc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);


        nametv = findViewById(R.id.name_reply_tv);
        questiontv = findViewById(R.id.que_reply_tv);
        imageViewQue = findViewById(R.id.iv_que_user);
        imageViewUser = findViewById(R.id.iv_reply_user);
        tvreply = findViewById(R.id.answer_tv);

        recyclerView = findViewById(R.id.rv_ans);
        recyclerView.setLayoutManager(new LinearLayoutManager(Reply.this));



        Bundle extra = getIntent().getExtras();
        if(extra != null)
        {
            uid = extra.getString("uid");
            post_key = extra.getString("postkey");
            question = extra.getString("ques");
        }
        else
        {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        Allquestion = database.getReference("AllQuestions").child(post_key).child("Answer");
        voteref = database.getReference("votes");

        reference = db.collection("user").document(uid);
        reference2 = db.collection("user").document(currentuid);

        tvreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Reply.this,AnswerActivity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("ques",question);
                intent.putExtra("postkey",post_key);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            String url = task.getResult().getString("URL");
                            String name = task.getResult().getString("Name");
                            Picasso.get().load(url).into(imageViewQue);
                            nametv.setText(name);
                            questiontv.setText(question);
                        }
                    }
                });

        reference2.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            String url = task.getResult().getString("URL");
                            Picasso.get().load(url).into(imageViewUser);

                        }
                    }
                });

        FirebaseRecyclerOptions<AnswerMember> options = new FirebaseRecyclerOptions.Builder<AnswerMember>()
                .setQuery(Allquestion, AnswerMember.class)
                .build();
        FirebaseRecyclerAdapter<AnswerMember, AnsViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AnswerMember, AnsViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AnsViewholder holder, int position, @NonNull AnswerMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentuser = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.setAnswer(getApplication(), model.getName(),model.getAns(),model.getUid(),model.getTime(),model.getUrl());
                        holder.upvote_check(postkey);
                        holder.upvotetv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                vc=true;
                                voteref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(vc.equals(true))
                                        {
                                            if(snapshot.child(postkey).hasChild(currentuser)){
                                                voteref.child(postkey).child(currentuser).removeValue();
                                                vc = false;
                                            }else
                                            {
                                                voteref.child(postkey).child(currentuser).setValue(true);
                                                vc = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                    }


                    @NonNull
                    @Override
                    public AnsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.ans_layout, parent, false);
                        return new AnsViewholder(view);

                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }
}