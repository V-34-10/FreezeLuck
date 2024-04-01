package com.forrtun.frreezy.game.view.manager.stake

class ManagerStatusStake(
    private val sumBet: Int,
    private val minBet: Double,
    private val maxBet: Double,
    private val stepBet: Int,
    private var defaultBetValue: Int = 300
) {
    val defaultBet: Int
        get() = defaultBetValue

    val canIncreaseBet: Boolean
        get() = defaultBet < (sumBet * maxBet).toInt()

    val canDecreaseBet: Boolean
        get() = defaultBet > (sumBet * minBet).toInt()

    fun increaseBet() {
        if (canIncreaseBet) defaultBetValue += stepBet
    }

    fun decreaseBet() {
        if (canDecreaseBet) defaultBetValue -= stepBet
    }
}