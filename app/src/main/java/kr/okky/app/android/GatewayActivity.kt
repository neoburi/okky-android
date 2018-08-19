package kr.okky.app.android

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class GatewayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gateway)
        Handler().postDelayed(
                    {
                        startActivity(Intent(baseContext, MainActivity::class.java))
                        finish()
                    },
                2000)
    }
}
