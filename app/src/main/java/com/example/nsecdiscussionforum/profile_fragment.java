package com.example.nsecdiscussionforum;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class profile_fragment extends Fragment implements View.OnClickListener {

    ImageView img;
    TextView nameEt,deptEt,dgEt,mailEt,posttv;
    ImageButton ib,menu ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        img = getActivity().findViewById(R.id.iv_cp);
        nameEt = getActivity().findViewById((R.id.tv_name_profile));
        deptEt = getActivity().findViewById(R.id.tv_dept_profile);
        dgEt = getActivity().findViewById(R.id.tv_dg);
        mailEt = getActivity().findViewById(R.id.tv_mail);
        posttv = getActivity().findViewById(R.id.posts);

        ib = getActivity().findViewById(R.id.ib_edit_profile);
        ib.setOnClickListener(this);
        posttv.setOnClickListener(this);
        menu = getActivity().findViewById(R.id.ib_menu_profile);
        menu.setOnClickListener(this);
        img.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ib_edit_profile:
                Intent intent = new Intent(getActivity(),UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.ib_menu_profile:
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(),"bottomsheet");
                break;

            case R.id.iv_cp:
                Intent intent1 = new Intent(getActivity(),ImageActivity.class);
                startActivity(intent1);
                break;
            case R.id.posts:
                Intent intent2 = new Intent(getActivity(),IndividualPost.class);
                startActivity(intent2);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        reference =firestore.collection("user").document(currentid);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                {
                 String nameResult = task.getResult().getString("Name") ;
                 String deptResult = "Department of "+ task.getResult().getString("Department") ;
                 String dgResult = task.getResult().getString("Designation") ;
                 String mailResult = task.getResult().getString("Email") ;
                 String url = task.getResult().getString("URL") ;

                    Picasso.get().load(url).into(img);
                    nameEt.setText(nameResult);
                    deptEt.setText(deptResult);
                    dgEt.setText(dgResult);
                    mailEt.setText(mailResult);
                }
                else
                {
                    Intent intent = new Intent(getActivity(),CreateProfile.class);
                    startActivity(intent);
                }
            }
        });
    }
}
