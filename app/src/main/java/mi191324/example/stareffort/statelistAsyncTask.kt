package mi191324.example.stareffort

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class statelistAsyncTask {
    fun httpPOST(url: String, flist: ArrayList<String>) : Deferred<String?> = GlobalScope.async(
        Dispatchers.Default,
        CoroutineStart.DEFAULT,
        {
            val client: OkHttpClient = OkHttpClient.Builder().build()
            val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()
            val Body = "{\"ids\":\"%s\"}".format(flist)
            Log.d("pushtxt", Body)
            val request: Request = Request.Builder().url(url).post(Body.toRequestBody(JSON_MEDIA)).build()
            val response = client.newCall(request).execute()

            val responseBody = response.body?.toString().orEmpty()

            return@async responseBody
        })
}