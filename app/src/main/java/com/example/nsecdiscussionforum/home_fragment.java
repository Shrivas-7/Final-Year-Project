package com.example.nsecdiscussionforum;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.security.Permission;
import java.util.List;

import static android.os.Build.VERSION_CODES.P;

public class home_fragment extends Fragment implements View.OnClickListener {

    Button btn;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,likeref,db1,db2,db3;
    Boolean likechecker= false;
    String desg_result;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String currentuid;

    LinearLayoutManager linearLayoutManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        documentReference = db.collection("user").document(currentuid);
        btn = getActivity().findViewById(R.id.post);
        reference = database.getReference("All Posts");
        likeref = database.getReference("Post Likes");
        db1 = database.getReference("All Images").child(currentuid);
        db2 = database.getReference("All Videos").child(currentuid);
        db3 = database.getReference("All Posts");
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = getActivity().findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.post:
                if(desg_result.equalsIgnoreCase("Student"))
                {
                    Toast.makeText(getActivity(), "You are not allowed to post.", Toast.LENGTH_SHORT).show();
                }
                else{
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);}
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists())
                        {
                            desg_result = task.getResult().getString("Designation");
                        }


                    }
                });

        reference = database.getReference("All Posts");
        Query sort = reference.orderByChild("time");
        sort.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.i("INFO  : ",String.valueOf(snapshot.getValue() ));

                FirebaseRecyclerOptions<PostMember> options =  new FirebaseRecyclerOptions.Builder<PostMember>()
                        .setQuery(reference , PostMember.class)
                        .build();
                FirebaseRecyclerAdapter<PostMember,PostViewholder> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<PostMember, PostViewholder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull PostViewholder holder, int position, @NonNull PostMember model) {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String currentuser = user.getUid();

                                final String postkey = getRef(position).getKey();
                                holder.setpost(getActivity(),model.getName(),model.getUrl(), model.getPosturi(), model.getTime(), model.getUid(), model.getType(), model.getDesc());

                                String name = getItem(position).getName();
                              //  String url = getItem(position).getUrl();
                                String time = getItem(position).getTime();
                                String userid = getItem(position).getUid();
                                String type = getItem(position).getType();
                                String url = getItem(position).getPosturi();

                                holder.likechecker(postkey);
                                holder.share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String sharetext = name +"\n" +"\n"+url;
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(Intent.EXTRA_TEXT,sharetext);
                                        intent.setType("text/plain");
                                        startActivity(intent.createChooser(intent,"Share Via "));
                                    }
                                });
                                holder.menu.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showmenu(name,url,time,userid,type);
                                    }
                                });
                                holder.likebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        likechecker=true;
                                        likeref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (likechecker.equals(true))
                                                {
                                                    if (snapshot.child(postkey).hasChild(currentuser))
                                                    {
                                                        likeref.child(postkey).child(currentuser).removeValue();
                                                        likechecker = false;
                                                        Toast.makeText(getActivity(), "Like Removed", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        likeref.child(postkey).child(currentuser).setValue(true);
                                                        likechecker= false;
                                                        Toast.makeText(getActivity(), "Liked", Toast.LENGTH_SHORT).show();
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
                            public PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.post_layout,parent,false);
                                return new PostViewholder(view);

                            }
                        };
                firebaseRecyclerAdapter.startListening();
                recyclerView.setAdapter(firebaseRecyclerAdapter);

            }
            void showmenu(String name, String url,String time, String userid,String type)
            {
                LayoutInflater inflater= LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.post_options,null);
                TextView download = view.findViewById(R.id.download);
                TextView copyurl = view.findViewById(R.id.copyurl);
                TextView delete= view.findViewById(R.id.deletepost);

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setView(view)
                        .create();
                alertDialog.show();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentuser = user.getUid();

                if(userid.equals(currentuser))
                    delete.setVisibility(View.VISIBLE);
                else
                    delete.setVisibility(View.INVISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Query query = db1.orderByChild("time").equalTo(time);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot1: snapshot.getChildren())
                                {
                                    dataSnapshot1.getRef().removeValue();
                                    Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Query query2 = db2.orderByChild("time").equalTo(time);
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot1: snapshot.getChildren())
                                {
                                    dataSnapshot1.getRef().removeValue();
                                    Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Query query3 = db3.orderByChild("time").equalTo(time);
                        query3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot1: snapshot.getChildren())
                                {
                                    dataSnapshot1.getRef().removeValue();
                                    Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        reference.delete();
                        alertDialog.dismiss();
                    }
                });
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PermissionListener permissionListener = new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {

                                if(type.equals("iv"))
                                {
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
                                    request.setTitle("Download");
                                    request.setDescription("Downloading Image...");
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+System.currentTimeMillis()+".jpeg");
                                    DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                    manager.enqueue(request);

                                    Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                                else
                                {
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
                                    request.setTitle("Download");
                                    request.setDescription("Downloading Video...");
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+System.currentTimeMillis()+".mp4");
                                    DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                    manager.enqueue(request);

                                    Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }

                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {

                                alertDialog.dismiss();
                                Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();

                            }
                        };
                        TedPermission.with(getActivity())
                                .setPermissionListener(permissionListener)
                                .setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check();


                    }
                });
                copyurl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager cp = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("String",url);
                        cp.setPrimaryClip(clip);
                        clip.getDescription();
                        Toast.makeText(getActivity(), "URL Copied", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
