package ruazosa.hr.fer.officememo

import android.os.Bundle
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
