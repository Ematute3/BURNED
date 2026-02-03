package org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Shooter

import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.ServoEx
import kotlin.math.PI

/**
 * Gate subsystem for controlling ball flow to shooter.
 */
object Hood : Subsystem {

    private var hood = ServoEx("hood")
    private var position = 0.0



    override fun periodic() {

        hood.position = position
    }

    /**
     * Set gate position (0.0 = open, 1.0 = closed, typically).
     */
    fun setPosition(newPosition: Double) {
        position = newPosition.coerceIn(0.0, 1.0)
    }

    /**
     * Check if gate is open.
     */


    // ==================== COMMANDS ====================

    val full = InstantCommand {
        position = 1.0
    }

    val close = InstantCommand {
        position = 0.0
    }
    val mid = InstantCommand{
        position = 0.5
    }
    val far = InstantCommand{
        position = 0.75
    }
}