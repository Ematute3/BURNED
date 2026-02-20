package org.firstinspires.ftc.teamcode.ILT.Next.TeleOp

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroDriverControlled
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.driving.Drivetrain
import org.firstinspires.ftc.teamcode.ILT.Next.Data.Alliance
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Commands
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Drive
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Gate
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Intake
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Shooter.FlyWheel

import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Shooter.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystem.Hood

@TeleOp(name = "COmmands", group = "Competition")
class TestCommands : NextFTCOpMode() {


    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(
                FlyWheel, Drivetrain, Hood, Gate, Intake, Turret
            ),
            BulkReadComponent, BindingsComponent
        )
    }


    override fun onInit() {
        PedroComponent.Companion.follower.pose = Pose(72.0, 72.0, 0.0)
    }

    override fun onStartButtonPressed() {
        PedroDriverControlled(
            Gamepads.gamepad1.leftStickY,
            Gamepads.gamepad1.leftStickX,
            -Gamepads.gamepad1.rightStickX,
            false  // false = field centric, true = robot centric
        ).schedule()


        bindControls()
    }

    private fun bindControls() {
        // --- DRIVER (GP1) ---
        Gamepads.gamepad1.leftBumper whenBecomesTrue { Commands.intake.schedule()}
        Gamepads.gamepad1.rightBumper whenBecomesTrue { Commands.shoot.schedule() }
        Gamepads.gamepad1.circle whenBecomesTrue {Commands.midShootPrep.schedule()}
        Gamepads.gamepad1.square whenBecomesTrue { Commands.midShoot.schedule() }
        Gamepads.gamepad1.triangle whenBecomesTrue { Commands.idle.schedule() }
        Gamepads.gamepad1.cross whenBecomesTrue { Commands.reset.schedule() }




    }

    override fun onUpdate() {
        Drive.poseValid = true

        Drive.currentX = PedroComponent.Companion.follower.pose.x
        Drive.currentY = PedroComponent.Companion.follower.pose.y
        Drive.currentHeading = PedroComponent.Companion.follower.pose.heading


    }

}