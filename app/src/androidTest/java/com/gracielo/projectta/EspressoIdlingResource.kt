package com.gracielo.projectta

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private val RESOURCE = "GLOBAL"
    val espressoTestIdlingResource: CountingIdlingResource =
            CountingIdlingResource(RESOURCE)

    open fun increment() {
        espressoTestIdlingResource.increment()
    }

    open fun decrement() {
        espressoTestIdlingResource.decrement()
    }

    @kotlin.jvm.JvmStatic
    val getEspressoIdlingResource: IdlingResource
        get() = espressoTestIdlingResource

}