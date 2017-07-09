package ruazosa.hr.fer.officememo.View;

import android.app.ProgressDialog;
import android.media.Image;
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
import ruazosa.hr.fer.officememo.R;

public class DepartmentProfileActivity extends AppCompatActivity {
    String did;
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
        did = getIntent().getStringExtra("did");
        if(did.isEmpty())
            finish();
        createInstances();
        addListeners();
    }

    private void createInstances() {
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
        dialog =  ProgressDialog.show(DepartmentProfileActivity.this, "",
                "Fetching department. Please wait...", true);


    }

    private void addListeners() {
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance()
                .getReference("departments").child(did),
                DataSnapshotMapper.of(Department.class)).subscribe(department -> {
                    fillDepartmentData(department);
        });
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance()
                .getReference("posts").orderByChild("did").equalTo(did),
                DataSnapshotMapper.listOf(Post.class)).doOnError(throwable -> dialog.dismiss()).subscribe(posts -> {
                    listOfPosts.clear();
                    posts.forEach(post -> listOfPosts.add(post));
                    listOfPosts.sort(Comparator.comparing(Post::getTimeStamp).reversed());
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
        });


    }

    private void fillDepartmentData(Department department) {
        name.setText(department.getName());
        shortname.setText(department.getShortName());
        location.setText("Location: " + department.getLocation());
        about.setText("About: " +  department.getAbout());
        OfficeMemo.setImageToViewFullWidthDepartmentProfile(this, cover, Uri.parse(department.getCoverUrl()));
        OfficeMemo.setImageToViewFullCircle(this, profile, Uri.parse(department.getImageUrl()), 400,400);

    }
}
