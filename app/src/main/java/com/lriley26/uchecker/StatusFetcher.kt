package com.lriley26.uchecker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Serializable
data class ResponseData(
    val data: ResultData
)

@Serializable
data class ResultData(
    val result: ResultDetail
)

@Serializable
data class ResultDetail(
    val createOrderEnabled: Boolean,
    val reason: String
)

suspend fun getStatus(postData: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
    val url = "https://phoenix.ujing.online/api/v1/wechat/devices/scanWasherCode"
    val headers = mapOf(
        "Accept" to "application/json, text/plain, */*",
        "Authorization" to "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHBVc2VySWQiOiJvZ3lSVDF1M0dlZU9OV2N5SGdHekZYM3RoLVVNIiwiZXhwIjoxNzAyMjgzNTg4LCJpYXQiOjE2OTQyNDgzODgsImlkIjozMDE5NDgzMiwibmFtZSI6IjE5ODc2NTc2NzY4In0.N83KdLj5-3DuyaY4-n9lsocUpq71QwnCvB4Ox7FL1D0",
        "Accept-Language" to "zh-CN,zh-Hans;q=0.9",
        "Accept-Encoding" to "gzip, deflate, br",
        "Content-Type" to "application/json; charset=utf-8",
        "x-app-code" to "BCI",
        "Origin" to "https://wx.zhinengxiyifang.cn",
        "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 15_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.33(0x18002121) NetType/WIFI Language/zh_CN",
        "Referer" to "https://wx.zhinengxiyifang.cn/",
        "x-app-version" to "0.1.40",
        "Connection" to "keep-alive"
    )

    val data = mapOf("qrCode" to postData)
    val body =
        Json.encodeToString(data).toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .headers(headers.toHeaders())
        .post(body)
        .build()

    val responseText = OkHttpClient().newCall(request).execute().body!!.string()

    val jsonObject = JSONObject(responseText)
    val createOrderEnabled =
        jsonObject.getJSONObject("data").getJSONObject("result").getBoolean("createOrderEnabled")
    val reason = jsonObject.getJSONObject("data").getJSONObject("result").getString("reason")
    return@withContext Pair(createOrderEnabled, reason)
}

suspend fun returnStatus(urlList: List<String>): String {
    var result = ""
    for (i in urlList.indices) {
        result += "第 ${i + 1} 层洗衣机状态："
        val status = getStatus(urlList[i])
        if (status.first) {
            result += "可以洗衣\n\n"
        } else {
            result += "不可以洗衣，原因：${status.second}\n\n"
        }
    }
    return result
}