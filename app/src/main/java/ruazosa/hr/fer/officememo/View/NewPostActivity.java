package ruazosa.hr.fer.officememo.View;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Observable;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.R;

public class NewPostActivity extends AppCompatActivity {
    List<Department> listOfDepartment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        RxFirebaseDatabase.observeValueEvent(FirebaseDatabase.getInstance().getReference("departments"),
                DataSnapshotMapper.listOf(Department.class))
                .subscribe(departments -> {
                    listOfDepartment.clear();
                    listOfDepartment.addAll(departments);
                });
        Observable.fromIterable(listOfDepartment).subscribe(a->{
            Snackbar.make(getCurrentFocus(),listOfDepartment.toString(), Snackbar.LENGTH_LONG).show();
        });

    }

}
