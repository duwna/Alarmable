package com.duwna.alarmable.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duwna.alarmable.R
import com.duwna.alarmable.entities.Alarm
import com.duwna.alarmable.utils.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_alarm.view.*


class AlarmAdapter() :
    ListAdapter<Alarm, AlarmViewHolder>(AlarmsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val containerView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class AlarmsDiffCallback : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }
}

class AlarmViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(
        item: Alarm
    ) = itemView.run {

        tv_time.text = item.time.toTimeString()
        tv_days.text = buildDaysString(
            item.onMon, item.onTue, item.onWed, item.onThu, item.onFri, item.onSat, item.onSun
        )
        tv_melody.text = item.melody

        checkbox_has_task.isChecked = item.hasTask
        switch_active.isChecked = item.isActive

        tv_monday.setDayChecked(item.onMon)
        tv_tuesday.setDayChecked(item.onTue)
        tv_wednesday.setDayChecked(item.onWed)
        tv_thursday.setDayChecked(item.onThu)
        tv_friday.setDayChecked(item.onFri)
        tv_saturday.setDayChecked(item.onSat)
        tv_sunday.setDayChecked(item.onSun)

        cardview.setOnClickListener {
            if (!expandable_container.isVisible) expand(expandable_container)
            else collapse(expandable_container)
        }
    }


    private fun TextView.setDayChecked(isChecked: Boolean) {
        setTextColor(
            context.attrValue(
                if (isChecked) R.attr.colorOnPrimary else R.attr.colorOnSurface
            )
        )
        setBackgroundResource(
            if (isChecked) R.drawable.day_background_checked else R.drawable.day_background_unchecked
        )
    }
}