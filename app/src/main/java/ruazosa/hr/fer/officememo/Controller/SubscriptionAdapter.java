package ruazosa.hr.fer.officememo.Controller;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Model.Department;
import ruazosa.hr.fer.officememo.Model.FirebaseHandler;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.Model.User;
import ruazosa.hr.fer.officememo.R;

/**
 * Created by shimu on 1.7.2017..
 */

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionViewHolder> {
    Context context;
    List<Department> listOfDepartments;

    public SubscriptionAdapter(Context context, List<Department> listOfDepartments) {
        this.context = context;
        this.listOfDepartments = listOfDepartments;
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_item, parent, false);
        return new SubscriptionViewHolder(context, view, this);
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {
        Department department = listOfDepartments.get(position);
        holder.name.setText(department.toString());
        OfficeMemo.setImageToViewFullCircle(context, holder.profile, Uri.parse(department.getImageUrl()), 250,250);
        if(position == listOfDepartments.size()-1)
            holder.deli.setVisibility(View.INVISIBLE);
        else
            holder.deli.setVisibility(View.VISIBLE);
        RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance().getReference("users")
                .child(OfficeMemo.getUserUid()), DataSnapshotMapper.of(User.class)).subscribe(user -> {
                    holder.toggle.setChecked(user.getSubscriptions().contains(department.getDid()));
        });
    }

    @Override
    public int getItemCount() {
        return listOfDepartments.size();
    }

    public List<Department> getList(){
        return listOfDepartments;
    }
}
