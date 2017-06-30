package ruazosa.hr.fer.officememo.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import ruazosa.hr.fer.officememo.BaseActivity;
import ruazosa.hr.fer.officememo.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName("Mike Penz")
                                .withEmail("mikepenz@gmail.com")
                                .withIcon(getResources().getDrawable(R.drawable.gallery))
                )
                .build();

        new DrawerBuilder()
                .withToolbar(toolbar)
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("Test1"),
                        new PrimaryDrawerItem().withIdentifier(2).withName("Test2")
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    switch((int)drawerItem.getIdentifier()){
                        case 1:
                            startActivity(new Intent(this,NewPostActivity.class));
                            return false;
                        case 2:
                            startActivity(new Intent(this,LoginAdditionalActivity.class));
                            return false;
                        default:
                            break;
                    }
                    return true;
                })
                .build();
    }
}
