package ruazosa.hr.fer.officememo.Controller;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.Model.User;
import ruazosa.hr.fer.officememo.R;

/**
 * Created by shimun on 08.07.17..
 */

public class FeedAdapter extends RecyclerView.Adapter<SubscriptionViewHolder> {
    Context context;
    List<Post> listOfPosts;

    public FeedAdapter(Context context, List<Post> listOfPosts) {
        this.context = context;
        this.listOfPosts = listOfPosts;
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new FeedViewHolder(context, view, this);
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {

    }

    public void listOfPosts(SubscriptionViewHolder holder, int position) {
        Post post = listOfPosts.get(position);

    }

    @Override
    public int getItemCount() {
        return listOfPosts.size();
    }

    public List<Post> getList(){
        return listOfPosts;
    }
}
