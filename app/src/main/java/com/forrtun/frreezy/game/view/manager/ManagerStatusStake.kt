package com.forrtun.frreezy.game.view.manager

class ManagerStatusStake(
    private val sumBet: Int,
    private val minBet: Double,
    private val maxBet: Double,
    private val stepBet: Int
) {
    private var defaultBet = 300
    fun getDefaultBet(): Int = defaultBet

    fun isPlusBet(): Boolean = defaultBet < (sumBet * maxBet).toInt()

    fun plusBet() {
        if (isPlusBet()) defaultBet += stepBet
    }

    fun isMinusBet(): Boolean = defaultBet > (sumBet * minBet).toInt()

    fun minusBet() {
        if (isMinusBet()) defaultBet -= stepBet
    }
}