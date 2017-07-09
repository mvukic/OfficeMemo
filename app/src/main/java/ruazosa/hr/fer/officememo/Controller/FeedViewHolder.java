package ruazosa.hr.fer.officememo.Controller;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.Model.User;
import ruazosa.hr.fer.officememo.R;
import ruazosa.hr.fer.officememo.Utils.GlobalData;
import ruazosa.hr.fer.officememo.View.DepartmentProfileActivity;
import ruazosa.hr.fer.officememo.View.UserProfileActivity;

/**
 * Created by shimun on 08.07.17..
 */

class FeedViewHolder extends RecyclerView.ViewHolder{
    Context context;
    TextView name, content, title, date, location, likes, upvote, comment, shortname;
    FeedAdapter  adapter;
    ImageView profile, image, locationtag;
    public FeedViewHolder(Context context,View itemView, FeedAdapter adapter) {
        super(itemView);
        this.context = context;
        this.adapter = adapter;
        name = (TextView)itemView.findViewById(R.id.textViewFeedName);
        content = (TextView)itemView.findViewById(R.id.textViewFeedContent);
        title = (TextView)itemView.findViewById(R.id.textViewFeedTitle);
        date = (TextView)itemView.findViewById(R.id.textViewFeedDate);
        location = (TextView)itemView.findViewById(R.id.textViewFeedLocation);
        profile = (ImageView)itemView.findViewById(R.id.imageViewFeedProfile);
        image = (ImageView)itemView.findViewById(R.id.imageViewFeedPicture);
        likes = (TextView)itemView.findViewById(R.id.textViewFeedVote);
        upvote = (TextView)itemView.findViewById(R.id.buttonLike);
        comment = (TextView) itemView.findViewById(R.id.buttonComment);
        locationtag = (ImageView)itemView.findViewById(R.id.imageViewLocationTag);
        shortname = (TextView)itemView.findViewById(R.id.textViewFeedDepartmentShortName);

        RxView.clicks(name).subscribe(o -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            Post post = adapter.getList().get(getAdapterPosition());
            intent.putExtra("uid", post.getUid());
            context.startActivity(intent);
        });
        RxView.clicks(shortname).subscribe(o -> {
            Intent intent = new Intent(context, DepartmentProfileActivity.class);
            Post post = adapter.getList().get(getAdapterPosition());
            intent.putExtra("did", post.getDid());
            context.startActivity(intent);
        });

        RxView.clicks(upvote).subscribe(o -> {
            Post post = adapter.getList().get(getAdapterPosition());
            if(post.getUpVotesList().contains(GlobalData.user.getUid())){
                upvote.setTextColor(context.getResources().getColor(R.color.md_grey_600));
                post.getUpVotesList().remove(GlobalData.user.getUid());
                post.setUpVotes(post.getUpVotes()-1);
            }
            else{
                upvote.setTextColor(context.getResources().getColor(R.color.accent));
                post.getUpVotesList().add(GlobalData.user.getUid());
                post.setUpVotes(post.getUpVotes()+1);

            }
            likes.setText(String.valueOf(post.getUpVotes()));
            FirebaseDatabase.getInstance().getReference("posts").child(post.getPid()).setValue(post);
        });
        RxView.clicks(comment).subscribe(o -> {
            //TODO comment page
        });

    }
}
