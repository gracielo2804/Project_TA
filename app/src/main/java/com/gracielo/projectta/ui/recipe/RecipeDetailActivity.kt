package com.gracielo.projectta.ui.recipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gracielo.projectta.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var binding :ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}