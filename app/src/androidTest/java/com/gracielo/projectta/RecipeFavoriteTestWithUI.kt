package com.gracielo.projectta

import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gracielo.projectta.ui.login.TestLoginActivity
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class RecipeFavoriteTestWithUI {
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
    fun testAddFavorit(){
        delay(2000)
        onView(withId(R.id.fab_add))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.rv_ingridients_list))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.rv_ingridients_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>((0..100).random(), click()))
        onView(withId(R.id.rv_ingridients_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>((0..100).random(), click()))
        onView(withId(R.id.rv_ingridients_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>((0..100).random(), click()))
        onView(withId(R.id.fab_search_recipe))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.fab_search_recipe)).perform(click())
        delay(7000)
        onView(withId(R.id.rv_search_result))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.rv_search_result))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(10, click()))
        delay(2000)
        onView(withId(R.id.imageFavouriteRecipe))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.imageFavouriteRecipe)).perform(click())
        onView(withText("Added to Favourite"))
            .inRoot(ToastMatcher().apply {
                matches(isDisplayed())
            })
    }

    @Test
    fun testRemoveFavorite(){
        delay(3000)
        onView(withId(android.R.id.content)).perform(swipeFromCenterToTop());
        delay(1000)
        onView(withId(R.id.rv_list_favourite_recipe))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.rv_list_favourite_recipe))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        delay(2000)
        onView(withId(R.id.imageFavouriteRecipe))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.imageFavouriteRecipe)).perform(click())
        onView(withText("Removed From Favourite"))
            .inRoot(ToastMatcher().apply {
                matches(isDisplayed())
            })
    }

    private fun delay(time:Long) {
        try {
            Thread.sleep(time)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
    fun nthChildOf(parentMatcher: Matcher<View>, childPosition: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("position $childPosition of parent ")
                parentMatcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                if(view.getParent() !is ViewGroup) return false
                val parent = view.getParent() as ViewGroup
                return (parentMatcher.matches(parent)
                        && parent.childCount > childPosition && parent.getChildAt(childPosition) == view)
            }
        }
    }
    var customScrollTo: ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return allOf(
                withEffectiveVisibility(Visibility.VISIBLE), isDescendantOfA(
                    anyOf(
                        isAssignableFrom(ConstraintLayout::class.java),
                        isAssignableFrom(ScrollView::class.java),
                        isAssignableFrom(HorizontalScrollView::class.java),
                        isAssignableFrom(NestedScrollView::class.java)
                    )
                )
            )
        }

        override fun getDescription(): String {
            return ""
        }

        override fun perform(uiController: UiController?, view: View?) {
            ScrollToAction().perform(uiController, view)
        }

    }
//    class ScrollToAction(
//        private val original: ScrollToAction? = ScrollToAction()
//    ) : ViewAction by original!! {
//
//        override fun getConstraints(): Matcher<View> = anyOf(
//            allOf(
//                withEffectiveVisibility(Visibility.VISIBLE),
//                isDescendantOfA(isAssignableFrom(NestedScrollView::class.java))),
//            original.constraints
//        )
//        fun scrollTo(): ViewAction = actionWithAssertions(ScrollToAction())
//    }
    private fun swipeFromCenterToTop(): ViewAction? {
        return CenterSwipeAction(
            Swipe.FAST,
            GeneralLocation.CENTER,
            { view ->
                val coordinates = GeneralLocation.CENTER.calculateCoordinates(view)
                coordinates[1] = 0f
                coordinates
            },
            Press.FINGER
        )
    }

    class CenterSwipeAction(
        private val swiper: Swiper, private val startCoordProvide: CoordinatesProvider,
        private val endCoordProvide: CoordinatesProvider, private val precDesc: PrecisionDescriber
    ) : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return withEffectiveVisibility(Visibility.VISIBLE)
        }

        override fun getDescription(): String {
            return "swipe from middle of screen"
        }

        override fun perform(uiController: UiController, view: View) {
            val startCoord = startCoordProvide.calculateCoordinates(view)
            val finalCoord = endCoordProvide.calculateCoordinates(view)
            val precision = precDesc.describePrecision()

            // you could try this for several times until Swiper.Status is achieved or try count is reached
            try {
                swiper.sendSwipe(uiController, startCoord, finalCoord, precision)
            } catch (re: RuntimeException) {
                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(re)
                    .build()
            }

            // ensures that the swipe has been run.
            uiController.loopMainThreadForAtLeast(
                ViewConfiguration.getPressedStateDuration().toLong()
            )
        }
    }
}