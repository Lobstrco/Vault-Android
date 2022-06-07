package com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.AFTER
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.BEFORE
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.BETWEEN
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.NON_CLAIMABLE
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type.NOT_SET
import kotlinx.parcelize.Parcelize

@Parcelize
data class Claimant(
    val destination: String,
    val timeIntervals: List<TimeInterval>
) : Parcelable {

    /**
     * @param currentTime Current time in milliseconds.
     * @return [ReadableTimeInterval] Readable representation for the [Claimant] Predicates.
     */
    fun getReadableTimeInterval(currentTime: Long = System.currentTimeMillis()): ReadableTimeInterval? {
        val listIntervals: MutableList<String?> = mutableListOf()

        if (timeIntervals.isNotEmpty()) {
            if (timeIntervals.size >= 2) { // Multiple time intervals.
                /** It is necessary to send time intervals to this list(listIntervals) in the order of their sequence,
                 *  so as not to violate the existing display logic. */
                var interval1 = timeIntervals[0]
                var interval2 = timeIntervals[1]
                if (interval1.timeStart != null && interval2.timeStart == null) {
                    interval1 = timeIntervals[1]
                    interval2 = timeIntervals[0]
                }

                when {
                    interval1.timeStart == null && interval1.timeEnd != null && currentTime < interval1.timeEnd!!.toLong()
                            && interval2.timeStart != null && currentTime < interval2.timeStart!!.toLong() -> { // Option 5.
                        listIntervals.add(interval1.timeEnd)
                        return ReadableTimeInterval(BEFORE, listIntervals)
                    }

                    interval1.timeEnd != null && currentTime > interval1.timeEnd!!.toLong()
                            && interval2.timeStart != null && currentTime < interval2.timeStart!!.toLong() -> { // Option 6.
                        listIntervals.add(interval2.timeStart)
                        return ReadableTimeInterval(AFTER, listIntervals)
                    }

                    interval2.timeStart != null && currentTime > interval2.timeStart!!.toLong() && interval2.timeEnd == null -> { // Option 7.
                        return ReadableTimeInterval(NOT_SET, listIntervals)
                    }
                }
            } else { // One time interval.
                val timeInterval = timeIntervals[0]
                when {
                    timeInterval.type == TimeInterval.IntervalType.LOCKED -> {
                        return ReadableTimeInterval(NON_CLAIMABLE, listIntervals)
                    }
                    timeInterval.type == TimeInterval.IntervalType.UNCONDITIONAL ||
                            (timeInterval.timeStart == null && timeInterval.timeEnd == null && timeInterval.type == TimeInterval.IntervalType.NORMAL) -> { // Option 12.
                        return ReadableTimeInterval(NOT_SET, listIntervals)
                    }
                    timeInterval.timeStart != null && timeInterval.timeEnd == null -> {
                        when {
                            currentTime <= timeInterval.timeStart!!.toLong() -> { // Option 3.
                                listIntervals.add(timeInterval.timeStart)
                                return ReadableTimeInterval(AFTER, listIntervals)
                            }
                            currentTime > timeInterval.timeStart!!.toLong() -> { // Option 4 and 10.
                                return ReadableTimeInterval(NOT_SET, listIntervals)
                            }
                        }
                    }
                    ((timeInterval.timeStart != null && timeInterval.timeStart!!.toLong() <= currentTime) || timeInterval.timeStart == null)
                            && timeInterval.timeEnd != null && currentTime < timeInterval.timeEnd!!.toLong() -> { // Option 1.
                        listIntervals.add(timeInterval.timeEnd)
                        return ReadableTimeInterval(BEFORE, listIntervals)
                    }
                    ((timeInterval.timeStart != null && timeInterval.timeStart!!.toLong() <= currentTime) || timeInterval.timeStart == null)
                            && timeInterval.timeEnd != null && currentTime > timeInterval.timeEnd!!.toLong() -> { // Option 2.
                        return ReadableTimeInterval(NON_CLAIMABLE, listIntervals)

                    }
                    timeInterval.timeStart != null && currentTime < timeInterval.timeStart!!.toLong() && timeInterval.timeEnd != null -> { // Option 8.
                        listIntervals.add(timeInterval.timeStart)
                        listIntervals.add(timeInterval.timeEnd)
                        return ReadableTimeInterval(BETWEEN, listIntervals)
                    }
                    timeInterval.timeStart != null && currentTime > timeInterval.timeStart!!.toLong()
                            && timeInterval.timeEnd != null && currentTime < timeInterval.timeEnd!!.toLong() -> { // Option 9.
                        listIntervals.add(timeInterval.timeStart)
                        listIntervals.add(timeInterval.timeEnd)
                        return ReadableTimeInterval(BETWEEN, listIntervals)
                    }
                    timeInterval.timeStart != null && timeInterval.timeEnd != null && currentTime > timeInterval.timeEnd!!.toLong() -> { // Option 13.
                        listIntervals.add(timeInterval.timeStart)
                        listIntervals.add(timeInterval.timeEnd)
                        return ReadableTimeInterval(NON_CLAIMABLE, listIntervals)
                    }
                }
            }
        }

        return null
    }
}