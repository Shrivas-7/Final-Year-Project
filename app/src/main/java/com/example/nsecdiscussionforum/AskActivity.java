package com.example.nsecdiscussionforum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AskActivity extends AppCompatActivity {

    EditText editText;
    Button btn,img_up;
    Uri imguri;
    ImageView img;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllQuestion, UserQuestion;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    StorageReference storageReference;
    QuestionMember member;
    String name,url,uid;
    private static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuser = user.getUid();

        editText = findViewById(R.id.ask_et);
        btn = findViewById(R.id.btn_submit);
        img_up = findViewById(R.id.up_img);
        img = findViewById(R.id.ques_img);
        storageReference = FirebaseStorage.getInstance().getReference("Questions");
        documentReference = db.collection("user").document(currentuser);

        AllQuestion = database.getReference("AllQuestions");
        UserQuestion = database.getReference("UserQuestions").child(currentuser);

        member = new QuestionMember();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = editText.getText().toString();
                Calendar date = Calendar.getInstance();
                SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                final String savedate = currentdate.format(date.getTime());

                Calendar ctime = Calendar.getInstance();
                SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm:ss");
                final String savetime = currenttime.format(ctime.getTime());

                String time = savedate +":"+ savetime;

                if(question!=null)
                {
                    member.setQuestion(question);
                    member.setName(name);
                    member.setUid(uid);
                    member.setUrl(url);
                    member.setTime(time);
                    String id =UserQuestion.push().getKey();
                    UserQuestion.child(id).setValue(member);
                    String child = AllQuestion.push().getKey();
                    member.setKey(id);
                    AllQuestion.child(child).setValue(member);
                    Toast.makeText(AskActivity.this, "Posted.", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                }
                else
                {
                    Toast.makeText(AskActivity.this, "Please ask a question.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        img_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imguri = data.getData();
                Picasso.get().load(imguri).into(img);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Image not uploaded", Toast.LENGTH_SHORT).show();
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

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists())
                {
                    name = task.getResult().getString("Name") ;
                    url = task.getResult().getString("URL") ;
                    uid =task.getResult().getString("UID");

                }
            }
        });
    }

}