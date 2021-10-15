package mi191324.example.stareffort

import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException
import java.util.HashMap
import kotlin.concurrent.thread


class statelistAsyncTask {
    fun httpPOST(url: String, flist: ArrayList<String>) : Deferred<String?> = GlobalScope.async(
        Dispatchers.Default,
        CoroutineStart.DEFAULT,
        {
            val client: OkHttpClient = OkHttpClient()
            val body: FormBody = FormBody.Builder()
                .add("ids", flist.toString())
                .build()
            val request = Request.Builder().url(url).post(body).build()
            val response = client.newCall(request).execute()
            val resbody = response.body!!.string()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string().orEmpty()
                    Log.d("bodycheck", responseBody)
                }
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("Error--", e.toString())
                }
            })
            Log.d("sss", resbody)

            return@async request.toString()
        })

    fun POST(url: String, flist: ArrayList<String>) : Deferred<String> = GlobalScope.async(
        Dispatchers.Default,
        CoroutineStart.DEFAULT,
        {
            //val recycle = findViewById(R.id.recycle) as RecyclerView
            var respon = "null"
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val requestAdapter = moshi.adapter(MainActivity.ids::class.java)
            val header: HashMap<String, String> = hashMapOf("Content-Type" to "application/json")
            val pushtext = MainActivity.ids(ids = flist)
            val httpAsync = url
                .httpPost()
                .header(header).body(requestAdapter.toJson(pushtext))
                .responseString() {request, response, result ->
                    Log.d("hoge", result.toString())
                    when (result){
                        is Result.Failure -> {
                            val ex = result.getException()
                            Log.d("response", ex.toString())
                        }
                        is Result.Success -> {
                            val data = result.get()
                            val res = moshi.adapter(MainActivity.statelistresponce::class.java).fromJson(data)
                            Log.d("res", res.toString())
                            respon = res.toString()
                        }
                    }
                }
            httpAsync.join()

            return@async respon
        })
}