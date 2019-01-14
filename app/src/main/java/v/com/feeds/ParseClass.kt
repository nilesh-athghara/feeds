package v.com.feeds

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.lang.Exception

class ParseClass {
    private val tag = "Parseclass"
    //declare an array list to store received data
    val applications = ArrayList<Feedentry>()

    fun parse(xmlData: String): Boolean//boolean is given for chechking that data was passed or not
    {
        Log.d(tag, "parse funtion called with $xmlData")
        var status = true//this will be set to false if data was not received
        var inEntry = false//used to check if we are inside the tag or not
        var textValue = " "//used to store the value of current tag
        try {
            //setting up xml parser part of java libraries
            val factory = XmlPullParserFactory.newInstance()//produces a pull parser object
            //parse factory is used when we dont know exactly which class to use
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())//xml pull parser needs a string reader to read data
            //xpp is now a valid pull parser object
            var eventType = xpp.eventType
            var currentRecord = Feedentry()//instance of feedentry class
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //here we can check for the tags we want
                val tagName = xpp.name?.toLowerCase()//we should use the safe call operator here ? beacuse here we are trying to call to lowerCase method on a value that can be null
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        Log.d(tag, "parse : Starting tag for $tagName")
                        if (tagName == "entry") {
                            inEntry = true
                        }
                    }
                    XmlPullParser.TEXT -> {
                        textValue = xpp.text
                    }
                    XmlPullParser.END_TAG -> {
                        Log.d(tag, "parse : Ending tag for  $tagName")
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = Feedentry()//create a new object to clear pre3vious data
                                }
                                "name" -> {
                                    currentRecord.name = textValue
                                }
                                "artist" -> {
                                    currentRecord.artist = textValue
                                }
                                "releasedate" -> {
                                    currentRecord.releaseDate = textValue
                                }
                                "summary" -> {
                                    currentRecord.summary = textValue
                                }
                                "image" -> {
                                    currentRecord.imageUrl = textValue
                                    //there are three image tags in the xml file the overwrites them
                                    //to get a particular image we can check their height and width attribute
                                    //use get attibute value function()
                                }
                            }
                        }
                    }
                }
                //nothing else to do
                eventType = xpp.next()
            }
            for (app in applications) {
                Log.d(tag, "*********************************")
                Log.d(tag, app.toString())//to string is the overloadded function that we created in class defination
            }
        } catch (e: Exception) {
            e.printStackTrace()
            status = false

        }
        return status
    }
}