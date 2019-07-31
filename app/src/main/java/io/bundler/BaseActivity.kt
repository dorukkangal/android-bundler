package io.bundler

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.bundler.annotation.Arg

abstract class BaseActivity : AppCompatActivity() {

    @Arg
    var startMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BundlerInjector.inject(this)

        startMessage?.let {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }
}
