package v.com.feeds

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

//we will declare a view holder class so that findviewbyid doesnt have to be called again and again for the same views
//we will store them here
//this is called view holder pattern
class ViewHolder(v:View)
{
    val tvName=v.findViewById<TextView>(R.id.list_reords_name)
    val tvartist=v.findViewById<TextView>(R.id.list_records_artist)
    val tvsummary=v.findViewById<TextView>(R.id.list_records_summary)
}



class FeedAdapter(context:Context,private val resource:Int,private val applications:List<Feedentry>) :ArrayAdapter<Feedentry>(context,resource,applications) {
    private val tag="feed adapter"
    //inflate the xml resiurces to create a view
    private val inflator=LayoutInflater.from(context)

    override fun getCount(): Int {
        Log.d(tag,"getCount() called")
        return applications.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d(tag,"getView Called")
        //val view=inflator.inflate(resource,parent,false)
        //problenm here is it inflates a new view every time getVIew is called thus not efficient
        //Findview by id functions are slow
        val view:View
        var viewholder:ViewHolder
        if (convertView==null)
        {
            Log.d("view","new View Created")
            view =inflator.inflate(resource,parent,false)
            viewholder=ViewHolder(view)//creates a new object when new view is inflated
            view.tag=viewholder
        }
        else
        {
            Log.d("view","older view used")
            view=convertView
            viewholder=view.tag as ViewHolder
        }
//        val tvName=view.findViewById<TextView>(R.id.list_reords_name)
//        val tvartist=view.findViewById<TextView>(R.id.list_records_artist)
//        val tvsummary=view.findViewById<TextView>(R.id.list_records_summary)

        val currentApp=applications[position]
        viewholder.tvName.text=currentApp.name
        viewholder.tvartist.text=currentApp.artist
        viewholder.tvsummary.text=currentApp.summary//it can hold these feilds as application is a generic list as feed entry
        return view
    }
}