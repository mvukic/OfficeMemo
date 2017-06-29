package ruazosa.hr.fer.officememo.Model;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by shimu on 29.6.2017..
 */

public class ObservableList<T> {
    protected final List<T> list;
    protected final PublishSubject<T> onAdd;

    public ObservableList() {
        this.list = new ArrayList<T>();
        this.onAdd = PublishSubject.create();
    }
    public void clear(){
        list.clear();
    }
    public void add(T value) {
        list.add(value);
        onAdd.onNext(value);
    }
    public Observable<T> getObservable() {
        return onAdd;
    }

}
