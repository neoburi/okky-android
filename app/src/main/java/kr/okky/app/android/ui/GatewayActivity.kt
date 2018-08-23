package kr.okky.app.android.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import kr.okky.app.android.R

class GatewayActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gateway)
        Handler().postDelayed({
            startActivity(Intent(baseContext, MainActivity::class.java))
            finish()
        },2000)
    }
}
