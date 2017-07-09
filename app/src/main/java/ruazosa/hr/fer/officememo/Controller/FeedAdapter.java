package ruazosa.hr.fer.officememo.Controller;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.Model.User;
import ruazosa.hr.fer.officememo.R;
import ruazosa.hr.fer.officememo.Utils.GlobalData;

/**
 * Created by shimun on 08.07.17..
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    Context context;
    List<Post> listOfPosts;

    public FeedAdapter(Context context, List<Post> listOfPosts) {
        this.context = context;
        this.listOfPosts = listOfPosts;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new FeedViewHolder(context, view, this);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        Post post = listOfPosts.get(position);
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance().getReference("users")
                .child(post.getUid()), DataSnapshotMapper.of(User.class)).subscribe(user -> {
            OfficeMemo.setImageToView(context, holder.profile,Uri.parse(user.getProfileUrl()), 50, 50);
            holder.name.setText(user.getName() + " " + user.getLastName());

        });
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance().getReference("departments")
                .child(post.getDid()), DataSnapshotMapper.of(Department.class)).subscribe(department -> {
            holder.shortname.setText(" " + department.getShortName()+ " ");

        });
        holder.date.setText(post.getTimeStamp());
        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        holder.likes.setText(String.valueOf(post.getUpVotes()));
        if(post.getLocation().isEmpty()){
            holder.location.setVisibility(View.GONE);
            holder.locationtag.setVisibility(View.GONE);
        }
        else {
            holder.location.setVisibility(View.VISIBLE);
            holder.location.setText(post.getLocation());
            holder.locationtag.setVisibility(View.VISIBLE);

        }
        if(post.getImageUrl().isEmpty()){
            holder.image.setVisibility(View.GONE);
        }
        else{
            holder.image.setVisibility(View.VISIBLE);
            OfficeMemo.setImageToView(context, holder.image, Uri.parse(post.getImageUrl()));
        }
        if(post.getUpVotesList().contains(GlobalData.user.getUid())){
            holder.upvote.setTextColor(context.getResources().getColor(R.color.accent));
        }
        else{
            holder.upvote.setTextColor(context.getResources().getColor(R.color.md_grey_600));
        }

    }

    public List<Post> listOfPosts(SubscriptionViewHolder holder, int position) {
        return listOfPosts;

    }

    @Override
    public int getItemCount() {
        return listOfPosts.size();
    }

    public List<Post> getList(){
        return listOfPosts;
    }
}
