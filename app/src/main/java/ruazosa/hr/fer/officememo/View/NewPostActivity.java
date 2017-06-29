package ruazosa.hr.fer.officememo.View;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Observable;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.ObservableList;
import ruazosa.hr.fer.officememo.R;

public class NewPostActivity extends AppCompatActivity {
    ObservableList<Department> listOfDepartment = new ObservableList<>();
    private View currentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        RxFirebaseDatabase.observeValueEvent(FirebaseDatabase.getInstance().getReference().child("departments"),
                DataSnapshotMapper.listOf(Department.class))
                .subscribe(departments -> {
                    listOfDepartment.clear();
                    departments.forEach(department -> listOfDepartment.add(department));
                });
        listOfDepartment.getObservable().subscribe(department -> {
            Snackbar.make((View) findViewById(R.id.textView), department.toString(), Snackbar.LENGTH_LONG).show();
        });



    }

}
