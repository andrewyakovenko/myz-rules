package org.air.myz.rules

import kotlin.test.Test

internal class DiceTest {
    @Test fun demoFullAuto() { // not an actual test
        println("Full auto roll")
        var roll = rollFullAuto(5, 7, 4)
        repeat(5) {
            println(roll)
            println("""Success: ${roll.success}, forced: ${roll.forced}, trauma: ${roll.trauma}, gear damage: ${roll.gearDamage}""")
            roll = roll.force()
        }
    }

    @Test fun demoForcedRoll() { // not an actual test
        println("Normal roll")
        val roll = roll(5, 7, 4)
        println(roll)
        println("""Success: ${roll.success}, forced: false, trauma: ${roll.trauma}, gear damage: ${roll.gearDamage}""")
        val forced = roll.force()
        println(forced)
        println("""Success: ${forced.success}, forced: true, trauma: ${forced.trauma}, gear damage: ${forced.gearDamage}""")
    }

}

