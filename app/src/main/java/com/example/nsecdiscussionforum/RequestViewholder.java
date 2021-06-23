package com.example.nsecdiscussionforum;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class RequestViewholder extends RecyclerView.ViewHolder {

    ImageView img;
    TextView req_name,req_desg,req_dept,acc,dec;

    public RequestViewholder(@NonNull View itemView) {
        super(itemView);
    }
    public void setReq(FragmentActivity activity,String name,String url,String desg,String dept,String email,String followers,String userid)
    {
        img = itemView.findViewById(R.id.req_dp);
        req_name = itemView.findViewById(R.id.req_name);
        req_desg =itemView.findViewById(R.id.req_desg);
        req_dept = itemView.findViewById(R.id.req_dept);
        acc = itemView.findViewById(R.id.acc);
        dec = itemView.findViewById(R.id.dec);

        Picasso.get().load(url).into(img);
        req_name.setText(name);
        req_desg.setText(desg);
        req_dept.setText(dept);

    }
}
