@file:Suppress("PackageName", "unused")

package org.firstinspires.ftc.teamcode.TeleOp

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.extensions.pedro.PedroDriverControlled
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.hardware.driving.DriverControlledCommand
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Drive

import org.firstinspires.ftc.teamcode.Systems.Shoot
import org.firstinspires.ftc.teamcode.Systems.Shooter
import org.firstinspires.ftc.teamcode.Systems.ShooterSubsystems.FlywheelState
import org.firstinspires.ftc.teamcode.ILT.Next.Data.Alliance
import org.firstinspires.ftc.teamcode.ILT.Next.Data.ROBOT
import org.firstinspires.ftc.teamcode.ILT.Next.Data.Stage
import org.firstinspires.ftc.teamcode.Util.addSubsystems
import org.firstinspires.ftc.teamcode.Util.includePedro
import org.firstinspires.ftc.teamcode.nextFtc.Subsystem.Shooter.Hood
import org.firstinspires.ftc.teamcode.nextFtc.Subsystem.Shooter.TurretMech.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.lower.Intake

@TeleOp(name = "Red TeleOp", group = "TeleOp")
class TeleOpRed : NextFTCOpMode() {
    init {
        includePedro(Constants::createFollower)
        addSubsystems(Shooter, Turret, Hood, Drive, Intake)
    }


    val drivetrain: DriverControlledCommand by lazy {
        PedroDriverControlled(
            -Gamepads.gamepad1.leftStickY,
            -Gamepads.gamepad1.leftStickX,
            -Gamepads.gamepad1.rightStickX,
            true
        )
    }
    var speedFactorIntake = 1.0;
    override fun onStartButtonPressed() {
        ROBOT.currAlliance = Alliance.RED
        ROBOT.currStage = Stage.TELEOP
        ROBOT.currStage.useFlywheelVel = false
        follower.setStartingPose(ROBOT.currTeleOpStartPose)
        drivetrain.schedule()

        Shooter.flywheelState = FlywheelState.AUTO_AIM

        // FIX: Intake
        val intakeMotorDrive = Intake.DriverCommand(
            Gamepads.gamepad1.rightTrigger.map { it * speedFactorIntake },
            Gamepads.gamepad1.leftTrigger.map { it * speedFactorIntake },
            { 0.0 }
        )
        intakeMotorDrive()

        //works sometimes array deque error read discord
        Gamepads.gamepad1.rightBumper
            .whenBecomesTrue(Shoot.shootTripleCommand())
        //works
        Gamepads.gamepad1.leftBumper
            .toggleOnBecomesTrue()
            .whenBecomesTrue { drivetrain.scalar = 0.2 }
            .whenBecomesFalse { drivetrain.scalar = 1.0 }
//works change idle speed to higher or like close shooting
        // also have to fix the auto shooting by changing and understanding the code
        Gamepads.gamepad1.circle.or(Gamepads.gamepad2.rightBumper)
            .whenBecomesTrue {
                Shooter.flywheelState = if (Shooter.flywheelState == FlywheelState.AUTO_AIM) {
                    FlywheelState.IDLE
                } else {
                    FlywheelState.AUTO_AIM
                }
            }
        // works just gotta change the values
        Gamepads.gamepad1.dpadUp.or(Gamepads.gamepad2.dpadUp)
            .whenBecomesTrue { Shooter.flywheelState = FlywheelState.STOPPED }
// works just gotta chagne the values
        Gamepads.gamepad1.dpadDown.or(Gamepads.gamepad2.dpadDown)
            .whenBecomesTrue { Shooter.flywheelState = FlywheelState.AUTO_AIM }
// change these poses to what william wants
        Gamepads.gamepad2.triangle
            .whenBecomesTrue { follower.pose = ROBOT.currAlliance.resetPoses.resetPose2 }
// same gotta change this
        Gamepads.gamepad2.square
            .whenBecomesTrue { follower.pose = ROBOT.currAlliance.resetPoses.resetPose3 }

        Gamepads.gamepad1.cross
            .whenBecomesTrue { follower.pose = ROBOT.currAlliance.resetPoses.resetPose1 }
    }

    override fun onUpdate() {
        // update this to the panels telemetry
        Shooter.update()
        telemetry.update()
    }
}