package com.allever.app.ui

import android.os.Bundle
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = ArrayList<String>()

        list.add("3333")
        list.add("1")
        list.add("22222222222222222222222222222222222222222222")
        list.add("清空万里")
        list.add("两条相交线")
        list.add("短途")
        list.add("希望在明天会更好")

        val autoAdapter = MyAutoAdapter(this)
        autoAdapter.setOnItemClickListener { view, text ->
            toast(text)
        }
        autoAdapter.setData(list)
        autoLayout.setAdapter(autoAdapter)
    }
}
