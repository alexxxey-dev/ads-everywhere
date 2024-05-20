package com.ads.everywhere.data.repository

import android.app.usage.UsageEvents.Event
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import com.ads.everywhere.data.models.AppUsage
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.isSystemApp


class UsageRepository(private val manager: UsageStatsManager, private val context:Context) {




    fun load(): List<AppUsage> = try {
        val events = loadEvents().filter { it.eventType == Event.MOVE_TO_FOREGROUND }

       manager
           .queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, System.currentTimeMillis())
           .filter { it.totalTimeInForeground > 0  && !context.isSystemApp(it.packageName)}
           .groupBy { it.packageName }
           .map { entry->
               val time= entry.value.sumOf { it.totalTimeInForeground }
               val launch  =entry.value.sumOf { launchCount(it, events) }
               val session = if(launch!=0) time / launch else 0
               AppUsage(
                   entry.key,
                   time,
                   launch,
                   session
               )
           }
    } catch (ex: Exception) {
        ex.printStackTrace()
        emptyList()
    }

    private fun loadEvents():List<Event>{
        val result = ArrayList<Event>()
        val events = manager.queryEvents(0,System.currentTimeMillis())

        if(!events.hasNextEvent()) return result

        do{
            val event = Event()
            events.getNextEvent(event)
            result.add(event)
        }while (events.hasNextEvent())

        return result
    }



    private fun launchCount(stats: UsageStats, events:List<Event>):Int{
        val pn = stats.packageName
        return events.count { it.packageName == pn}
    }

}