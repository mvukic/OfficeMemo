package ruazosa.hr.fer.officememo.View;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Observable;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.FirebaseHandler;
import ruazosa.hr.fer.officememo.Model.ObservableList;
import ruazosa.hr.fer.officememo.Model.Post;
import ruazosa.hr.fer.officememo.R;

public class NewPostActivity extends AppCompatActivity {
    ObservableList<Department> listOfDepartment = new ObservableList<>();
    private View currentView;
    private Post newPost = new Post();
    Spinner spinner;
    SpinnerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        spinner = (Spinner)findViewById(R.id.spinnerDepartments);
        adapter = new ArrayAdapter<Department>(this,R.layout.support_simple_spinner_dropdown_item, listOfDepartment.getList());
        spinner.setAdapter(adapter);
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance().getReference().child("departments"),
                DataSnapshotMapper.listOf(Department.class))
                .subscribe(departments -> {
                    listOfDepartment.clear();
                    departments.forEach(department -> listOfDepartment.add(department));
                });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                newPost.setDid(((Department)parent.getItemAtPosition(position)).getDid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newPost.setDid("");
            }
        });
    }

}
