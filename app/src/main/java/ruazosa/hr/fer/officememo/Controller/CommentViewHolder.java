package ruazosa.hr.fer.officememo.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.Comparator;
import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Model.Comment;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.R;
import ruazosa.hr.fer.officememo.Utils.GlobalData;
import ruazosa.hr.fer.officememo.View.DepartmentProfileActivity;
import ruazosa.hr.fer.officememo.View.UserProfileActivity;

/**
 * Created by shimun on 09.07.17..
 */

public class CommentViewHolder extends RecyclerView.ViewHolder{
    Context context;
    TextView name, content, date, like, count;
    CommentAdapter  adapter;
    ImageView profile;
    View split;
    public CommentViewHolder(Context context, View itemView, CommentAdapter adapter) {
        super(itemView);
        this.context = context;
        this.adapter = adapter;
        name = (TextView)itemView.findViewById(R.id.textViewCommentName);
        content = (TextView)itemView.findViewById(R.id.textViewCommentContent);
        date = (TextView)itemView.findViewById(R.id.textViewCommentTimeStamp);
        profile = (ImageView)itemView.findViewById(R.id.imageViewCommentProfile);
        like = (TextView)itemView.findViewById(R.id.textViewCommentLike);
        count = (TextView)itemView.findViewById(R.id.textViewCommentCount);
        split = itemView.findViewById(R.id.commentSplit);

        RxView.clicks(name).subscribe(o -> {
            Comment comment = adapter.getListOfComments().get(getAdapterPosition());
            Intent i = new Intent(context, UserProfileActivity.class);
            i.putExtra("uid", comment.getUid());
            context.startActivity(i);
        });
        RxView.clicks(profile).subscribe(o -> {
            Comment comment = adapter.getListOfComments().get(getAdapterPosition());
            Intent i = new Intent(context, UserProfileActivity.class);
            i.putExtra("uid", comment.getUid());
            context.startActivity(i);
        });

        RxView.clicks(like).subscribe(o -> {
            Comment comment = adapter.getListOfComments().get(getAdapterPosition());
            String userId = GlobalData.INSTANCE.getUser().getUid();
            if(comment.getListOfLikes().contains(userId)){
                    List<Comment> comments = adapter.getListOfComments();
                    comments.remove(comment);
                    comment.getListOfLikes().remove(userId);
                    comment.setUpVotes(comment.getUpVotes()-1);
                    comments.add(comment);
                    FirebaseDatabase.getInstance().getReference("posts")
                            .child(comment.getPid()).child("comments").setValue(comments);
                like.setTextColor(Color.GRAY);

            }
            else{
                List<Comment> comments = adapter.getListOfComments();
                comments.remove(comment);
                comment.getListOfLikes().add(userId);
                comment.setUpVotes(comment.getUpVotes()+1);
                comments.add(comment);
                FirebaseDatabase.getInstance().getReference("posts")
                        .child(comment.getPid()).child("comments").setValue(comments);
                like.setTextColor(context.getResources().getColor(R.color.accent));
            }
            count.setText(String.valueOf(comment.getUpVotes()));
            adapter.getListOfComments().sort(Comparator.comparing(Comment::getTimeStamp).reversed());
            adapter.notifyDataSetChanged();
        });

    }
}
