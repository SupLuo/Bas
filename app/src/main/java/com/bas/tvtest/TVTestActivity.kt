package com.bas.tvtest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import bas.droid.core.content.ActionIntent
import bas.droid.core.ui.toast
import bas.lib.core.lang.orDefaultIfNullOrEmpty
import bas.lib.core.lang.toUrlEncode
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
            R.id.open_player_btn ->{
                openTVPlayer()
            }
        }
    }

    private fun openTVPlayer(){
        val url = findViewById<EditText>(R.id.player_url_edit).text.toString()
        if(url.isEmpty()){
            toast("请输入播放地址")
            return
        }


        val intent = Intent("gdtv.intent.action.VIEW", Uri.parse("gdtv://intent/player?url=${url.toUrlEncode()}&islive=false"))
        startActivity(intent)
    }

//    gdtv://intent/player?url={必填：播放地址}&islive={必填boolean类型：是否是直播}&title={选填}&current={开始播放位置，单位s；点播可选，直播忽略}"
}