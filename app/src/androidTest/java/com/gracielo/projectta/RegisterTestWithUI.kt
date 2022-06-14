package com.gracielo.projectta

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.ui.login.TestLoginActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import junit.framework.Assert.assertFalse
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter


@RunWith(AndroidJUnit4::class)
class RegisterTestWithUI {
    val current = LocalDateTime.now(ZoneId.of("UTC+7"))
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss")
    val formattedDatetime = current.format(formatter)
    val listUsername = mutableListOf<String>()
    val apiServices = ApiServices()
    @get:Rule
    var activityRule = ActivityScenarioRule(TestLoginActivity::class.java)

    @Before
    fun setUp() {
        AndroidThreeTen.init(Application())
        ActivityScenario.launch(TestLoginActivity::class.java)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.espressoTestIdlingResource)
        apiServices.getAllUsers {
            it?.dataAllUser?.forEach {data->
                listUsername.add(data.username)
            }
        }
    }


    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @Test
    fun registerScenario1(){

        onView(withId(R.id.btnToRegister))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.btnToRegister)).perform(click())
        onView(withId(R.id.txtFieldUsernameReg))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldPasswordReg))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldConPasswordReg))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldEmailReg))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldNameReg))
            .check(matches(ViewMatchers.isDisplayed()))
        val username = "user-$formattedDatetime"
        val password=username
        onView(withId(R.id.username_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(username))

        onView(withId(R.id.password_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(password))
        onView(withId(R.id.conpassword_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(password))
        onView(withId(R.id.email_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText("cielo.justine01@gmail.com"))
        onView(withId(R.id.name_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(username))
        onView(withId(R.id.btnReqister)).perform(click())
        delay(1000)
        onView(withId(R.id.btnFinishVerification))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun registerScenario2(){
        val username = "gracielo"
        onView(withId(R.id.btnToRegister))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.btnToRegister)).perform(click())
        onView(withId(R.id.txtFieldUsernameReg))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldPasswordReg))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldConPasswordReg))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldEmailReg))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.txtFieldNameReg))
            .check(matches(ViewMatchers.isDisplayed()))
        val usernamenew = "user-$formattedDatetime"
        val password=username
        onView(withId(R.id.username_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(username))
        onView(withId(R.id.password_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText("$password -1"))
        onView(withId(R.id.conpassword_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(password))
        onView(withId(R.id.email_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText("cielo.justine01@gmail.com"))
        onView(withId(R.id.name_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(username))
        onView(withId(R.id.btnReqister)).perform(click())
        onView(withId(R.id.txtFieldUsernameReg))
            .check(matches(CustomMatcherError.withErrorText("Username Has Been Taken")))
        onView(withId(R.id.txtFieldConPasswordReg))
            .check(matches(CustomMatcherError.withErrorText("Password And Confirmation Password Must Be The Same")))
        onView(withId(R.id.username_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(usernamenew))
        onView(withId(R.id.password_reg)).perform(ViewActions.clearText())
            .perform(ViewActions.typeText(password))
        onView(withId(R.id.btnReqister)).perform(click())
        delay(1000)
        onView(withId(R.id.btnFinishVerification))
            .check(matches(ViewMatchers.isDisplayed()))
    }
    private fun delay(time:Long) {
        try {
            Thread.sleep(time)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}