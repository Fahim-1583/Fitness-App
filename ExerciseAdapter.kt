package com.app.fitlife.recyclerview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.fitlife.R
import com.app.fitlife.actvities.VideoPlayActivity

class ExerciseAdapter(
    private val exercises: List<Int>,
    val context: Context
) :
    RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {
    var mainHandler: Handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_exercise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = exercises[position]
        val bitmap = getVideoFrame(model)
        if (bitmap != null) {
            holder.ivIcon.setImageBitmap(bitmap)
        }
        holder.ivIcon.setOnClickListener {
            context.startActivity(Intent(context,VideoPlayActivity::class.java).putExtra("position",position)
                .putIntegerArrayListExtra("videoList",ArrayList(exercises)))
        }
    }
    private fun getVideoFrame(videoResId: Int): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            val uriPath = "android.resource://${context.packageName}/$videoResId"
            retriever.setDataSource(context, Uri.parse(uriPath))
            retriever.getFrameAtTime(0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
    override fun getItemCount(): Int {
        return exercises.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.exercise)
    }
}
