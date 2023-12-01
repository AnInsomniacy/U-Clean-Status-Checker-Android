package com.lriley26.uchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.widget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //获取引用
        val button_check = findViewById<Button>(R.id.button_check)
        val button_about = findViewById<Button>(R.id.button_about)
        val resultshowerScroll = findViewById<ScrollView>(R.id.resultshower)
        val resultshower= resultshowerScroll.findViewById<TextView>(R.id.text_view)
        val buildingSpinner = findViewById<Spinner>(R.id.building)
        val sideSpinner = findViewById<Spinner>(R.id.side)

        //预设reslutshower的内容
        resultshower.setText("欢迎使用 U Clean Status Checker\n\n请在上方选择相关信息，然后点击“查询”按钮\n\n如需支持更多宿舍楼的洗衣机，请根据 ”关于“ 按钮中的提示，协助作者获取必要信息。\n\n代码已开源，欢迎各位前来指点\n\n本项目GitHub地址:\n\nhttps://github.com/CarlWtrs/U-Clean-Status-Checker-Android")

        //设置buildingSpinner的选项为“东十九”和“西一”
        val buildingList = arrayOf("东十九", "西一")
        buildingSpinner.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buildingList)

        //根据buildingSpinner的选项设置sideSpinner的选项，如果buildingSpinner的选项为“东十九”，则sideSpinner的选项为“兆基”和“常工”，如果buildingSpinner的选项为“西一”，则sideSpinner的选项为“混合”
        buildingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (buildingSpinner.selectedItem.toString() == "东十九") {
                    val sideList = arrayOf("兆基", "常工")
                    sideSpinner.adapter = ArrayAdapter<String>(
                        this@MainActivity, android.R.layout.simple_spinner_item, sideList
                    )
                } else if (buildingSpinner.selectedItem.toString() == "西一") {
                    val sideList = arrayOf("暂不支持")
                    sideSpinner.adapter = ArrayAdapter<String>(
                        this@MainActivity, android.R.layout.simple_spinner_item, sideList
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }


        //单击button_check时，执行getStatus函数
        button_check.setOnClickListener {
            // 启动协程
            //清空resultshower，显示“正在查询...”
            resultshower.setText("正在查询...")
            GlobalScope.launch {
                //如果楼层是“东十九”，进一步判断侧面是“兆基”还是“常工”
                var result = ""
                if (buildingSpinner.selectedItem.toString() == "东十九") {
                    if (sideSpinner.selectedItem.toString() == "兆基") {
                        //如果侧面是“兆基”，则执行getStatus函数，参数为“东十九兆基”
                        //拼接result前置提示，如“东十九楼兆基侧洗衣机状态:”
                        result = "东十九楼兆基侧洗衣机状态:\n\n"
                        result = result + returnStatus(D19_ZhaoJi)
                    } else if (sideSpinner.selectedItem.toString() == "常工") {
                        //如果侧面是“常工”，则执行getStatus函数，参数为“东十九常工”
                        //拼接result前置提示，如“东十九楼常工侧洗衣机状态:”
                        result = "东十九楼常工侧洗衣机状态:\n\n"
                        result = result + returnStatus(D19_ChangGong)
                    }
                } else if (buildingSpinner.selectedItem.toString() == "西一") {
                    result = "暂不支持，请根据 ”关于“ 按钮中的提示，协助作者获取必要信息。"
                }
                //更新UI
                withContext(Dispatchers.Main) {
                    resultshower.setText(result)
                }
            }
        }

        //定义about函数
        fun about() {
            //在reslutshower中显示关于信息
            resultshower.setText("本项目GitHub地址:  \n\nhttps://github.com/AnInsomniacy\n\n如需支持更多宿舍楼的洗衣机，请协助作者获取对应宿舍楼的洗衣机URL\n\n洗衣机的二维码扫描之后，会得到一串指向这台机器的链接\n\n请前往GitHub提交issue，并附加洗衣机二维码扫描后得到的链接。\n\n请按照以下格式提交:    xx楼xx侧xx层洗衣机:扫码得到的url\n\n如:    东十九楼兆基侧一层洗衣机:http://app.littleswan.com/u_download.html?type=Ujing& uuid=0000000000000A0007555201809040059546\n\n作者将及时更新并提供支持服务本程序仅供学习交流使用,请勿用于非法用途\n\n代码已开源，欢迎各位前来指点")
        }

        //单击button_about时，执行about函数
        button_about.setOnClickListener {
            about()
        }
    }
}