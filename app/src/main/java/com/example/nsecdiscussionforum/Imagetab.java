package com.example.nsecdiscussionforum;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Imagetab extends Fragment {

    FirebaseDatabase database ;
    DatabaseReference reference;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.image_tab,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        database=FirebaseDatabase.getInstance();

        recyclerView = getActivity().findViewById(R.id.rv_imgtab);
        reference = database.getReference("All Images").child(uid);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostMember> options =  new FirebaseRecyclerOptions.Builder<PostMember>()
                .setQuery(reference , PostMember.class)
                .build();
        FirebaseRecyclerAdapter<PostMember,ImageFragment> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostMember, ImageFragment>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ImageFragment holder, int position, @NonNull PostMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentuser = user.getUid();

                        final String postkey = getRef(position).getKey();
                        holder.setImage(getActivity(),model.getName(),model.getUrl(), model.getPosturi(), model.getTime(), model.getUid(), model.getType(), model.getDesc());

                    }

                    @NonNull
                    @Override
                    public ImageFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_img,parent,false);
                        return new ImageFragment(view);

                    }
                };
        firebaseRecyclerAdapter.startListening();
        GridLayoutManager glm = new GridLayoutManager(getActivity(),3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}
