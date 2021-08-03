package com.example.splash.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.splash.R
import com.example.splash.commons.TwitterCloneAppData
import com.example.splash.commons.autoNotify
import com.example.splash.model.Tweet
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.row_layout_tweet.view.*
import kotlin.properties.Delegates

interface TweetAdapterListener {
    fun onUpdateTweetTapped(tweet: Tweet)
    fun onDeleteTweetTapped(tweet: Tweet)
}
class TweetAdapter(private val listener: TweetAdapterListener): RecyclerView.Adapter<TweetAdapter.ViewHolder>() {

    var tweets: List<Tweet> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout_tweet, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TweetAdapter.ViewHolder, position: Int) {
        holder.tvTweet.text = tweets[position].tweetText
        holder.userName.text = tweets[position].userData?.name
        holder.tvTime.text = tweets[position].timestamp?.let { getTimeString(it) }
        if (TwitterCloneAppData.getUser()?.id == tweets[position].userData?.id) {
//            holder.btnUpdateTweet.visibility = View.VISIBLE
//            holder.btnDeleteTweet.visibility = View.VISIBLE
            holder.btnMenuTweet.visibility = View.VISIBLE
        }


//        holder.btnUpdateTweet.setOnClickListener { listener.onUpdateTweetTapped(tweets[position]) }
//        holder.btnDeleteTweet.setOnClickListener { listener.onDeleteTweetTapped(tweets[position]) }
        holder.btnMenuTweet.setOnClickListener {

            val popupMenu: PopupMenu = PopupMenu(holder.btnMenuTweet.context, holder.btnMenuTweet)
            popupMenu.menuInflater.inflate(R.menu.pop_menu,popupMenu.menu)
            //popupMenu.show()
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.edit ->{
                        listener.onUpdateTweetTapped(tweets[position])
                        Toast.makeText(holder.btnMenuTweet.context,"Updated", Toast.LENGTH_SHORT).show()
                    }


                    R.id.delete ->{
                        listener.onDeleteTweetTapped(tweets[position])
                        Toast.makeText(holder.btnMenuTweet.context,"Deleted", Toast.LENGTH_SHORT).show()
                    }


                }
                true
            })
            popupMenu.show()



        }
    }

    private fun getTimeString(timeStamp: Timestamp) : String {
        val tweetTime = timeStamp.seconds
        val currentTime = System.currentTimeMillis()/1000
        val diffSeconds = currentTime - tweetTime
        return if (diffSeconds <= 60) {
            "Just now"
        } else if (diffSeconds in 61..3599) {
            val minute = diffSeconds/60
            "${minute}m ago"
        } else if (diffSeconds in 3601..86399) {
            val hour = diffSeconds/3600
            "${hour}h ago"
        } else {
            val day = diffSeconds/86400
            "${day}d ago"
        }
    }


    override fun getItemCount(): Int = tweets.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var tvTweet: AppCompatTextView = v.tvTweet
        val userName: AppCompatTextView = v.tvName
        val tvTime: AppCompatTextView = v.tvTime
        //        val btnUpdateTweet: AppCompatImageButton = v.btnUpdateTweet
//        val btnDeleteTweet: AppCompatImageButton = v.btnDeleteTweet
        val btnMenuTweet: AppCompatImageButton = v.btnMenuTweet
    }
}