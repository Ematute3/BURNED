package org.firstinspires.ftc.teamcode.ILT.Next.Subsystems

import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Shooter.FlyWheel
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Shooter.Hood
import org.opencv.objdetect.HOGDescriptor
import java.time.Instant
import kotlin.math.PI

object Commands : Subsystem{

    val midShoot =
        ParallelGroup(
            Hood.far,
            FlyWheel.mid
        )
    val midShootPrep =
        ParallelGroup(
            Hood.far,
            FlyWheel.mid,
            Gate.close
        )
    val shoot = ParallelGroup(
        Intake.run,
        Gate.open
    )

    val closeShoot =
        ParallelGroup(
            Hood.mid,
            FlyWheel.close
        )

    val farShoot =
        ParallelGroup(
            Hood.far,
            FlyWheel.max
        )

    val idle =
        ParallelGroup(
            Hood.close,
            Gate.close,
            FlyWheel.idle
        )

    val off =
        ParallelGroup(
            FlyWheel.off,
            Hood.close,
            Gate.close,
        )
    val intake = ParallelGroup(
        FlyWheel.idle,
        Gate.close,
        Intake.run
    )
    val reset = InstantCommand {
        ParallelGroup(
            Hood.close,
            Gate.close,
            FlyWheel.off,
            Intake.stop,

            )
    }

}