package ruazosa.hr.fer.officememo.View;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import ruazosa.hr.fer.officememo.BaseActivity;
import ruazosa.hr.fer.officememo.Controller.GlobalData;
import ruazosa.hr.fer.officememo.Controller.SubscriptionAdapter;
import ruazosa.hr.fer.officememo.Model.User;
import ruazosa.hr.fer.officememo.R;

public class MainActivity extends BaseActivity {

    private Drawer drawer;
    private AccountHeader headerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                        new PrimaryDrawerItem().withIdentifier(2).withName("Me"),
                        new PrimaryDrawerItem().withIdentifier(1).withName("New post"),
                        new PrimaryDrawerItem().withIdentifier(3).withName("New department"),
                        new PrimaryDrawerItem().withIdentifier(4).withName("Subscriptions")

                ).addStickyDrawerItems(new PrimaryDrawerItem().withIdentifier(5).withName("Sign out"))
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    switch((int)drawerItem.getIdentifier()){
                        case 1:
                            startActivity(new Intent(this,NewPostActivity.class));
                            return false;
                        case 2:
                            startActivity(new Intent(this,LoginAdditionalActivity.class));
                            return false;
                        case 3:
                            startActivity(new Intent(this, NewDepartmentActivity.class));
                            return false;
                        case 4:
                            startActivity(new Intent(this, DepartmentSubscriptionActivity.class));
                            return false;
                        case 5:
                            signOut(this);
                            return false;
                        default:
                            break;
                    }
                    return true;
                }).withSelectedItem(-1)
                .build();
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
        refreshHeader();
    }
}
