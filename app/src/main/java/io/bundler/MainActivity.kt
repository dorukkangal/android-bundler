package io.bundler

import android.os.Bundle
import io.bundler.annotation.Bundler
import kotlinx.android.synthetic.main.activity_main.*

@Bundler
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonUserList.setOnClickListener {
            startActivity(
                    userListActivityIntent(
                            context = this,
                            userList = mock()
                    )
            )
        }
    }
}

private fun mock(): ArrayList<User> {
    return arrayListOf(
            User("Jessie", "Clarke"),
            User("Betty", "Terry"),
            User("Justin", "Jones"),
            User("Ethel", "King"),
            User("Alexander", "Nichols"),
            User("Loren", "Coleman"),
            User("Conrad", "Fitzgerald"),
            User("Brendan", "Henry"),
            User("John", "Patrick")
    )
}

