package com.gracielo.projectta

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gracielo.projectta.ui.login.TestLoginActivity
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchRecipeTestWithUI {
    @get:Rule
    var activityRule = ActivityScenarioRule(TestLoginActivity::class.java)

    @Before
    fun setUp() {
        ActivityScenario.launch(TestLoginActivity::class.java)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.espressoTestIdlingResource)
    }


    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @Test
    fun SearchScenario1(){
        delay(2000)
        onView(withId(R.id.fab_add))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.rv_ingridients_list))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.rv_ingridients_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>((0..100).random(), click()))
        onView(withId(R.id.rv_ingridients_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>((0..100).random(), click()))
        onView(withId(R.id.rv_ingridients_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>((0..100).random(), click()))

        onView(withId(R.id.btnFilterNutrient)).perform(click())
        onView(withId(R.id.txtMinCaloriesFilter))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtMinCarbohydrateFilter))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtMaxProteinFilter))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.etMinCaloriesFilter)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText("100"))
        onView(withId(R.id.etMinCarbohydrateFilter)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText("10"))
        onView(withId(R.id.etMaxProteinFilter)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText("120"))
        onView(withId(R.id.btn_save_nutrient_filter)).perform(click())
        onView(withId(R.id.txtCaloriesShowFilter)).check(matches(withText(containsString("More Than 100"))))
        onView(withId(R.id.txtCarbohydrateShowFilter)).check(matches(withText(containsString("More Than 10"))))
        onView(withId(R.id.txtProteinShowFilter)).check(matches(withText(containsString("Less Than 120"))))
        onView(withId(R.id.fab_search_recipe))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.fab_search_recipe)).perform(click())
        delay(7000)
        onView(withId(R.id.rv_search_result))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.rv_search_result))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(10, scrollTo()))
    }

    @Test
    fun SearchScenario2(){
        delay(2000)
        onView(withId(R.id.fab_add))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.rv_ingridients_list))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.rv_ingridients_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>((0..100).random(), click()))
        onView(withId(R.id.rv_ingridients_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>((0..100).random(), click()))
        onView(withId(R.id.rv_ingridients_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>((0..100).random(), click()))
        onView(withId(R.id.fab_search_recipe))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.fab_search_recipe)).perform(click())
        delay(7000)
        onView(withId(R.id.rv_search_result))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.rv_search_result))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(10, scrollTo()))
    }
    private fun delay(time:Long) {
        try {
            Thread.sleep(time)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}