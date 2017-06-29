package ruazosa.hr.fer.officememo.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ruazosa.hr.fer.officememo.BaseActivity;
import ruazosa.hr.fer.officememo.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,NewPostActivity.class);
        this.startActivity(intent);
    }
}
