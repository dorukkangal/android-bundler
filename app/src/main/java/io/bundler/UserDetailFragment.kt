package io.bundler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.bundler.annotation.Arg
import io.bundler.annotation.Bundler
import kotlinx.android.synthetic.main.recycler_item_user.*

@Bundler
class UserDetailFragment : DialogFragment() {

    @Arg
    lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BundlerInjector.inject(this)

        textViewName.text = user.name
        textViewSurname.text = user.surname
    }
}
