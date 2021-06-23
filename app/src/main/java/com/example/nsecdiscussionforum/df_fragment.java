package com.example.nsecdiscussionforum;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class df_fragment extends Fragment implements View.OnClickListener{

    FloatingActionButton fb;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference,saveref, save_listref;
    Boolean save_check=false;
    RecyclerView recyclerView;

    ImageView imageView;

    QuestionMember member;

    LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.df_fragment,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuser = user.getUid();

        recyclerView = getActivity().findViewById(R.id.rv_df);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        databaseReference =database.getReference("AllQuestions");
        member = new QuestionMember();
        saveref = database.getReference("Saved");
        save_listref = database.getReference("SavedList").child(currentuser);



        imageView = getActivity().findViewById(R.id.iv_df);
        fb = getActivity().findViewById(R.id.floatingActionButton6);
        reference = db.collection("user").document(currentuser);

        fb.setOnClickListener(this);
        imageView.setOnClickListener(this);

        FirebaseRecyclerOptions<QuestionMember> options =  new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(databaseReference , QuestionMember.class)
                .build();
        FirebaseRecyclerAdapter<QuestionMember,Viewholder_Question> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Question holder, int position, @NonNull QuestionMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentuser = user.getUid();

                        final String postkey = getRef(position).getKey();


                        holder.setitem(getActivity(),model.getName(),model.getUrl(),model.getUid(),model.getKey(),model.getQuestion(),model.getTime());

                        String ques = getItem(position).getQuestion();
                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String time = getItem(position).getTime();
                        String uid = getItem(position).getUid();

                        holder.ansbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),Reply.class);
                                intent.putExtra("uid",uid);
                                intent.putExtra("ques",ques);
                                intent.putExtra("postkey",postkey);
                                startActivity(intent);

                            }
                        });

                        holder.save_checker(postkey);
                        holder.save_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                save_check=true;
                                saveref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (save_check.equals(true))
                                        {
                                            if (snapshot.child(postkey).hasChild(currentuser))
                                            {
                                                saveref.child(postkey).child(currentuser).removeValue();
                                                delete(time);
                                                save_check = false;
                                            }
                                            else{
                                                saveref.child(postkey).child(currentuser).setValue(true);
                                                member.setName(name);
                                                member.setTime(time);
                                                member.setUid(uid);
                                                member.setUrl(url);
                                                member.setQuestion(ques);

                                                //String id = save_listref.push().getKey();
                                                save_listref.child(postkey).setValue(member);
                                                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                                                save_check = false;
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
                    public Viewholder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.question_item,parent,false);
                        return new Viewholder_Question(view);

                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    void delete(String time)
    {
        Query query = save_listref.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1: snapshot.getChildren())
                {
                    dataSnapshot1.getRef().removeValue();
                    Toast.makeText(getActivity(), "Question removed from Saved List", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.iv_df:
                BottomSheetdf bottomSheetdf = new BottomSheetdf();
                bottomSheetdf.show(getFragmentManager(),"bottom");
                break;
            case R.id.floatingActionButton6:
                Intent intent = new Intent(getActivity(),AskActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists())
                        {
                            String url = task.getResult().getString("URL");
                            Picasso.get().load(url).into(imageView);
                        }
                    }
                });
    }
}
