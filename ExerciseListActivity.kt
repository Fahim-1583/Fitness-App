package com.app.fitlife.actvities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.fitlife.R
import com.app.fitlife.databinding.ActivityExerciseListBinding
import com.app.fitlife.recyclerview.ExerciseAdapter

class ExerciseListActivity : AppCompatActivity() {
    private lateinit var binding:ActivityExerciseListBinding
    var exercises: List<Int>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val level=intent.getStringExtra("level")
        if (level=="Easy"){
            exercises= listOf(R.raw.easy1,R.raw.easy2,R.raw.easy3,R.raw.easy4)
            val exerciseAdapter=ExerciseAdapter(exercises!!,this)
            binding.recycleview.adapter=exerciseAdapter
        }
        if (level=="Medium"){
            exercises= listOf(R.raw.medium1,R.raw.medium2,R.raw.medium3,R.raw.medium4,R.raw.medium5,R.raw.medium6)
            val exerciseAdapter=ExerciseAdapter(exercises!!,this)
            binding.recycleview.adapter=exerciseAdapter
        }

        if (level=="Hard"){
            exercises= listOf(R.raw.hard1,R.raw.hard2,R.raw.hard3,R.raw.hard4,R.raw.hard5,R.raw.hard6,R.raw.hard7,R.raw.hard8)
            val exerciseAdapter=ExerciseAdapter(exercises!!,this)
            binding.recycleview.adapter=exerciseAdapter
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }
}