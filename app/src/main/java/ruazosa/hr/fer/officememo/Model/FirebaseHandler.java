package ruazosa.hr.fer.officememo.Model;

import android.net.Uri;

import com.github.b3er.rxfirebase.database.RxFirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import durdinapps.rxfirebase2.RxFirebaseStorage;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by shimu on 29.6.2017..
 */

public class FirebaseHandler {
    private static final String departmentRef="departments";
    private static final String userRef="users";
    private static final String postRef="posts";
    private static final String userDepartmentRef="usersdepartmentspair";

    /**
    * Returns key of department in firebase, saves to ref "departments"
     * @param department department object that you want to save to firebase database1
    *
*/
    public static String pushDepartment(Department department){
        DatabaseReference pushRef = FirebaseDatabase.getInstance().getReference(departmentRef).push();
        department.setDid(pushRef.getKey());
        pushRef.setValue(department);
        return pushRef.getKey();
    }

    /**
     * Returns user key after writing details to database.
     * @param user
     * @return Single<String>
     */
    public static Single<String> pushUser(User user){
        return Single.create(e -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userRef).child(user.getUid());
            ref.setValue(user);
            e.onSuccess(user.getUid());
        });
    }

    /**
     * Returns key of post in firebase, saves to ref "posts"
     * @param post post object that you want to save to firebase database1
     *
     * */
    public static String pushPost(Post post){
        DatabaseReference pushRef = FirebaseDatabase.getInstance().getReference(postRef).push();
        post.setPid(pushRef.getKey());
        pushRef.setValue(post);
        return pushRef.getKey();
    }

    public static Maybe<UploadTask.TaskSnapshot> pushUriToStorage(Uri uri, StorageReference ref){
        return RxFirebaseStorage.putFile(ref,uri);
    }

}
