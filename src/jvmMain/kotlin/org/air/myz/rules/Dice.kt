package org.air.myz.rules

import kotlin.random.Random

private fun skillText(text: Any) = "\u001B[42m\u001B[30m $text \u001B[0m"
private fun attributeText(text: Any) = "\u001B[43m\u001B[30m $text \u001B[0m"
private fun gearText(text: Any) = "\u001B[100m\u001B[97m $text \u001B[0m"

private fun d6(): Int = Random.nextInt(6) + 1

private fun attributeRoll(): Die = when (val value = d6()) {
    1 -> Trauma
    in 2..5 -> AttributeDie(value)
    6 -> AttributeSuccess
    else -> throw IllegalStateException("""Impossible dice roll of $value""")
}

private fun skillRoll(): Die = when (val value = d6()) {
    in 1..5 -> SkillDie(value)
    6 -> SkillSuccess
    else -> throw IllegalStateException("""Impossible dice roll of $value""")
}

private fun gearRoll(): Die = when (val value = d6()) {
    1 -> GearDamage
    in 2..5 -> GearDie(value)
    6 -> GearSuccess
    else -> throw IllegalStateException("""Impossible dice roll of $value""")
}

private fun reroll(die: RollableDie) = when(die) {
    is AttributeDie -> attributeRoll()
    is SkillDie -> skillRoll()
    is GearDie -> gearRoll()
}

sealed class Die
internal sealed class RollableDie: Die()
internal sealed class SuccessfulDieRoll: Die()

internal class AttributeDie(val value: Int) : RollableDie() {
    override fun toString() = attributeText(value)
}
internal class SkillDie(val value: Int) : RollableDie() {
    override fun toString() = skillText(value)
}
internal class GearDie(val value: Int) : RollableDie() {
    override fun toString() = gearText(value)
}
internal object AttributeSuccess : SuccessfulDieRoll() {
    override fun toString() = attributeText("+")
}
internal object SkillSuccess : SuccessfulDieRoll() {
    override fun toString() = skillText("+")
}
internal object GearSuccess : SuccessfulDieRoll() {
    override fun toString() = gearText("+")
}
internal object Trauma : Die() {
    override fun toString() = attributeText("!")
}
internal object GearDamage : Die() {
    override fun toString() = gearText("~")
}

private fun dicePool(attribute: Int, skill: Int = 0, gear: Int = 0) = sequence {
    repeat(attribute) {
        yield(attributeRoll())
    }
    repeat(skill) {
        yield(skillRoll())
    }
    repeat(gear) {
        yield(gearRoll())
    }
}.toList()

fun roll(attribute: Int, skill: Int = 0, gear: Int = 0) = InitialRollResult(dicePool(attribute, skill, gear))

fun rollFullAuto(attribute: Int, skill: Int = 0, gear: Int = 0) = FullAutoRollResult(dicePool(attribute, skill, gear))

private fun forceRoll(dice: List<Die>) = dice.map { when(it) {
    is RollableDie -> reroll(it)
    else -> it
}}

interface ForceableRollResult {
    fun force(): RollResult
}

abstract class RollResult(val dice: List<Die>) {
    val success get() = dice.count { it is SuccessfulDieRoll }
    open val trauma get() = dice.count { it is Trauma }
    open val gearDamage get() = dice.count { it is GearDamage }

    override fun toString() =
        dice.joinToString(separator = " ") { it.toString() }
}

class InitialRollResult(dice: List<Die>) : RollResult(dice), ForceableRollResult {
    override val trauma = 0
    override val gearDamage = 0
    override fun force() = ForcedRollResult(forceRoll(dice))
}

class ForcedRollResult(dice: List<Die>) : RollResult(dice)

class FullAutoRollResult(dice: List<Die>, val forced: Boolean = false) : RollResult(dice), ForceableRollResult {
    override fun force() = FullAutoRollResult(forceRoll(dice), forced = true)
}
