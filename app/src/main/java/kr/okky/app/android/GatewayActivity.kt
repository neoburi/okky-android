package kr.okky.app.android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import kr.okky.app.android.global.PERMISSIONS
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class GatewayActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gateway)
        if(!hasAppPermission()){
            requestAppPermission()
        }else{
            Handler().postDelayed({gotoMain()},2000)
        }
    }

    private fun gotoMain(){
        startActivity(Intent(baseContext, MainActivity::class.java))
        finish()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (!hasAppPermission()) {
            toast(R.string.txt_permission_denied)
        }
        gotoMain()
    }

    override fun requestAppPermission() {
        EasyPermissions.requestPermissions(this,
                getString(R.string.txt_permission_desc_1), 5001,
                *PERMISSIONS)
    }

    override fun hasAppPermission():Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return EasyPermissions.hasPermissions(this, *PERMISSIONS)
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
