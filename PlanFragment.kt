package com.app.fitlife.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.fitlife.R
import com.app.fitlife.actvities.ExerciseListActivity
import com.app.fitlife.databinding.FragmentPlanBinding

class PlanFragment : Fragment() {
    private lateinit var binding:FragmentPlanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentPlanBinding.inflate(layoutInflater)

        binding.easyLevel.setOnClickListener {
            startActivity(Intent(requireActivity(),ExerciseListActivity::class.java).putExtra("level","Easy"))
        }

        binding.mediumLevel.setOnClickListener {
            startActivity(Intent(requireActivity(),ExerciseListActivity::class.java).putExtra("level","Medium"))
        }

        binding.hardLevel.setOnClickListener {
            startActivity(Intent(requireActivity(),ExerciseListActivity::class.java).putExtra("level","Hard"))
        }
        return binding.root
    }

}