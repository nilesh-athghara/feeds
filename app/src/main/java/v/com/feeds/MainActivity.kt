package v.com.feeds

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_records.*
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.properties.Delegates

//declare a new class to store received data
class Feedentry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageUrl: String = ""
    //it is use ful to declare get string methods in a class to get back data
    //here we will orevride the to_string method to use it in our way
    //the defauult toString method will just return the hash code of each object
    override fun toString(): String {
        return """
            name=$name
            artist=$artist
            release date=$releaseDate
            summary=$summary
            image url=$imageUrl
        """.trimIndent()//trim deletes extra spaces that occured due to code styling
    }
}

class MainActivity : AppCompatActivity() {
    private val tag: String = "mainActivity"
    private var objectOfAsyncTask:DownloadData?=null
    //by the time we will use this instance the widgets will be already created beacuse of by lazy
    private var feedurl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedlimit=10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "onCreate Called")

        //create instance of your Async class to run your task
        // val objectOfAsyncTask = DownloadData(this, mainXmlListview)//android studio did synthetic import no need to call it from r.id
//        val firstDownload= DownloadData(this,mainXmlListview)
//        firstDownload.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        downloadurl(feedurl.format(feedlimit))//string.fromat method is used to replace the %d
        Log.d(tag, "onCreate is completed")
    }


    private fun downloadurl(feedurl:String)
    {
        //one instance of async task can be executed only once
        //so we will have to make object every time
        objectOfAsyncTask=DownloadData(this,mainXmlListview)
        objectOfAsyncTask?.execute(feedurl)

    }

    //compnian object is similar to static nested class
    companion object {
        //declare a inner class to download data by inheriting ASync class
        private class DownloadData(context: Context, listview: ListView) : AsyncTask<String, Void, String>() {
            val tag = "String"
            /* var propContext:Context=context
             var propListview:ListView=listview*/

            var propContext: Context by Delegates.notNull()
            var propListview: ListView by Delegates.notNull()

            //compaion object was created to instead of inner class to prevent data leaks
            //but passing context would have done the same so we use delegates
            init {
                propContext = context
                propListview = listview
            }

            override fun doInBackground(vararg params: String?): String {
                //we can pass many parameteres in vararg and can be assesced like p[1],p[2]
                Log.d(tag, "doInBackground Started with $params")
                val rssfeed = downloadXML(params[0])//download xml will also run in async
                if (rssfeed.isEmpty()) {
                    Log.e(tag, "error downloading xml")
                }
                //result is passed as an array
                return rssfeed
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
//                Log.d(tag, "onPostExecute called content is $result")
                val parseApplication = ParseClass()
                parseApplication.parse(result)

                //arrayAdaptetr
                //we need to give it a context same as in recycler view
                //this is a very basic arrayadapter provided by android we have very few customization options
              /*  val arrayAdapter = ArrayAdapter<Feedentry>(propContext, R.layout.list_items, parseApplication.applications)//application is an arraylist in parse class
                propListview.adapter = arrayAdapter*/
                //Now we have our custom adapter
                val feedadapter=FeedAdapter(propContext,R.layout.list_records,parseApplication.applications)
                propListview.adapter=feedadapter
            }

            private fun downloadXML(urlPath: String?): String {
                val xmlResult = StringBuilder()
                //we want a string because we need to constantly append the received result it is more efficient than concatenating strings
                //we use try block because may be the url can be wrong or net connection may not be available etc
                try {
                    val url = URL(urlPath)//url is obtained
                    //open connection on url
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    val response = connection.responseCode
                    Log.d(tag, "the response code was $response")
//            val inputStream=connection.inputStream
//            val inputStreamReader=InputStreamReader(inputStream)
//            val reader=BufferedReader(inputStreamReader)
                    // val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    //here buffered reader reads characters not strings
                    //so we need a character array to store them
                    /*  val inputbuffer = CharArray(500)
                      var charRead = 0
                      while (charRead >= 0) {
                          charRead = reader.read(inputbuffer)
                          if (charRead > 0)//i have checked it again because may be it is possible than 0 is returned but it is not end of file ,so there is no need to append it
                          {
                              xmlResult.append(String(inputbuffer, 0, charRead))
                          }
                      }
                      reader.close()
                      Log.d(tag, "Received ${xmlResult.length} bytes")*/
                    //reading the data in kotlin style of way

                    //connection.inputStream.buffered().reader().use { reader -> xmlResult.append(reader.readText()) }
                    //no need to write reader as a parameter as there is only one parameter
                    connection.inputStream.buffered().reader().use { xmlResult.append(it.readText()) }
                    return xmlResult.toString()
                }
                //it is important to have an order of the catch blocks beacuse malformed url is a subclass of ioexception  so if we implement it first malformed will never be called
//                catch (e: MalformedURLException) {
//                    Log.e(tag, "Invalid url ${e.message}")//message method returns the exact error
//                } catch (e: IOException) {
//                    Log.e(tag, "IO exception reading data ${e.message}")
//                } catch (e: SecurityException) {
//                    Log.e(tag, "security exception ${e.message}")
//                } catch (e: Exception) {
//                    Log.e(tag, "unknown exception ${e.message}")
//                }
                //writing catch in kotlin type code
                catch (e: Exception) {
                    val error = when (e) {
                        is MalformedURLException -> "invalid url ${e.message}"
                        is IOException -> "io exception reading data ${e.message}"
                        is SecurityException -> "security exception ${e.message}"
                        else -> "unknown exception ${e.message}"
                    }
                }
                return " " //if execution has reached here there has been an error and one of the catch blocks have been executed so we have returned an empty string
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        objectOfAsyncTask?.cancel(true)//to stop if we has exit the application
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        if(feedlimit==10)
        {
            menu?.findItem(R.id.menu_top10)?.isChecked=true
        }
        else
        {
            menu?.findItem(R.id.menu_top25)?.isChecked=true
        }
        return true//return true to tell android that we have a menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.menu_Free->feedurl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.menu_paid->feedurl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.menu_songs->
            {
                feedurl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            }
            R.id.menu_top10,R.id.menu_top25->
            {
                if(!item.isChecked)
                {
                    item.isChecked=true
                    feedlimit=35-feedlimit
                    //here 35-10=25 and 35-25=10
                    //a very good logic to iterate between two variables
                }
            }
            else-> return super.onOptionsItemSelected(item)
        }
        downloadurl(feedurl.format(feedlimit))
        return true
    }

}
