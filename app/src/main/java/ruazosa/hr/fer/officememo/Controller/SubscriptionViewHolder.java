package ruazosa.hr.fer.officememo.Controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCheckedTextView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import ruazosa.hr.fer.officememo.Model.OfficeMemo;
import ruazosa.hr.fer.officememo.Model.User;
import ruazosa.hr.fer.officememo.R;

/**
 * Created by shimu on 1.7.2017..
 */

class SubscriptionViewHolder extends RecyclerView.ViewHolder{
    Context context;
    TextView name;
    CompoundButton toggle;
    SubscriptionAdapter adapter;
    ImageView profile;
    public SubscriptionViewHolder(Context context,View itemView, SubscriptionAdapter adapter) {
        super(itemView);
        this.context = context;
        this.adapter = adapter;
        name = (TextView) itemView.findViewById(R.id.textViewSubscription);
        toggle = (CompoundButton) itemView.findViewById(R.id.switchSubscription);
        profile = (ImageView) itemView.findViewById(R.id.imageViewSubscription);
        RxCompoundButton.checkedChanges(toggle).subscribe(aBoolean -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(OfficeMemo.getUserUid());
            RxFirebaseDatabase.observeSingleValueEvent(ref, DataSnapshotMapper.of(User.class))
                    .subscribe(user -> {
                        if(aBoolean){
                            user.getSubscriptions().add(adapter.getList().get(getAdapterPosition()).getDid());
                        }
                        else{
                            user.getSubscriptions().remove(adapter.getList().get(getAdapterPosition()).getDid());
                        }
                        ref.setValue(user);
                    });
        });

    }


}
