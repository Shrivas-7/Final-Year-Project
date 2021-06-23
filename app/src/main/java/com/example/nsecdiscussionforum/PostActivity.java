package com.example.nsecdiscussionforum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    Button select, upload;
    EditText etdesc;
    VideoView videoView;
    String url, name;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1, db2, db3;

    MediaController mediaController;
    String type;
    PostMember postMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postMember = new PostMember();
        mediaController = new MediaController(this);
        progressBar = findViewById(R.id.pb_post);
        imageView = findViewById(R.id.iv_post);
        videoView = findViewById(R.id.vv_post);
        select = findViewById(R.id.btn_cf);
        upload = findViewById(R.id.btn_uf);
        etdesc = findViewById(R.id.desc_post);


        storageReference = FirebaseStorage.getInstance().getReference("User Posts");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All Images").child(currentuid);
        db2 = database.getReference("All Videos").child(currentuid);
        db3 = database.getReference("All Posts");

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Select();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Upload();
            }
        });
    }

    private void Upload() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        String desc = etdesc.getText().toString();
        Calendar date = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate = currentdate.format(date.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm:ss");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate +":"+ savetime;

        if(!TextUtils.isEmpty(desc) && selectedUri!=null)
        {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        if(type.equals("iv"))
                        {
                            postMember.setDesc(desc);
                            postMember.setName(name);
                            postMember.setPosturi(downloadUri.toString());
                            postMember.setTime(time);
                            postMember.setUid(currentuid);
                            postMember.setUrl(url);
                            postMember.setType("iv");

                            String id = db1.push().getKey();
                            db1.child(id).setValue(postMember);

                            String id1 = db3.push().getKey();
                            db3.child(id1).setValue(postMember);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if(type.equals("vv"))
                        {
                            postMember.setDesc(desc);
                            postMember.setName(name);
                            postMember.setPosturi(downloadUri.toString());
                            postMember.setTime(time);
                            postMember.setUid(currentuid);
                            postMember.setUrl(url);
                            postMember.setType("vv");

                            String id = db2.push().getKey();
                            db2.child(id).setValue(postMember);

                            String id1 = db3.push().getKey();
                            db3.child(id1).setValue(postMember);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });


        } else {
            Toast.makeText(this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
        }

    }



    private void Select() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedUri = data.getData();
          //  Log.d("tag","Shrivas Mourya :    " +String.valueOf(selectedUri));
            if (selectedUri.toString().contains(".jpg")) {
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
            } else if (selectedUri.toString().contains(".mp4")) {
                videoView.setMediaController(mediaController);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
            } else {
                Toast.makeText(this, "No Files Selected", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentuid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()) {
                    name = task.getResult().getString("Name");
                    url = task.getResult().getString("URL");

                }
            }
        });

    }
}