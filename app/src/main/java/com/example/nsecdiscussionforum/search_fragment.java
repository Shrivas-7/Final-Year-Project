package com.example.nsecdiscussionforum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class search_fragment extends Fragment {

    private EditText search;
    ImageButton search_btn;
    RecyclerView result;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reference = database.getReference("All Users");
        search = getActivity().findViewById(R.id.search_field);
        search_btn = getActivity().findViewById(R.id.search_btn);
        result = getActivity().findViewById(R.id.result_list);
        result.setHasFixedSize(true);
        result.setLayoutManager(new LinearLayoutManager(getActivity()));
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = search.getText().toString();
                searchUser(s);
            }
        });
    }

    private void searchUser(String s) {

        Query search = reference.orderByChild("name").startAt(s).endAt(s+"\\uf8ff");
        FirebaseRecyclerOptions<User_search> options =  new FirebaseRecyclerOptions.Builder<User_search>()
                .setQuery(search , User_search.class)
                .build();
        FirebaseRecyclerAdapter<User_search,UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User_search, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User_search model) {

                holder.setdetails(getActivity(),model.getName(),model.getDg(),model.getDept(),model.getUrl(),model.getUid());

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layuot,parent,false);
                return new UserViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        result.setAdapter(firebaseRecyclerAdapter);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setdetails(FragmentActivity activity,String Name, String Desg, String Dept, String url,String uid)
        {
            TextView name = mView.findViewById(R.id.search_name);
            TextView desg = mView.findViewById(R.id.search_desg);
            TextView dept = mView.findViewById(R.id.search_dept);
            ImageView dp = mView.findViewById(R.id.dp);
            Log.d("SHRIVAS MOURYA   ::: ",Name);
            Log.d("SHRIVAS MOURYA   ::: ",Desg);
            Log.d("SHRIVAS MOURYA   ::: ",Dept);
            Log.d("SHRIVAS MOURYA   ::: ",uid);

            name.setText(Name);
            desg.setText(Desg);
            dept.setText("Department of "+Dept);
            Picasso.get().load(url).into(dp);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),ShowUser.class);
                    intent.putExtra("n",Name);
                    intent.putExtra("u",url);
                    intent.putExtra("uid",uid);
                    startActivity(intent);
                }
            });
        }
    }
}
