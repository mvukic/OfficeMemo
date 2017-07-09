package ruazosa.hr.fer.officememo.View;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Controller.FeedAdapter;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.Model.User;
import ruazosa.hr.fer.officememo.R;

public class UserProfileActivity extends AppCompatActivity {
    String uid;
    ImageView cover, profile;
    TextView name, shortname, location, about;
    RecyclerView recyclerView;
    FeedAdapter adapter;
    ProgressDialog dialog;
    List<Post> listOfPosts = new ArrayList<>();
    public FrameLayout frame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uid = getIntent().getStringExtra("uid");
        if(uid== null || uid.isEmpty())
            finish();

        instanceViews();
        addListeners();
    }

    private void addListeners() {
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance()
                        .getReference("users").child(uid),
                DataSnapshotMapper.of(User.class)).subscribe(user -> {
            fillUserData(user);
        });
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance()
                        .getReference("posts").orderByChild("uid").equalTo(uid),
                DataSnapshotMapper.listOf(Post.class)).doOnError(throwable -> dialog.dismiss()).subscribe(posts -> {
            listOfPosts.clear();
            posts.forEach(post -> listOfPosts.add(post));
            listOfPosts.sort(Comparator.comparing(Post::getTimeStamp).reversed());
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
    }

    private void fillUserData(User user) {
        name.setText(user.getName() + " " + user.getLastName());
        shortname.setText(user.getEmail());
        location.setText("Location: " + user.getLocation());
        about.setText("About me: " +  user.getAboutMe());
        OfficeMemo.setImageToViewFullWidthUserProfile(this, cover, Uri.parse(user.getCoverUrl()));
        OfficeMemo.setImageToViewFullCircle(this, profile, Uri.parse(user.getProfileUrl()), 400,400);
    }

    private void instanceViews() {
        frame = (FrameLayout)findViewById(R.id.frameLayoutDep);
        cover = (ImageView)findViewById(R.id.imageViewProfileCover);
        profile = (ImageView)findViewById(R.id.imageViewProfileProfile);
        name = (TextView)findViewById(R.id.textViewProfileName);
        shortname = (TextView)findViewById(R.id.textViewProfileName2);
        location = (TextView)findViewById(R.id.textViewProfileLocation);
        about = (TextView)findViewById(R.id.textViewProfileAbout);
        recyclerView = (RecyclerView)findViewById(R.id.recylerViewFeedProfile);
        adapter = new FeedAdapter(this, listOfPosts);
        LinearLayoutManager llm = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        dialog =  ProgressDialog.show(UserProfileActivity.this, "",
                "Fetching user. Please wait...", true);

    }
}
