package com.example.clfilter

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

open class BaseActivity : AppCompatActivity() {
    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.find {
            it.isVisible
        }
        if (fragment != null && fragment is NavHostFragment) {
            fragment.findNavController().popBackStack()
        }
    }
}