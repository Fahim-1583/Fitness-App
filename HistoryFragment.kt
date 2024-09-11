package com.app.fitlife.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.app.fitlife.R
import com.app.fitlife.databinding.FragmentHistoryBinding
import com.app.fitlife.room.AppDatabase
import com.app.fitlife.room.VideoProgressDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryFragment : Fragment() {
    private lateinit var database: AppDatabase
    private lateinit var binding:FragmentHistoryBinding
    private lateinit var videoProgressDao: VideoProgressDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHistoryBinding.inflate(layoutInflater)
        database = AppDatabase.getDatabase(requireActivity().applicationContext)
        videoProgressDao = database.videoProgressDao()

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)

            fetchRecordByDate(selectedDate)
        }
        val currentDate = getCurrentDate()
        fetchRecordByDate(currentDate)
        return binding.root
    }
    private fun fetchRecordByDate(date: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val progress = videoProgressDao.getProgressByDate(date)
            if (progress!=null) {
                progress.let {
                    val caloriesBurnt = calculateCalories(it.seconds)
                    lifecycleScope.launch(Dispatchers.Main) {
                        if (it.seconds < 60) {
                            binding.tvMin2.text = it.seconds.toString()
                            binding.tvMinutesText.text = "sec"
                            binding.tvMin.text=caloriesBurnt.toString()
                        } else {
                            val minutes = it.seconds / 60
                            binding.tvMin2.text = minutes.toString()
                            binding.tvMinutesText.text = "min"
                            binding.tvMin.text=caloriesBurnt.toString()
                        }
                    }
                }
            }else {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.tvMin2.text = "0"
                    binding.tvMinutesText.text = "min"
                    binding.tvMin.text = "0"
                }

            }
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
    }
    private fun calculateCalories(seconds: Int): Int {
        val minutes = seconds / 60.0
        return (minutes * 8).toInt()
    }

}