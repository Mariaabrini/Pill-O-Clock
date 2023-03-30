package com.example.monthlyviewcalendar

import java.time.LocalDate
import java.time.LocalTime

class Event(var name: String, date: LocalDate, time: LocalTime,
            var timesaday: String, var dosage: String, var type: String, var stock: String, var container: String) {
    private var date: LocalDate
    private var time: LocalTime

    init {
        this.date = date
        this.time = time
    }

    fun getDate(): LocalDate {
        return date
    }

    fun setDate(date: LocalDate) {
        this.date = date
    }

    fun getTime(): LocalTime {
        return time
    }

    fun setTime(time: LocalTime) {
        this.time = time
    }

    companion object {
        //list of all scheduled medications
        var eventsList: ArrayList<Event> = ArrayList()

        //used in setEventAdapter in WeekViewActivity
        fun eventsForDate(date: LocalDate?): ArrayList<Event> {
            val events: ArrayList<Event> = ArrayList()
            for (event in eventsList) {
                //if (event.getDate().equals(date)) events.add(event)
                events.add(event) // adds event for everyday
            }
            // Sort events by time
            events.sortBy { it.getTime() }

            return events
        }
    }
}