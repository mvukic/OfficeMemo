package ruazosa.hr.fer.officememo.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Controller.SubscriptionAdapter;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.ObservableList;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.R;

public class DepartmentSubscriptionActivity extends AppCompatActivity {
    List<Department> listOfDepartment = new ArrayList<>();
    SubscriptionAdapter adapter;
    RecyclerView recyclerView;
    ProgressDialog dialog;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_subscription);
        instanceViews();
        addListeners();
    }

    private void instanceViews() {
        recyclerView = (RecyclerView)findViewById(R.id.recylerViewSubscription);
        adapter = new SubscriptionAdapter(this, listOfDepartment);
        LinearLayoutManager llm = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        fab = (FloatingActionButton)findViewById(R.id.fabNewDepartment);
        dialog =  ProgressDialog.show(DepartmentSubscriptionActivity.this, "",
                "Fetching departments. Please wait...", true);
    }

    private void addListeners() {

        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance().getReference().child("departments"),
                DataSnapshotMapper.listOf(Department.class))
                .subscribe(departments -> {
                    listOfDepartment.clear();
                    listOfDepartment.addAll(departments);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                });
        RxView.clicks(fab).subscribe(o -> startActivity(new Intent(this, NewDepartmentActivity.class)));
    }
}
