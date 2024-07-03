package com.sleepydev.bookvibe.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root

        Handler(Looper.getMainLooper()).postDelayed({

            view.findNavController().navigate(
                R.id.homeFragment, null,
                NavOptions.Builder()
                    .setPopUpTo(
                        R.id.splashFragment,
                        true
                    ).build())

        }, 2000)
        return view
    }



    override fun onResume() {
        super.onResume()

        Handler(Looper.getMainLooper()).postDelayed({

            view?.findNavController()?.navigate(
                R.id.homeFragment, null,
                NavOptions.Builder()
                    .setPopUpTo(
                        R.id.splashFragment,
                        true
                    ).build())

        }, 2000)

        if (binding.appicon.isVisible){
            Handler(Looper.getMainLooper()).postDelayed({


            }, 2000)
        }

    }
}