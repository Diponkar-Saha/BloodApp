package com.example.splash.ui.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.splash.R
import com.example.splash.adapter.TweetAdapter
import com.example.splash.adapter.TweetAdapterListener
import com.example.splash.commons.ProgressDialog
import com.example.splash.databinding.FragmentHomeBinding
import com.example.splash.databinding.FragmentRegisterBinding
import com.example.splash.model.Tweet
import com.example.splash.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : Fragment() , OnAddTweetListener, TweetAdapterListener {
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var adapter: TweetAdapter? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root


        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        fab.setOnClickListener { showAddTweetDialog() }
    }

    private fun initViews() {
        binding.apply {


            showProgressBar("Loading...")
            rv_tweets.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        homeViewModel.tweetListLiveData?.observe(viewLifecycleOwner, Observer { tweets ->
            if (tweets == null) {
                showEmptyView()
            } else {
                if (tweets.isNotEmpty()) {
                    clEmptyView.visibility = View.GONE
                    if (adapter == null) {
                        adapter = TweetAdapter(this)
                    }
                    adapter?.tweets = tweets
                    rv_tweets.adapter = adapter
                } else {
                    showEmptyView()
                }
            }
            homeViewModel.progress.value = false
        })




    }

    private fun showEmptyView() {
        if (adapter != null) {
            adapter?.tweets = ArrayList<Tweet>()
        }
        clEmptyView.visibility = View.VISIBLE
    }

    private fun showAddTweetDialog() {
        openDialog(false, null, null)
    }

    override fun onUpdateTweetTapped(tweet: Tweet) {
        openDialog(true, tweet.id, tweet.tweetText)
    }

    override fun onDeleteTweetTapped(tweet: Tweet) {
        AlertDialog.Builder(context)
            .setTitle("Delete Tweet")
            .setMessage("Are you sure you want to delete this tweet?")
            .setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
                tweet.id?.let { homeViewModel.deleteTweet(it) }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .show()
    }


    override fun onAddTweet(tweetString: String) {
        homeViewModel.addTweet(tweetString)
        Log.d("HomeFragment", tweetString)
    }

    override fun onUpdateTweet(tweetString: String, tweetId: String) {
        homeViewModel.updateTweet(tweetString, tweetId)
    }


    private fun openDialog(isForUpdate: Boolean, tweetId: String?, tweetString: String?) {
        val transaction = childFragmentManager.beginTransaction()
        val fragment = AddTweetDialogFragment.newInstance(isForUpdate, tweetId, tweetString)
        fragment.listener = this
        fragment.show(transaction, "add tweet")
    }

    private fun showProgressBar(text: String) {
        context?.let {
            val dialog =  ProgressDialog.dialog(it, text)
            homeViewModel.progress.observe(viewLifecycleOwner, Observer { showing ->
                if (showing) {
                    dialog.show()
                } else {
                    dialog.dismiss()
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }



}