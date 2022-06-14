package com.gracielo.projectta

import android.util.Log
import android.view.View
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object CustomMatcherError {
    fun withErrorText(expectedErrorText: String): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                if (view !is TextInputLayout) {
                    return false
                }
                Log.d("checkerror",view.error.toString())
                val error = view.error ?: return false

                return expectedErrorText == error.toString()
            }

            override fun describeTo(description: Description) {}
        }
    }
}