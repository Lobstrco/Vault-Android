package com.lobstr.stellar.tsmapper.data.claim

import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.Claimant
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.TimeInterval
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.TimeInterval.IntervalType.LOCKED
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.TimeInterval.IntervalType.NORMAL
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.TimeInterval.IntervalType.UNCONDITIONAL
import org.stellar.sdk.Predicate
import java.math.BigDecimal

class ClaimantMapper {

    fun mapClaimants(claimants: List<org.stellar.sdk.Claimant>): List<Claimant> {
        return mutableListOf<Claimant>().apply {
            claimants.forEach {
                add(mapClaimant(it))
            }
        }
    }

    fun mapClaimant(claimant: org.stellar.sdk.Claimant): Claimant {
        return Claimant(
            claimant.destination,
            mutableListOf<TimeInterval>().apply {
                addAll(try { getTimeIntervals(claimant.predicate) } catch (exc: Exception) { mutableListOf()})
            }
        )
    }

    private fun getTimeIntervals(predicate: Predicate?): List<TimeInterval> {
        val intervalsList: MutableList<TimeInterval> = mutableListOf()
        intervalsList.addAll(parsePredicate(predicate))
        intervalsList.forEach {
            if (it.timeStart != null) {
                it.timeStart = it.timeStart.plus("000")
            }
            if (it.timeEnd != null) {
                it.timeEnd = it.timeEnd.plus("000")
            }
        }

        return intervalsList
    }

    private fun parsePredicate(predicate: Predicate?): List<TimeInterval> {
        val intervalsList: MutableList<TimeInterval> = mutableListOf()
        when (predicate) {
            is Predicate.Unconditional -> {
                intervalsList.add(
                    TimeInterval(
                        timeStart = null,
                        timeEnd = null,
                        type = UNCONDITIONAL
                    )
                )
            }
            is Predicate.AbsBefore -> {
                intervalsList.add(parsePredicateAbsBefore(predicate))
            }
            is Predicate.RelBefore -> {
                intervalsList.add(parsePredicateRelBefore(predicate))
            }
            is Predicate.And -> {
                intervalsList.add(parsePredicateAnd(predicate.inner))
            }
            is Predicate.Or -> {
                intervalsList.addAll(parsePredicateOr(predicate.inner))
            }
            is Predicate.Not -> {
                intervalsList.add(parsePredicateNot(predicate.inner))
            }
        }

        return intervalsList
    }

    private fun parsePredicateAbsBefore(predicate: Predicate.AbsBefore): TimeInterval {
        return TimeInterval(timeStart = null, timeEnd = predicate.timestampSeconds.toString())
    }

    private fun parsePredicateRelBefore(predicate: Predicate.RelBefore): TimeInterval {
        val createTime: String = System.currentTimeMillis().toString().substring(0,System.currentTimeMillis().toString().length -3)
        return TimeInterval(timeStart = null, timeEnd = BigDecimal(createTime).add(BigDecimal(predicate.secondsSinceClose.toString())).toString())
    }

    private fun parsePredicateNot(predicate: Predicate?): TimeInterval {
        var timeStart: String? = null
        var timeEnd: String? = null
        var type = 0

        when (predicate) {
            is Predicate.Unconditional -> { // NOT.2
                timeStart = null
                timeEnd = null
                type = LOCKED
            }
            is Predicate.AbsBefore -> { // NOT.1
                timeStart = parsePredicateAbsBefore(predicate).timeEnd
                timeEnd = null
                type = NORMAL
            }
            is Predicate.RelBefore -> {
                timeStart = parsePredicateRelBefore(predicate as Predicate.RelBefore).timeEnd
                timeEnd = null
                type = NORMAL
            }
            is Predicate.And -> { // NOT.3_00001
                val timeInterval = parsePredicateAnd(predicate.inner)
                when (timeInterval.type) {
                    UNCONDITIONAL -> {
                        timeStart = null
                        timeEnd = null
                        type = LOCKED
                    }
                    else -> {
                        timeStart = timeInterval.timeEnd
                        timeEnd = null
                        type = NORMAL
                    }
                }
            }
            is Predicate.Or -> { // NOT.4_00002
                val timeInterval = parsePredicateOr(predicate.inner)[0]
                when (timeInterval.type) {
                    UNCONDITIONAL -> {
                        timeStart = null
                        timeEnd = null
                        type = LOCKED
                    }
                    else -> {
                        timeStart = timeInterval.timeEnd
                        timeEnd = null
                        type = NORMAL
                    }
                }
            }
            is Predicate.Not -> {
                val timeInterval = parsePredicateNot(predicate.inner)
                when (timeInterval.type) {
                    NORMAL -> { // NOT.6_00004
                        timeStart = null
                        timeEnd = timeInterval.timeStart
                        type = NORMAL
                    }
                    LOCKED -> { // NOT.5_00003
                        timeStart = null
                        timeEnd = null
                        type = NORMAL
                    }
                }
            }
        }

        return TimeInterval(timeStart = timeStart, timeEnd = timeEnd, type = type)
    }

    private fun parsePredicateAnd(inner: List<Predicate>): TimeInterval {
        val timeInterval = TimeInterval()

        if (inner.size < 2) {
            val listIntervalWithUnconditionalPredicate = parsePredicate(inner[0])
            return if (listIntervalWithUnconditionalPredicate.isNotEmpty()) {
                listIntervalWithUnconditionalPredicate[0]
            } else {
                TimeInterval(timeStart = null, timeEnd = null)
            }
        }

        /** Due to the peculiarities of the structure of predicates. This method can't receive a list of more than two intervals. */
        val listIntervals1 = parsePredicate(inner[0])
        val listIntervals2 = parsePredicate(inner[1])

        val timeInterval1 = listIntervals1[0]
        val timeInterval2 = listIntervals2[0]

        when {
            timeInterval1.type == UNCONDITIONAL && timeInterval2.type == UNCONDITIONAL -> { // AND.10_00005
                /** Processing two unconditional predicates. */
                timeInterval.timeStart = null
                timeInterval.timeEnd = null
                timeInterval.type = UNCONDITIONAL
            }
            timeInterval1.type == UNCONDITIONAL || timeInterval2.type == UNCONDITIONAL -> { // AND.11
                /** Processing one unconditional predicates. */
                if (timeInterval1.type == UNCONDITIONAL) {
                    timeInterval.timeStart = timeInterval2.timeStart
                    timeInterval.timeEnd = timeInterval2.timeEnd
                    timeInterval.type = timeInterval2.type
                } else {
                    timeInterval.timeStart = timeInterval1.timeStart
                    timeInterval.timeEnd = timeInterval1.timeEnd
                    timeInterval.type = timeInterval1.type
                }
            }
            timeInterval1.type == LOCKED || timeInterval2.type == LOCKED -> { // AND.12.1/12.3/13
                /** Processing two predicates with one NOT predicate with unconditional interval. */
                timeInterval.timeStart = null
                timeInterval.timeEnd = null
                timeInterval.type = LOCKED
            }
            timeInterval1.timeEnd == null && timeInterval2.timeEnd == null &&
                    timeInterval1.timeStart != null && timeInterval2.timeStart != null -> { // AND.12.2
                /** Checking for the presence of a two-interval obtained from a predicate of type PredicateNot an Conditional. */
                if (timeInterval1.timeStart!!.toLong() <= timeInterval2.timeStart!!.toLong()) {
                    timeInterval.timeStart = timeInterval2.timeStart
                } else {
                    timeInterval.timeStart = timeInterval1.timeStart
                }
                timeInterval.timeEnd = null
                timeInterval.type = NORMAL
            }
            timeInterval1.timeEnd == null || timeInterval2.timeEnd == null -> {
                /** Checking for the presence of one interval obtained from a predicate of type PredicateNot. */
                if (timeInterval2.timeEnd == null) {

                    if (timeInterval1.timeEnd!!.toLong() < timeInterval2.timeStart!!.toLong()) { //AND.4.1
                        /** In this case, when creating Claimant, such time intervals were set at which the payment is blocked and cannot be received. */
                        timeInterval.timeStart = null
                        timeInterval.timeEnd = null
                        timeInterval.type = LOCKED
                    } else { //AND.4.2
                        timeInterval.timeStart = timeInterval2.timeStart
                        timeInterval.timeEnd = timeInterval1.timeEnd
                        timeInterval.type = NORMAL
                    }
                } else {
                    if (timeInterval2.timeEnd!!.toLong() < timeInterval1.timeStart!!.toLong()) { //AND.4.1
                        /** In this case, when creating Claimant, such time intervals were set at which the payment is blocked and cannot be received. */
                        timeInterval.timeStart = null
                        timeInterval.timeEnd = null
                        timeInterval.type = LOCKED
                    } else { //AND.4.2
                        timeInterval.timeStart = timeInterval1.timeStart
                        timeInterval.timeEnd = timeInterval2.timeEnd
                        timeInterval.type = NORMAL
                    }
                }
            }
            else -> {
                /** Handling two ordinary intervals. */ //AND.1-3/5-7
                if (timeInterval1.timeEnd!!.toLong() <= timeInterval2.timeEnd!!.toLong()) {
                    timeInterval.timeEnd = timeInterval1.timeEnd
                } else {
                    timeInterval.timeEnd = timeInterval2.timeEnd
                }
                timeInterval.timeStart = null
                timeInterval.type = NORMAL
            }
        }

        return timeInterval
    }

    private fun parsePredicateOr(inner: List<Predicate>): List<TimeInterval> {
        val listIntervals: MutableList<TimeInterval> = mutableListOf()

        if (inner.size < 2) {
            val listIntervalWithUnconditionalPredicate = parsePredicate(inner[0])
            if (listIntervalWithUnconditionalPredicate.isNotEmpty()) {
                listIntervals.add(listIntervalWithUnconditionalPredicate[0])
            } else {
                listIntervals.add(TimeInterval(timeStart = null, timeEnd = null))
            }
            return listIntervals
        }

        /** Due to the peculiarities of the structure of predicates. This method cannot receive a list of more than two intervals. */
        val listIntervals1 = parsePredicate(inner[0])
        val listIntervals2 = parsePredicate(inner[1])

        val timeInterval = TimeInterval()
        val timeInterval1 = listIntervals1[0]
        val timeInterval2 = listIntervals2[0]

        when {
            timeInterval1.type == UNCONDITIONAL && timeInterval2.type == UNCONDITIONAL -> { // OR.10
                /** Processing two unconditional predicates. */
                listIntervals.add(
                    TimeInterval(
                        timeStart = null,
                        timeEnd = null,
                        type = UNCONDITIONAL
                    )
                )
            }
            timeInterval1.type == UNCONDITIONAL || timeInterval2.type == UNCONDITIONAL -> { // OR.11
                /** Processing one unconditional predicates. */
                if (timeInterval1.type == UNCONDITIONAL) {
                    listIntervals.add(timeInterval2)
                } else {
                    listIntervals.add(timeInterval1)
                }
            }
            timeInterval1.type == LOCKED && timeInterval2.type == LOCKED -> { // OR.9.3
                /** Handling a NOT Predicate with an Unconditional interval. */
                listIntervals.add(TimeInterval(timeStart = null, timeEnd = null, type = LOCKED))
            }
            timeInterval1.type == LOCKED && timeInterval2.type != LOCKED -> { // OR.9.1
                /** Processing two predicates with one NOT predicate with unconditional interval. */
                listIntervals.add(timeInterval2)
            }
            timeInterval1.type != LOCKED && timeInterval2.type == LOCKED -> { // OR.9.1
                /** Processing two predicates with one NOT predicate with unconditional interval. */
                listIntervals.add(timeInterval1)
            }
            timeInterval1.timeEnd == null && timeInterval2.timeEnd == null &&
                    timeInterval1.timeStart != null && timeInterval2.timeStart != null -> { // OR.9.2
                /** Checking for the presence of a two-interval obtained from a predicate of type PredicateNot. */
                if (timeInterval1.timeStart!!.toLong() <= timeInterval2.timeStart!!.toLong()) {
                    timeInterval.timeStart = timeInterval1.timeStart
                } else {
                    timeInterval.timeStart = timeInterval2.timeStart
                }
                timeInterval.timeEnd = null
                listIntervals.add(timeInterval)
            }
            timeInterval1.timeEnd == null || timeInterval2.timeEnd == null -> { // OR.4/7/12
                /** Checking for the presence of one interval obtained from a predicate of type PredicateNot with conditional interval. */
                if (timeInterval2.timeEnd == null) { // OR.4.2
                    if (timeInterval1.timeEnd!!.toLong() >= timeInterval2.timeStart!!.toLong()) {
                        timeInterval.timeStart = null
                        timeInterval.timeEnd = null
                        timeInterval.type = NORMAL
                        listIntervals.add(timeInterval)
                    } else { // OR.4.1
                        listIntervals.add(timeInterval1)
                        listIntervals.add(timeInterval2)
                    }
                } else {
                    if (timeInterval2.timeEnd!!.toLong() >= timeInterval1.timeStart!!.toLong()) { // OR.4.2
                        timeInterval.timeStart = null
                        timeInterval.timeEnd = null
                        listIntervals.add(timeInterval)
                    } else { // OR.4.1
                        listIntervals.add(timeInterval1)
                        listIntervals.add(timeInterval2)
                    }
                }
            }
            else -> {
                /** Handling two ordinary intervals. */
                if (timeInterval1.timeEnd!!.toLong() <= timeInterval2.timeEnd!!.toLong()) { // OR.1-3/5/6/8
                    timeInterval.timeEnd = timeInterval2.timeEnd
                } else {
                    timeInterval.timeEnd = timeInterval1.timeEnd
                }
                timeInterval.timeStart = null
                listIntervals.add(timeInterval)
            }
        }

        return listIntervals.onEach {
            /** Bring the intervals to the display format in milliseconds. */
            if (it.timeStart != null) {
                it.timeStart = it.timeStart.plus("000")
            }
            if (it.timeEnd != null) {
                it.timeEnd = it.timeEnd.plus("000")
            }
        }
    }
}