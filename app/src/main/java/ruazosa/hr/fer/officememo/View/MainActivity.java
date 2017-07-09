package ruazosa.hr.fer.officememo.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding2.view.RxView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.BaseActivity;
import ruazosa.hr.fer.officememo.Controller.FeedAdapter;
import ruazosa.hr.fer.officememo.Controller.SubscriptionAdapter;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.Utils.GlobalData;
import ruazosa.hr.fer.officememo.Model.User;
import ruazosa.hr.fer.officememo.R;

// TODO: 09.07.17. Feed by department (spinner) and change query
public class MainActivity extends BaseActivity {
    List<Post> listOfPosts = new ArrayList<>();
    public RecyclerView recyclerView;
    FeedAdapter adapter ;
    SwipeRefreshLayout swipeRefreshLayout;
    private Drawer drawer;
    private AccountHeader headerResult;
    ProgressDialog dialog;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        if(i.hasExtra("pid")){
            Log.d("MA","Notification pid: "+i.getStringExtra("pid"));
        }
        if(i.hasExtra("did")){
            Log.d("MA","Notification did: "+i.getStringExtra("did"));
        }
        if(i.hasExtra("uid")){
            Log.d("MA","Notification uid: "+i.getStringExtra("uid"));
        }
        // MaterialDrawer use Picasso
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

        User u = GlobalData.INSTANCE.getUser();
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(u.getName())
                                .withEmail(u.getEmail())
                                .withIcon(u.getProfileUrl())
                ).withOnAccountHeaderListener((view, profile, currentProfile) -> {
                    startActivity(new Intent(this,LoginAdditionalActivity.class));
                    return false;
                })
                .build();

        Picasso.with(this).load(u.getCoverUrl()).into(headerResult.getHeaderBackgroundView());

        drawer = new DrawerBuilder()
                .withToolbar(toolbar)
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(0).withName("Me"),
                        new PrimaryDrawerItem().withIdentifier(1).withName("New post"),
                        new PrimaryDrawerItem().withIdentifier(2).withName("New department"),
                        new PrimaryDrawerItem().withIdentifier(3).withName("Departments")
                )
                .addStickyDrawerItems(new PrimaryDrawerItem().withIdentifier(4).withName("Sign out"))
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    switch((int)drawerItem.getIdentifier()){
                        case 1:
                            startActivity(new Intent(this,NewPostActivity.class));
                            return false;
                        case 0:
                            startActivity(new Intent(this,LoginAdditionalActivity.class));
                            return false;
                        case 2:
                            startActivity(new Intent(this, NewDepartmentActivity.class));
                            return false;
                        case 3:
                            startActivity(new Intent(this, DepartmentSubscriptionActivity.class));
                            return false;
                        case 4:
                            signOut();
                            return false;
                        default:
                            break;
                    }
                    return true;
                })
                .withSelectedItem(-1)
                .build();
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshFeed);
        fab = (FloatingActionButton)findViewById(R.id.fabNewPost);
        recyclerView = (RecyclerView)findViewById(R.id.recylerViewFeed);
        adapter = new FeedAdapter(this, listOfPosts);
        LinearLayoutManager llm = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        addListeners();
    }

    private void addListeners() {
         fetchPosts();
         dialog.dismiss();
        swipeRefreshLayout.setOnRefreshListener(() -> fetchPosts());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    // Scroll Down
                    if (fab.isShown()) {
                        fab.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
            }
        });
        RxView.clicks(fab).subscribe(o -> startActivity(new Intent(this,NewPostActivity.class)));
    }

    private void fetchPosts() {
        dialog = ProgressDialog.show(MainActivity.this, "",
                "Fetching posts. Please wait...", true);
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance().getReference("posts"),
                DataSnapshotMapper.listOf(Post.class)).doOnError(throwable -> {

        }).subscribe(posts -> {
            listOfPosts.clear();
            posts.forEach(post -> listOfPosts.add(0,post));
            listOfPosts.sort(Comparator.comparing(Post::getTimeStamp).reversed());
            dialog.dismiss();
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

        });
    }

    public void refreshHeader(){
        User u = GlobalData.INSTANCE.getUser();
        Picasso.with(this).load(u.getCoverUrl()).into(headerResult.getHeaderBackgroundView());
        headerResult.getActiveProfile().withName(u.getName());
        headerResult.getActiveProfile().withEmail(u.getEmail());
        headerResult.getActiveProfile().withIcon(u.getProfileUrl());
        headerResult.removeProfile(0);
        headerResult.addProfile(
                new ProfileDrawerItem()
                .withName(u.getName())
                .withEmail(u.getEmail())
                .withIcon(u.getProfileUrl()),0);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(GlobalData.INSTANCE.getShouldRefreshHeader()){
            refreshHeader();
            GlobalData.INSTANCE.setShouldRefreshHeader(false);
        }
        fetchPosts();
    }
}
