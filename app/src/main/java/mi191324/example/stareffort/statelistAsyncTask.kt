package mi191324.example.stareffort

import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.HashMap
import kotlin.concurrent.thread

class statelistAsyncTask {
    fun httpPOST(url : String, list:ArrayList<*>) : Deferred<String?> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT, {
        val client = OkHttpClient()
        val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()
        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()
        return@async response.body?.string()
    })
}