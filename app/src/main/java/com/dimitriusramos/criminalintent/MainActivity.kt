package com.dimitriusramos.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.UUID

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment == null){
            val fragment = CrimeListFragment.newInstance()

            /*this code here says, create a new fragment transaction, include one add operation in it, and then commit it */
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }

    override fun onCrimeSelected(crimeId: UUID) {
       val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()

    }
}