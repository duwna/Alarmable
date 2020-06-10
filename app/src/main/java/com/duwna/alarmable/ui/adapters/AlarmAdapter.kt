package com.duwna.alarmable.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duwna.alarmable.R
import com.duwna.alarmable.database.alarm.Alarm
import com.duwna.alarmable.utils.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_alarm.view.*


class AlarmAdapter(
    private val listener: AlarmClickListener
) :
    ListAdapter<Alarm, AlarmViewHolder>(
        AlarmsDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val containerView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)
        holder.bind(alarm)
        holder.setListeners(alarm, listener)
    }
}

class AlarmsDiffCallback : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Alarm, newItem: Alarm): Any? {
        return true
    }
}

class AlarmViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(alarm: Alarm) = itemView.run {

        tv_time.text = alarm.time.toTimeString()
        tv_days.text = buildDaysString(
            alarm.onMon,
            alarm.onTue,
            alarm.onWed,
            alarm.onThu,
            alarm.onFri,
            alarm.onSat,
            alarm.onSun
        )
        tv_melody.text = alarm.melody
        checkbox_has_task.isChecked = alarm.hasTask
        switch_active.isChecked = alarm.isActive
        tv_monday.setDayChecked(alarm.onMon)
        tv_tuesday.setDayChecked(alarm.onTue)
        tv_wednesday.setDayChecked(alarm.onWed)
        tv_thursday.setDayChecked(alarm.onThu)
        tv_friday.setDayChecked(alarm.onFri)
        tv_saturday.setDayChecked(alarm.onSat)
        tv_sunday.setDayChecked(alarm.onSun)
        cardview.setOnClickListener {
            if (!expandable_container.isVisible) expand(expandable_container)
            else collapse(expandable_container)
        }
    }

    fun setListeners(alarm: Alarm, listener: AlarmClickListener) = itemView.run {
        switch_active.setOnClickListener { listener.onSwitchClicked(alarm) }
        tv_label_melody.setOnClickListener { listener.onMelodyClicked(alarm) }
        checkbox_has_task.setOnClickListener { listener.onTaskClicked(alarm) }
        tv_label_delete.setOnClickListener { listener.onDeleteClicked(alarm) }
        tv_monday.setOnClickListener { listener.onMonClicked(alarm) }
        tv_tuesday.setOnClickListener { listener.onTueClicked(alarm) }
        tv_wednesday.setOnClickListener { listener.onWedClicked(alarm) }
        tv_thursday.setOnClickListener { listener.onThuClicked(alarm) }
        tv_friday.setOnClickListener { listener.onFriClicked(alarm) }
        tv_saturday.setOnClickListener { listener.onSatClicked(alarm) }
        tv_sunday.setOnClickListener { listener.onSunClicked(alarm) }
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

class AlarmClickListener(
    val onSwitchClicked: (Alarm) -> Unit,
    val onMelodyClicked: (Alarm) -> Unit,
    val onTaskClicked: (Alarm) -> Unit,
    val onDeleteClicked: (Alarm) -> Unit,
    val onMonClicked: (Alarm) -> Unit,
    val onTueClicked: (Alarm) -> Unit,
    val onWedClicked: (Alarm) -> Unit,
    val onThuClicked: (Alarm) -> Unit,
    val onFriClicked: (Alarm) -> Unit,
    val onSatClicked: (Alarm) -> Unit,
    val onSunClicked: (Alarm) -> Unit
)
