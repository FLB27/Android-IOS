/*package fr.isen.fallabrinom.isensmartcompanion.agenda

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventDecorator(private val color: Int, private val dates: Collection<CalendarDay>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day) // Applique la déco si la date est dans la liste
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(10f, color)) // Ajoute un point coloré sous la date
    }
}*/