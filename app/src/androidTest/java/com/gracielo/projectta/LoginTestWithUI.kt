package com.gracielo.projectta

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.ui.login.TestLoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginTestWithUI {

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
    fun loginScenario1(){
        val username = "gracielo"
        val password = "cielo"
        onView(withId(R.id.txtFieldUsernameLog))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldPasswordLog))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.username_log)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(username))
        onView(withId(R.id.password_log)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(password))
        onView(withId(R.id.login)).perform(click())
        delay(1000)
        onView(withId(R.id.fab_add))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun loginScenario2(){
        val username = "gracielo"
        val wrongpassword ="wrong password"
        val password ="cielo"
        onView(withId(R.id.txtFieldUsernameLog))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldPasswordLog))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.username_log)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(username))
        onView(withId(R.id.password_log)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(wrongpassword))
        onView(withId(R.id.login)).perform(click())
        delay(1000)
        onView(withText("Wrong username or password"))
            .inRoot(ToastMatcher().apply {
                matches(isDisplayed())
            });
        onView(withId(R.id.password_log)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(password))
        onView(withId(R.id.login)).perform(click())
        delay(1000)
        onView(withId(R.id.fab_add))
            .check(matches(isDisplayed()))
    }
    private fun delay(time:Long) {
        try {
            Thread.sleep(time)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

}
