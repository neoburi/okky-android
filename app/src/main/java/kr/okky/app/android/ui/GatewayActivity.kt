package kr.okky.app.android.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kr.okky.app.android.R
import kr.okky.app.android.global.MODE
import kr.okky.app.android.global.Mode


class GatewayActivity : BaseActivity() {
    val tag:String = "Okky"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gateway)
        /*crashlyticsTest()*/

        Handler().postDelayed({
            val mainIntent = Intent(baseContext, MainActivity::class.java)
            if(intent?.extras != null) {
                mainIntent.putExtras(intent?.extras)
            }
            startActivity(mainIntent)
            finish()
        },1500)
    }

    private fun crashlyticsTest(){
        getView<Button>(R.id.btn_test).setOnClickListener {
            Crashlytics.getInstance().crash() // Force a crash
        }
    }
}
