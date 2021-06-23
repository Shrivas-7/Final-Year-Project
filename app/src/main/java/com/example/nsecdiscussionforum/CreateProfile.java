package com.example.nsecdiscussionforum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {


    EditText etname, etdept, etdg, etmail;
    Button button;
    ImageView img;
    ProgressBar pb;
    Uri imguri;
    UploadTask ut;
    StorageReference sr;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference db_ref;
    FirebaseFirestore f_db = FirebaseFirestore.getInstance();
    DocumentReference doc_ref;
    private static final int PICK_IMAGE = 1;
    All_User_Member member;
    String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        member = new All_User_Member();
        img = findViewById(R.id.iv_cp);
        etname = findViewById(R.id.name_cp_et);
        etdept = findViewById(R.id.dept_cp_et);
        etdg = findViewById(R.id.dg_cp_et);
        etmail = findViewById(R.id.email_cp_et);
        button = findViewById(R.id.btn_cp);
        pb = findViewById(R.id.progressbar_cp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = user.getUid();
        doc_ref = f_db.collection("user").document(current_user_id);
        sr = FirebaseStorage.getInstance().getReference("Profile Images");
        db_ref = db.getReference("All Users");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();

            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imguri = data.getData();
                Picasso.get().load(imguri).into(img);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error : " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadData() {

        String name = etname.getText().toString();
        String dept = etdept.getText().toString();
        String dg = etdg.getText().toString();
        String mail = etmail.getText().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(dept) && !TextUtils.isEmpty(dg) && !TextUtils.isEmpty(mail) && imguri != null) {
            pb.setVisibility(View.VISIBLE);
            final StorageReference reference = sr.child(System.currentTimeMillis() + "." + getFileExt(imguri));
            ut = reference.putFile(imguri);

            Task<Uri> uriTask = ut.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Map<String, String> profile = new HashMap<>();
                        profile.put("Name", name);
                        profile.put("Department", dept);
                        profile.put("Designation", dg);
                        profile.put("Email", mail);
                        profile.put("Privacy", "Public");
                        profile.put("UID", current_user_id);
                        profile.put("URL", downloadUri.toString());
                        try {
                            member.setName(name);
                            member.setDept(dept);
                            member.setDg(dg);
                            member.setEmail(mail);
                            member.setUid(current_user_id);
                            member.setUrl(downloadUri.toString());
                            db_ref.child(current_user_id).setValue(member);
                            doc_ref.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pb.setVisibility(View.INVISIBLE);
                                    Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(CreateProfile.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 2000);
                                }

                            });
                        } catch (Exception e) {
                            Toast.makeText(CreateProfile.this, "Error : " + e, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });


        } else {
            Toast.makeText(this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
        }

    }
}

