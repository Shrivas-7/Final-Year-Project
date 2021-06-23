package com.example.nsecdiscussionforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnswerActivity extends AppCompatActivity {

    String uid,ques,postkey;
    EditText editText;
    Button btn;
    AnswerMember member;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Allquestion;
    String name ,url,time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        member = new AnswerMember();
        editText = findViewById(R.id.ans_et);
        btn = findViewById(R.id.btn_ans_submit);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            uid = bundle.getString("uid");
            //ques = bundle.getString("ques");
            postkey = bundle.getString("postkey");

        }
        else
        {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        try {

            Allquestion = database.getReference("AllQuestions").child(postkey).child("Answer");
        }
        catch (Exception e)
        {
            Log.w("postkey ",postkey);
            Toast.makeText(this, " "+postkey+ e, Toast.LENGTH_SHORT).show();

        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAns();
            }
        });
    }
    void saveAns()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        String ans = editText.getText().toString();
        if( ans != null)
        {
            Calendar date = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String savedate = currentdate.format(date.getTime());

            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm:ss");
            final String savetime = currenttime.format(ctime.getTime());

            time = savedate +":"+ savetime;
            member.setAns(ans);
            member.setTime(time);
            member.setUid(userid);
            member.setName(name);
            member.setUrl(url);

            String id = Allquestion.push().getKey();
            Allquestion.child(id).setValue(member);

            Toast.makeText(this, "Answer Saved", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Please write the answer", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        FirebaseFirestore d = FirebaseFirestore.getInstance();
        DocumentReference reference;
        reference = d.collection("user").document(userid);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists())
                        {
                            url = task.getResult().getString("URL");
                            name = task.getResult().getString("Name");

                        }
                    }
                });

    }
}