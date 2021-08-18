package com.duwna.alarmable.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duwna.alarmable.R
import com.duwna.alarmable.data.database.alarm.Alarm
import com.duwna.alarmable.databinding.ItemAlarmBinding
import com.duwna.alarmable.utils.*


class AlarmAdapter(
    private val listener: AlarmClickListener,
    private val onMelodyClicked: (Alarm) -> Unit
) :
    ListAdapter<Alarm, AlarmViewHolder>(
        AlarmsDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding =
            ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)
        holder.bind(alarm)
        holder.setListeners(alarm, listener, onMelodyClicked)
    }
}

class AlarmsDiffCallback : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Alarm, newItem: Alarm): Any {
        return true
    }
}

class AlarmViewHolder(
    private val binding: ItemAlarmBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(alarm: Alarm) = with(binding) {

        tvTime.text = getTimeString(alarm.hour, alarm.minute)

        tvDays.text = when {
            alarm.isRepeating -> alarm.buildDaysString()
            isNextDay(alarm.hour, alarm.minute) -> "Завтра"
            else -> "Сегодня"
        }

        tvMelody.text = alarm.melodyName ?: "По умолчанию"
        checkboxHasTask.isChecked = alarm.hasTask
        switchActive.isChecked = alarm.isActive

        checkboxRepeat.isChecked = alarm.isRepeating
        daysContainer.isVisible = alarm.isRepeating

        tvMonday.setDayChecked(alarm.onMon)
        tvTuesday.setDayChecked(alarm.onTue)
        tvWednesday.setDayChecked(alarm.onWed)
        tvThursday.setDayChecked(alarm.onThu)
        tvFriday.setDayChecked(alarm.onFri)
        tvSaturday.setDayChecked(alarm.onSat)
        tvSunday.setDayChecked(alarm.onSun)

        cardview.setOnClickListener {
            if (!settingsContainer.isVisible) expand(settingsContainer)
            else collapse(settingsContainer)
        }
    }

    fun setListeners(
        alarm: Alarm,
        listener: AlarmClickListener,
        onMelodyClicked: (Alarm) -> Unit
    ) = with(binding) {
        switchActive.setOnClickListener { listener.onSwitchClicked(alarm) }
        taskContainer.setOnClickListener { listener.onTaskClicked(alarm) }
        deleteContainer.setOnClickListener { listener.onDeleteClicked(alarm) }
        repeatContainer.setOnClickListener { listener.onRepeatClicked(alarm) }
        tvMonday.setOnClickListener { listener.onMonClicked(alarm) }
        tvTuesday.setOnClickListener { listener.onTueClicked(alarm) }
        tvWednesday.setOnClickListener { listener.onWedClicked(alarm) }
        tvThursday.setOnClickListener { listener.onThuClicked(alarm) }
        tvFriday.setOnClickListener { listener.onFriClicked(alarm) }
        tvSaturday.setOnClickListener { listener.onSatClicked(alarm) }
        tvSunday.setOnClickListener { listener.onSunClicked(alarm) }
        melodyContainer.setOnClickListener { onMelodyClicked(alarm) }
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
    val onTaskClicked: (Alarm) -> Unit,
    val onDeleteClicked: (Alarm) -> Unit,
    val onRepeatClicked: (Alarm) -> Unit,
    val onMonClicked: (Alarm) -> Unit,
    val onTueClicked: (Alarm) -> Unit,
    val onWedClicked: (Alarm) -> Unit,
    val onThuClicked: (Alarm) -> Unit,
    val onFriClicked: (Alarm) -> Unit,
    val onSatClicked: (Alarm) -> Unit,
    val onSunClicked: (Alarm) -> Unit
)
