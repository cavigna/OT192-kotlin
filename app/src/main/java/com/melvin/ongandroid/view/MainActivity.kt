package com.melvin.ongandroid.view

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import com.melvin.ongandroid.R
import com.melvin.ongandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val drawerLayout = binding.drawerLayout
        navView = binding.navView

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener { itemMenu ->
            navigationFragments(itemMenu)
            true
        }

        binding.navView.bringToFront()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigationFragments(item: MenuItem) {
        when (item.itemId) {
            R.id.inicio_menu -> {
                Toast.makeText(applicationContext, getString(R.string.inicio), Toast.LENGTH_SHORT)
                    .show()
            }

            R.id.actividades_menu -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.actividades),
                    Toast.LENGTH_SHORT
                ).show()
            }

            R.id.novedades_menu -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.novedades),
                    Toast.LENGTH_SHORT
                ).show()
            }

            R.id.testimonios_menu -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.testimonios),
                    Toast.LENGTH_SHORT
                ).show()
            }
            R.id.nosotros_menu -> {
                Toast.makeText(applicationContext, getString(R.string.nosotros), Toast.LENGTH_SHORT)
                    .show()
            }

            R.id.contacto_menu -> {
                Toast.makeText(applicationContext, getString(R.string.contacto), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}