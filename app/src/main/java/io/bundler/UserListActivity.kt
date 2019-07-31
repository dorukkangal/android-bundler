package io.bundler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.bundler.annotation.Arg
import io.bundler.annotation.Bundler
import kotlinx.android.synthetic.main.activity_user_list.*
import kotlinx.android.synthetic.main.recycler_item_user.view.*

@Bundler(inherited = false)
class UserListActivity : BaseActivity() {

    @Arg
    lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = UserListAdapter(userList) {
            userDetailFragmentInstance(it).show(supportFragmentManager, "UserDetail")
        }
    }
}

class UserListAdapter(
        private val userList: List<User>,
        private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindItem(userList[position])
        holder.itemView.setOnClickListener {
            onItemClick(userList[position])
        }
    }

    override fun getItemCount(): Int = userList.size
}

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindItem(user: User) {
        itemView.textViewName.text = user.name
        itemView.textViewSurname.text = user.surname
    }
}
