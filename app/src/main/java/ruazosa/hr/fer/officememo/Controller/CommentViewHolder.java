package ruazosa.hr.fer.officememo.Controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding2.view.RxView;

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
    TextView name, content, date;
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

    }
}
