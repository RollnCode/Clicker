package com.rollncode.clicker.loaders

import android.content.Context
import com.rollncode.clicker.storage.ClicksDbManager

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.15
 */
class ClicksByDateLoader(context: Context) : BaseLoader(context) {
    override fun loadInBackground() = ClicksDbManager.getClicksByDates()
}