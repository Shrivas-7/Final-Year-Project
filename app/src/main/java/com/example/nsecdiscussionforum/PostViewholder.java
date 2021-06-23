package com.example.nsecdiscussionforum;

import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostViewholder extends RecyclerView.ViewHolder {

    ImageView imageViewprofile, ivpost;
    TextView tv_name, tv_desc, tv_likes, tv_comment, tv_time, tv_nameprofile;
    ImageButton likebtn, menu, comment, share;
    DatabaseReference likeref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int likes;

    public PostViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setpost(FragmentActivity activity, String name, String url, String posturi, String time, String uid, String type, String desc) {
        imageViewprofile = itemView.findViewById(R.id.iv_profile);
        ivpost = itemView.findViewById(R.id.iv_post_item);
        tv_comment = itemView.findViewById(R.id.tv_comments_post);
        tv_desc = itemView.findViewById(R.id.tv_desc_post);
        comment = itemView.findViewById(R.id.comment);
        menu = itemView.findViewById(R.id.more);
        share = itemView.findViewById(R.id.share);
        tv_nameprofile = itemView.findViewById(R.id.tv_name_post);
        tv_time = itemView.findViewById(R.id.tv_time_post);
        tv_likes = itemView.findViewById(R.id.tv_likes_post);

        SimpleExoPlayer exoPlayer;
        PlayerView playerView = itemView.findViewById(R.id.exoplayer_item_post);
        if (type.equals("iv")) {
            Picasso.get().load(url).into(imageViewprofile);
            Picasso.get().load(posturi).into(ivpost);
            tv_desc.setText(desc);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
            playerView.setVisibility(View.INVISIBLE);

        } else if (type.equals("vv")) {
            Picasso.get().load(url).into(imageViewprofile);
            tv_desc.setText(desc);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
            ivpost.setVisibility(View.INVISIBLE);

            try {
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(activity).build();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(activity);
                Uri video = Uri.parse(posturi);
                DefaultHttpDataSourceFactory df = new DefaultHttpDataSourceFactory("video");
                ExtractorsFactory ef = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(video, df, ef, null, null);
                playerView.setPlayer(exoPlayer);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(false);

            } catch (Exception e) {
                Toast.makeText(activity, "Error : " + e, Toast.LENGTH_SHORT).show();

            }

        }
    }

    public void likechecker(String postkey) {
        likebtn = itemView.findViewById(R.id.like);
        likeref = database.getReference("Post Likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        likeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postkey).hasChild(uid)) {
                    likebtn.setImageResource(R.drawable.ic_baseline_like_alt_24);
                    likes = (int)snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likes)+" likes");
                } else {
                    likebtn.setImageResource(R.drawable.ic_baseline_dislike_alt_24);
                    likes = (int)snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likes)+" likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
