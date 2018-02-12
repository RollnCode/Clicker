@file:Suppress("MemberVisibilityCanBePrivate")

package com.rollncode.clicker

import android.content.Intent
import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.intent.matcher.IntentMatchers.hasType
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import android.support.v4.app.ShareCompat
import com.rollncode.clicker.activity.ClickActivity
import com.rollncode.clicker.content.MetaData.ClickColumns
import com.rollncode.clicker.content.toTimestamp
import junit.framework.Assert.assertTrue
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("MemberVisibilityCanPrivate")
@RunWith(AndroidJUnit4::class)
class UserInteractionTest {

    @get:Rule
    val rule = IntentsTestRule<ClickActivity>(ClickActivity::class.java, false, true)

    private var clicks = 5
    private var counter = 0
    private var countClicks = false

    @Before
    fun setCounter() {
        counter = getClicksForToday()
    }

    @Test
    fun addClicksAndShare() {
        insertOrDeleteClicks(clicksNumber = clicks)
        onView(withId(R.id.action_share)).perform(click())
        assertShareIntent()
    }

    @Test
    fun checkValidityOfAddingAndDeletingClicks() {
        countClicks = true
        insertOrDeleteClicks(clicksNumber = 5)
        insertOrDeleteClicks(false, 2)

        clicks = 2
        addClicksAndShare()
        assertTrue(getClicksForToday() == counter)
    }

    @Test
    fun addClicksGoToRecordsAndCheckIntent() {
        insertOrDeleteClicks(clicksNumber = 5)
        onView(withId(R.id.action_list)).perform(click())

        val listItem = onData(anything()).inAdapterView(withId(R.id.listView)).atPosition(0)
        listItem.onChildView(withId(R.id.tvDate)).check(matches(withText(System.currentTimeMillis().toTimestamp())))
        listItem.perform(click())

        onView(withId(R.id.action_share)).perform(click())
        assertShareIntent()
    }

    @Test
    fun resetAndAddClicksGoToRecordsAndCheckNumberOfRows() {
        countClicks = true
        insertOrDeleteClicks(false, counter)
        insertOrDeleteClicks(clicksNumber = 10)

        onView(withId(R.id.action_list)).perform(click())
        assertTrue(getClicksForToday() == counter)
    }

    private fun assertShareIntent() {
        intended(allOf(hasAction(Intent.ACTION_SEND),
                hasType("text/*"),
                hasExtra(ShareCompat.EXTRA_CALLING_PACKAGE, rule.activity.packageName)))
    }

    private fun insertOrDeleteClicks(insert: Boolean = true, clicksNumber: Int) {
        @IdRes val btnId = if (insert) R.id.btnClick else R.id.btnUndo
        (0..clicksNumber).forEach {
            when {
                countClicks && insert  -> counter++
                countClicks && !insert -> if (counter == 0) counter = 0 else counter--
            }
            onView(withId(btnId)).perform(click())
        }
    }

    private fun getClicksForToday(): Int {
        val cursor = rule.activity.contentResolver.query(ClickColumns.CONTENT_URI,
                arrayOf("COUNT(*) as ${ClickColumns.COUNT}"),
                "${ClickColumns.QUERY_TIMESTAMP} LIKE ?",
                arrayOf(System.currentTimeMillis().toTimestamp()), null)

        val todayClicks = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndex(ClickColumns.COUNT)) else 0
        cursor.close()
        return todayClicks
    }
}