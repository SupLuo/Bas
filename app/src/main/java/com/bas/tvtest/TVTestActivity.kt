package com.bas.tvtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bas.droid.core.content.ActionIntent
import com.bas.R

class TVTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_test_activity)
    }

    fun onViewClick(v: View) {
        when(v.id){
            R.id.btn1 ->{
                startActivity(ActionIntent("uxtv://applink?uri=gdtv%3A%2F%2Fwgw%2Fcatg%3Fid%3D2501828091700140578").also {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
            R.id.btn2 ->{
                startActivity(ActionIntent("uxtv://applink?uri=gdtv%3A%2F%2Fwgw%2Fcatg%3Fid%3D2501828091700140576").also {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }
}