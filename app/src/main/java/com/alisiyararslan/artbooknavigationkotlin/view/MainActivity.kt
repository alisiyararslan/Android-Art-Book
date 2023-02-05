package com.alisiyararslan.artbooknavigationkotlin.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.Navigation
import com.alisiyararslan.artbooknavigationkotlin.R
import com.alisiyararslan.artbooknavigationkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.art_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.add_art_item){
            val action=ListFragmentDirections.actionListFragmentToDetailFragment("new",0)
            Navigation.findNavController(this,R.id.fragmentContainerView).navigate(action)



        }
        return super.onOptionsItemSelected(item)
    }

}