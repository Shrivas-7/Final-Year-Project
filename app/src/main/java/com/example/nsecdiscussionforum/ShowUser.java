package com.example.nsecdiscussionforum;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStore;

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

public class ShowUser extends AppCompatActivity {

    TextView name,dept,desg,email,followers,posts;
    Button btn;
    ImageView img;
    FirebaseDatabase database;
    DatabaseReference databaseReference,databaseReference1,databaseReference2,postnoref,db1,db2;
    int postno,followersno,postiv,postvv;
    String url, Name,uid,Dept,Desg,foluid;
    RequestMember requestmember;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference,documentReference1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showuser);

        database = FirebaseDatabase.getInstance();
        requestmember = new RequestMember();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuser = user.getUid();

        name = findViewById(R.id.su_name);
        dept = findViewById(R.id.su_dept);
        desg = findViewById(R.id.su_dg);
        email = findViewById(R.id.su_mail);
        img = findViewById(R.id.su_cp);
        followers = findViewById(R.id.followers);
        posts = findViewById(R.id.posts);
        btn = findViewById(R.id.su_follow);

        Bundle bundle = getIntent().getExtras();

        if(bundle!= null){
            url = bundle.getString("u");
        Name = bundle.getString("n");
        uid = bundle.getString("uid");}

        Log.d("SHRIVAS MOURYA   ::: ",uid);
        Log.d("SHRIVAS MOURYA   ::: ",currentuser);

        databaseReference = database.getReference("Requests").child(uid);
        databaseReference1 = database.getReference("followers").child(uid);
        documentReference = db.collection("user").document(uid);
        //postnoref = database.getReference("User Posts").child(uid);
        databaseReference2 = database.getReference("followers");
        documentReference1 = db.collection("user").document(currentuser);
        db1 = database.getReference("All Images").child(uid);
        db2 = database.getReference("All Videos").child(uid);

      /*  postnoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postno = (int)snapshot.getChildrenCount();
                posts.setText(Integer.toString(postno));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = btn.getText().toString();
                if(status.equals("Follow"))
                    follow();
                else if (status.equals("Requested"))
                    delReq();
                else if(status.equals("Following"))
                    unfollow();
            }
        });
    }
    private void delReq()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid = user.getUid();
        databaseReference.child(currentuserid).removeValue();
        btn.setText("Follow");

    }

    @Override
    protected void onStart() {
        super.onStart();

        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postiv = (int)snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postvv = (int)snapshot.getChildrenCount();
                String total = Integer.toString(postiv+postvv);
                posts.setText(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists())
                        {
                            String name_result = task.getResult().getString("Name");
                            String desg_result = task.getResult().getString("Designation");
                            String dept_result = task.getResult().getString("Department");
                            String email_result = task.getResult().getString("Email");
                            String url = task.getResult().getString("URL");


                            name.setText(name_result);
                            dept.setText(dept_result);
                            desg.setText(desg_result);
                            email.setText(email_result);
                            Picasso.get().load(url).into(img);
                        }


                    }
                });

        documentReference1.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists())
                        {
                            Name = task.getResult().getString("Name");
                            Desg = task.getResult().getString("Designation");
                            Dept = task.getResult().getString("Department");
                            foluid = task.getResult().getString("UID");
                            url = task.getResult().getString("URL");

                        }
                    }
                });
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postiv = (int)snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postvv = (int)snapshot.getChildrenCount();
                String total = Integer.toString(postiv+postvv);
                posts.setText(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    followersno = (int)snapshot.getChildrenCount();
                    followers.setText(Integer.toString(followersno));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuser = user.getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(currentuser))
                {
                    btn.setText("Requested");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).hasChild(currentuser))
                    btn.setText("Following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void follow()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuser = user.getUid();


        requestmember.setName(Name);
        requestmember.setDept(Dept);
        requestmember.setDesg(Desg);
        requestmember.setUrl(url);
        requestmember.setUserid(foluid);


        if(uid.equals(currentuser))
            Toast.makeText(this, "You cant follow yourself", Toast.LENGTH_SHORT).show();
        else {
            databaseReference.child(currentuser).setValue(requestmember);
            btn.setText("Requested");
            Toast.makeText(this, "Follow Request sent", Toast.LENGTH_SHORT).show();
        }

    }
    void unfollow()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuser = user.getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowUser.this);
        builder.setTitle("Unfollow")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference1.child(currentuser).removeValue();
                        btn.setText("Follow");
                        Toast.makeText(ShowUser.this, "Unfollowed", Toast.LENGTH_SHORT).show();

                                    followersno--;
                                    followers.setText(Integer.toString(followersno));


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create();
        builder.show();

    }

}
