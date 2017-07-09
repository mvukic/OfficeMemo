package ruazosa.hr.fer.officememo.View;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Controller.*;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.google.android.gms.games.snapshot.Snapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ruazosa.hr.fer.officememo.Model.Comment;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.R;
import ruazosa.hr.fer.officememo.Utils.GlobalData;

public class CommentActivity extends AppCompatActivity {
    String pid;
    RecyclerView recyclerView;
    CommentAdapter adapter;
    TextInputEditText commentEditText;
    ImageView commentButton;
    private List<Comment> listOfComments= new ArrayList<>();
    ProgressDialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        pid = getIntent().getStringExtra("pid");
        if(pid==null || pid.isEmpty())
            finish();
        instanceViews();
        addListeners();
    }

    private void addListeners() {
       fetchComments();
        RxView.clicks(commentButton).subscribe(o -> {
            if(commentEditText.getText().toString().isEmpty())
            {
                Snackbar.make(getCurrentFocus(), "Comment can't be empty", Snackbar.LENGTH_SHORT).show();
                return;
            }
            ProgressDialog commentDialog =  ProgressDialog.show(CommentActivity.this, "",
                    "Posting comment. Please wait...", true);
            Comment comment = new Comment(GlobalData.INSTANCE.getUser().getUid(),
                    commentEditText.getText().toString(), OfficeMemo.timeStampToString(new Date()),0);

            RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance()
                    .getReference("posts").child(pid), DataSnapshotMapper.of(Post.class))
                    .subscribe(post -> {
                        listOfComments.add(comment);
                        post.setComments(listOfComments);
                        FirebaseDatabase.getInstance().getReference("posts").child(post.getPid())
                                .setValue(post);
                        commentEditText.setText(null);
                        commentDialog.dismiss();
                        fetchComments();
                    });
        });
        swipeRefreshLayout.setOnRefreshListener(() -> fetchComments());
    }

    private void fetchComments() {
        dialog =  ProgressDialog.show(CommentActivity.this, "",
                "Fetching comments. Please wait...", true);
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance()
                .getReference("posts").child(pid), DataSnapshotMapper.of(Post.class))
                .subscribe(post -> {
                    listOfComments.clear();
                    post.getComments().forEach(comment -> listOfComments.add(comment));
                    listOfComments.sort(Comparator.comparing(Comment::getTimeStamp).reversed());
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                });


    }

    private void instanceViews() {
        recyclerView = (RecyclerView)findViewById(R.id.recylerViewComment);
        adapter = new CommentAdapter(this, listOfComments);
        LinearLayoutManager llm = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        commentEditText = (TextInputEditText) findViewById(R.id.editTextComment);
        commentButton = (ImageView)findViewById(R.id.imageViewCommentSend);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshComment);

    }
}
