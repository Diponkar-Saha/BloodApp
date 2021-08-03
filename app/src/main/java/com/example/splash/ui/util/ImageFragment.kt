package com.example.splash.ui.util

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.splash.R
import com.example.splash.commons.ProgressDialog
import com.example.splash.databinding.FragmentImageBinding
import com.example.splash.utilites.Constants
import com.example.splash.utilites.Resource
import com.example.splash.utilites.Status
import com.example.splash.viewmodel.ProfileImageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
private const val TAG = "TESTE"

@AndroidEntryPoint
class ImageFragment : Fragment() , DatePickerDialog.OnDateSetListener {
    private val viewModel: ProfileImageViewModel by viewModels()
    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private val authentication = FirebaseAuth.getInstance()

    val db = Firebase.firestore
    private val fileName = UUID.randomUUID().toString()


    val imageRef = Firebase.storage.reference


    var day = 0
    var month: Int = 0
    var year: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        _binding = FragmentImageBinding.inflate(inflater, container, false)
        val view = binding.root
        initu()

        binding.addImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, Constants.PICK_IMAGE)
        }

        initView()

        return view

    }

    private fun initView() {
        showProgressBar()

        binding.btnImg.setOnClickListener {
            val date = binding.textView.text.toString().trim()

            // viewModel.uploadImageData(date,imageUri!!)
             viewModel.upload(imageUri!!,date)
            navigateToMainActivity()


        }


        //
        viewModel.userLiveData?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), "image upload succesfuuly", Toast.LENGTH_SHORT)
                    .show()
                navigateToMainActivity()

            }else{
                Toast.makeText(requireContext(), "eeeeeeeeeee", Toast.LENGTH_SHORT)
                    .show()

            }
            viewModel.progress.value = false
        })
    }
        private fun fire() = CoroutineScope(Dispatchers.Main).launch {
            try {
                val date = binding.textView.text.toString().trim()
                val userr = Firebase.auth.currentUser
                val imageUrl = imageRef.child("images/$fileName").putFile(imageUri!!)
                    .await().storage.downloadUrl.await().toString()
                db.collection(Constants.USERS)
                    .document(userr.toString())
                    .update("date", date, "image", imageUrl)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Successfully updated user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "updated failed $e", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

        private fun navigateToMainActivity() {
            findNavController().navigate(R.id.action_imageFragment_to_bloodFragment)
        }

        private fun initu() {
            binding.apply {
                binding.btnPick.setOnClickListener {
                    val calendar: Calendar = Calendar.getInstance()
                    day = calendar.get(Calendar.DAY_OF_MONTH)
                    month = calendar.get(Calendar.MONTH)
                    year = calendar.get(Calendar.YEAR)
                    val datePickerDialog =
                        DatePickerDialog(requireContext(), this@ImageFragment, year, month, day)
                    datePickerDialog.show()
                }
            }
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            myDay = day
            myYear = year
            myMonth = month
            DateFormat.is24HourFormat(requireContext())
            binding.textView.text = "Date : " + myDay + "-" + myMonth + "-" + myYear

        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == Constants.PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
                imageUri = data?.data
                Glide.with(requireContext()).load(imageUri).circleCrop().into(binding.profileImage)
            }
        }

        private fun showProgressBar() {
            val dialog = ProgressDialog.dialog(requireContext(), "image loading...")
            viewModel.progress.observe(viewLifecycleOwner, Observer { showing ->
                if (showing) {
                    dialog.show()
                } else {
                    dialog.dismiss()
                }
            })
        }

    private fun showViewForSuccess() {

        binding.loadingAnim.isVisible = false

    }

    private fun showViewForLoading() {
        binding.loadingAnim.isVisible = true
    }

    private fun showViewsForError(message: String) {
        binding.loadingAnim.isVisible = false
        Toast.makeText(requireContext(), "eeeeeeeeeee$message", Toast.LENGTH_SHORT)
                    .show()
        Log.d(TAG, "Error update :: $message")
    }




}

//    private fun updateDateInView() {
//        val myFormat = "MM/dd/yyyy" // mention the format you need
//        val sdf = SimpleDateFormat(myFormat, Locale.US)
//        textview_date.text = sdf.format(cal.time)
//    }



