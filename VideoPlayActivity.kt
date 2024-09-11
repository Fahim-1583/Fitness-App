package com.app.fitlife.actvities

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.app.fitlife.R
import com.app.fitlife.databinding.ActivityVideoPlayBinding
import com.app.fitlife.room.AppDatabase
import com.app.fitlife.room.VideoProgress
import com.app.fitlife.room.VideoProgressDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class VideoPlayActivity : AppCompatActivity() {
    var currentVideoIndex = 0
    private lateinit var binding: ActivityVideoPlayBinding
    private lateinit var exercises: List<Int>
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    var currentPosition = 0
    private lateinit var database: AppDatabase
    private lateinit var videoProgressDao: VideoProgressDao
    private var currentProgress: VideoProgress? = null
    private var accumulatedSeconds = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        exercises = intent.getIntegerArrayListExtra("videoList") ?: emptyList()
        currentVideoIndex = intent.getIntExtra("position", 0)
        if (exercises.size - 1 == currentVideoIndex) {
            binding.bottom.visibility = View.GONE
        } else {
            val bitmap = getVideoFrame(exercises[currentVideoIndex + 1])
            if (bitmap != null) {
                binding.imageView3.setImageBitmap(bitmap)
            }
        }
        database = AppDatabase.getDatabase(applicationContext)
        videoProgressDao = database.videoProgressDao()
        checkIfRecordExists()

        if (exercises.isNotEmpty()) {
            playVideo(currentVideoIndex)
        }
        binding.ivPause.setOnClickListener {
            pauseVideo()
        }
        binding.ivPlay.setOnClickListener {
            resumeVideo()
        }

        binding.ivNext.setOnClickListener {
            playNextVideo()
        }
        binding.ivRewind.setOnClickListener {
            playPreviousVideo()
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

//        binding.seekBar.setOnTouchListener(OnTouchListener { view, motionEvent -> true })

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentPosition = progress
                    binding.videoView.seekTo(currentPosition * 1000)
                    binding.tvStartTime.text = formatTime(currentPosition)
                    binding.tvEndTime.text = formatTime(30 - currentPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                pauseVideo()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                resumeVideo()
            }
        })

    }

    private fun checkIfRecordExists() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = dateFormat.format(Date())
        GlobalScope.launch(Dispatchers.IO) {
            currentProgress = videoProgressDao.getProgressByDate(today) ?: run {
                val newProgress = VideoProgress(today, 0)
                videoProgressDao.insertProgress(newProgress)
                newProgress
            }
            accumulatedSeconds= currentProgress!!.seconds
            Log.d("onCreate", "onCreate: $accumulatedSeconds")
        }
    }

    private fun playPreviousVideo() {
        checkIfRecordExists()
        if (currentVideoIndex == 0) {
            Toast.makeText(this, "This is the first video", Toast.LENGTH_SHORT).show()
        } else {
            handler.removeCallbacks(runnable!!)
            currentPosition = 0
            currentVideoIndex--
            binding.bottom.visibility = View.VISIBLE
            val bitmap = getVideoFrame(exercises[currentVideoIndex])
            if (bitmap != null) {
                binding.imageView3.setImageBitmap(bitmap)
            }
            playVideo(currentVideoIndex)
        }
    }

    private fun resumeVideo() {
        binding.videoView.start()
        binding.ivPause.visibility = View.VISIBLE
        binding.ivPlay.visibility = View.GONE
        startSeekBarUpdate()
    }

    private fun pauseVideo() {
        binding.videoView.pause()
        binding.ivPause.visibility = View.GONE
        binding.ivPlay.visibility = View.VISIBLE
        handler.removeCallbacks(runnable!!)
    }

    private fun getVideoFrame(videoResId: Int): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            val uriPath = "android.resource://${packageName}/$videoResId"
            retriever.setDataSource(this, Uri.parse(uriPath))
            retriever.getFrameAtTime(0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    private fun playVideo(index: Int) {
        val videoResId = exercises[index]
        val uriPath = "android.resource://${packageName}/$videoResId"
        binding.videoView.setOnPreparedListener {
            it.isLooping = true
        }
        binding.videoView.setVideoURI(Uri.parse(uriPath))
        binding.videoView.start()
        startSeekBarUpdate()
    }


    private fun startSeekBarUpdate() {
        runnable = object : Runnable {
            val totalDuration = 30

            override fun run() {
                if (currentPosition <= totalDuration) {
                    binding.seekBar.progress = currentPosition
                    binding.tvStartTime.text = formatTime(currentPosition)
                    binding.tvEndTime.text = formatTime(totalDuration - currentPosition)
                    currentPosition++
                    currentProgress?.let { progress ->
                        progress.seconds = currentPosition +accumulatedSeconds
                        lifecycleScope.launch(Dispatchers.IO) {
                            videoProgressDao.updateProgress(progress)
                        }
                    }

                    handler.postDelayed(this, 1000)
                } else {
                    playNextVideo()
                    handler.removeCallbacks(this)
                }
            }
        }
        handler.postDelayed(runnable!!, 0)
    }

    private fun playNextVideo() {
        checkIfRecordExists()
        if (currentVideoIndex == exercises.size - 1) {
            Toast.makeText(this, "No more videos", Toast.LENGTH_SHORT).show()
        } else {
            handler.removeCallbacks(runnable!!)
            currentPosition = 0
            currentVideoIndex++
            if (currentVideoIndex == exercises.size - 1) {
                binding.bottom.visibility = View.GONE
            } else {
                binding.bottom.visibility = View.VISIBLE
                val bitmap = getVideoFrame(exercises[currentVideoIndex + 1])
                if (bitmap != null) {
                    binding.imageView3.setImageBitmap(bitmap)
                }
            }
            playVideo(currentVideoIndex)
        }
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "onDestroy: called")
        handler.removeCallbacks(runnable!!)
    }
}