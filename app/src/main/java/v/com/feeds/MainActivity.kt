package v.com.feeds

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    private val tag:String="mainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag,"onCreate Called")

        //cretate instance of your Async class to run your task
        val objectOfAsyncTask=downloadData();
        objectOfAsyncTask.execute("url")
        Log.d(tag,"onCreate is completed")
    }

    //compnian object is similar to static nested class
    companion object {
        //declare a inner class to download data by inheriting ASync class
        private class downloadData: AsyncTask<String, Void, String>()
        {
            val tag="String"
            override fun doInBackground(vararg params: String?): String {
                Log.d(tag,"doInBackground Started with $params")
                return "Do in background completed"
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.d(tag,"onPostExecute called content is $result")
            }

        }
    }
}
