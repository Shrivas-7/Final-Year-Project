package com.example.nsecdiscussionforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.core.Transaction;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {

    EditText etname, etdept, etdg, etmail;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference ;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentid = user.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        documentReference= db.collection("user").document(currentid);

        etname = findViewById(R.id.name_up_et);
        etdept = findViewById(R.id.dept_up_et);
        etdg = findViewById(R.id.dg_up_et);
        etmail = findViewById(R.id.email_up_et);
        button = findViewById(R.id.btn_up);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists())
                {
                    String nameResult = task.getResult().getString("Name") ;
                    String deptResult = "Department of "+ task.getResult().getString("Department") ;
                    String dgResult = task.getResult().getString("Designation") ;
                    String mailResult = task.getResult().getString("Email") ;
                    String url = task.getResult().getString("URL") ;

                    etname.setText(nameResult);
                    etdept.setText(deptResult);
                    etdg.setText(dgResult);
                    etmail.setText(mailResult);
                }
                else
                {
                    Toast.makeText(UpdateProfile.this, "Profile Doesn't Exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfile() {

        String name = etname.getText().toString();
        String dept = etdept.getText().toString();
        String dg = etdg.getText().toString();
        String mail = etmail.getText().toString();

       DocumentReference doc_ref =FirebaseFirestore.getInstance().collection("user").document(currentid);
        Map<String, Object> map = new HashMap<>();
        map.put("Name",name);
        map.put("Department",dept);
        map.put("Designation",dg);
        map.put("Email",mail);

        doc_ref.update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProfile.this, "Profile Updated Successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, "Profile Update Failed. ", Toast.LENGTH_SHORT).show();
                    }
                });
        Map<String, Object> hash = new HashMap<>();
        hash.put("name",name);
        hash.put("dept",dept);
        hash.put("dg",dg);
        hash.put("email",mail);

        reference = FirebaseDatabase.getInstance().getReference("All Users");
        reference.child(currentid).updateChildren(hash)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(UpdateProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
}