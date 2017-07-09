package ruazosa.hr.fer.officememo.Controller;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Model.Comment;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.Model.User;
import ruazosa.hr.fer.officememo.R;
import ruazosa.hr.fer.officememo.Utils.GlobalData;

/**
 * Created by shimun on 09.07.17..
 */

public class CommentAdapter  extends RecyclerView.Adapter<CommentViewHolder> {
    Context context;
    List<Comment> listOfComments;

    public CommentAdapter(Context context, List<Comment> listOfComments) {
        this.context = context;
        this.listOfComments = listOfComments;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(context, view, this);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = listOfComments.get(position);
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance().getReference("users")
                .child(comment.getUid()), DataSnapshotMapper.of(User.class)).subscribe(user -> {
            OfficeMemo.setImageToView(context, holder.profile, Uri.parse(user.getProfileUrl()), 50, 50);
            holder.name.setText(user.getName() + " " + user.getLastName());

        });
        holder.count.setText(String.valueOf(comment.getUpVotes()));
        if(comment.getListOfLikes().contains(GlobalData.INSTANCE.getUser().getUid()))
            holder.like.setTextColor(context.getResources().getColor(R.color.accent));
        else{
            holder.like.setTextColor(Color.GRAY);
        }
        holder.date.setText(comment.getTimeStamp());
        holder.content.setText(comment.getContent());
        if(position == listOfComments.size()-1)
            holder.split.setVisibility(View.INVISIBLE);
        else
            holder.split.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return listOfComments.size();
    }
    public List<Comment> getListOfComments(){return listOfComments;}
}
