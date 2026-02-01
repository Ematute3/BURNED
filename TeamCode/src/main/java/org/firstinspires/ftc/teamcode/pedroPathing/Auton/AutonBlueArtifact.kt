package org.firstinspires.ftc.teamcode.next.kotlin

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import dev.nextftc.core.commands.conditionals.switchCommand
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Drive
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Gate
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Intake
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Shooter.FlyWheel
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Shooter.Hood
import org.firstinspires.ftc.teamcode.ILT.Next.Subsystems.Shooter.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import java.lang.StrictMath.toRadians
import kotlin.math.PI

@Disabled
@Autonomous(name = "Pedro Auto 3+3")
@Configurable
class PedroAutonomous : NextFTCOpMode() {

    private var tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)
    private lateinit var autoPath: AutoPath
    private var index = 0

    init {
        addComponents(
            SubsystemComponent(Intake, FlyWheel, Gate, Hood, Drive, Turret),
            PedroComponent(Constants::createFollower),
            BulkReadComponent,
            BindingsComponent
        )
    }

    override fun onInit() {
        follower.setStartingPose(Pose(123.0, 123.5, toRadians(37.0)))
        autoPath = AutoPath()

        tele.run {
            addLine("Status: Initialized")
            addLine("Ready to run 3+3 autonomous")
            update()
        }
    }

    override fun onStartButtonPressed() {
        // Schedule the first path
        autoPath.next().schedule()
    }

    override fun onUpdate() {
        follower.update()

        tele.run {
            addLine("Path Index: $index / ${autoPath.pathCount}")
            addLine("X: ${"%.2f".format(follower.pose.x)}")
            addLine("Y: ${"%.2f".format(follower.pose.y)}")
            addLine("Heading: ${"%.2f".format(Math.toDegrees(follower.pose.heading))}°")
            addLine()
            //addLine(Outtake.getTelemetryString())
            update()
        }
    }

    inner class AutoPath {

        // Poses for autonomous
        val start = Pose(123.0, 123.5, toRadians(37.0))
        val scorePose = Pose(96.0,96.0,toRadians(37.0))
        val scorePoseCP1 = Pose(85.5,75.5)
        val intake1 = Pose(131.0,59.5,toRadians(0.0))
        val intake1CP1 = Pose(93.4,54.0)
        val intake1CP2 = Pose(103.7,60.5)
        val clear1 = Pose(129.6,62.73, toRadians(13.0))
        val clear1CP1 = Pose(97.4,71.6)
        val clear1CP2 = Pose(96.5,63.7)
        val intake2 = Pose(134.65,51.5, toRadians(39.0))
        val intake2CP1 = Pose(126.8,52.9)
        val scorePoseCPI2 = Pose(103.7,56.2)
        val intake3 = Pose(130.0,84.5, toRadians(0.0))
        val intake3CP1 = Pose(96.0,84.0)


        val pathCount = 7

        // Helper command to shoot with distance-based velocity


        // Path sequences
        val startToLaunch = SequentialGroup(

                Commands.midShoot.schedule(),
                // Prep flywheel
                FollowPath(
                follower.pathBuilder()
                    .addPath(BezierLine(start, scorePose))
                    .setLinearHeadingInterpolation(toRadians(39.0), toRadians(39.0))
                    .build()
                ),
            ParallelGroup(
                Gate.open,
                Intake.run
            ),
            Delay(1.5)

            )
             // Shoot once at launch point


        val launchToFirstSet = SequentialGroup(
            FlyWheel.idle, // Start intaking
            FollowPath(
                follower.pathBuilder()
                    .addPath(BezierCurve(scorePose, intake1CP1,intake1CP2,intake1))
                    .setConstantHeadingInterpolation(Math.toRadians(0.0))
                    .build()
            ),
            Intake.stop
        )

        val firstSetIntakeToLaunch = SequentialGroup(
            ParallelGroup(
                Intake.reverseIntakeSlow, // Clear clogged balls
                shootWithDistance, // Prep flywheel
                FollowPath(
                    follower.pathBuilder()
                        .addPath(BezierLine(firstSetStart, firstSetEnd))
                        .addPath(BezierLine(firstSetEnd, launchPoint))
                        .setTangentHeadingInterpolation()
                        .build()
                )
            ),
            Intake.reverseIntakeSlow // Shoot once at launch point
        )

        val launchToSecondSet = SequentialGroup(
            Outtake.flywheelOff,
            Intake.runIntake, // Start intaking
            FollowPath(
                follower.pathBuilder()
                    .addPath(BezierLine(launchPoint, secondSetStart))
                    .setLinearHeadingInterpolation(toRadians(138.0), toRadians(180.0))
                    .build()
            )
        )

        val secondSetIntakeToLaunch = SequentialGroup(
            ParallelGroup(
                Intake.reverseIntakeSlow, // Clear clogged balls
                shootWithDistance, // Prep flywheel
                FollowPath(
                    follower.pathBuilder()
                        .addPath(BezierLine(secondSetStart, secondSetEnd))
                        .addPath(BezierLine(secondSetEnd, launchPoint))
                        .setTangentHeadingInterpolation()
                        .build()
                )
            ),
            Intake.reverseIntakeSlow // Shoot once at launch point
        )

        val launchToThirdSet = SequentialGroup(
            Outtake.flywheelOff,
            Intake.runIntake, // Start intaking
            FollowPath(
                follower.pathBuilder()
                    .addPath(BezierLine(launchPoint, thirdSetStart))
                    .setLinearHeadingInterpolation(toRadians(138.0), toRadians(180.0))
                    .build()
            )
        )

        val thirdSetIntakeToLaunch = SequentialGroup(
            ParallelGroup(
                Intake.reverseIntakeSlow, // Clear clogged balls
                shootWithDistance, // Prep flywheel
                FollowPath(
                    follower.pathBuilder()
                        .addPath(BezierLine(thirdSetStart, thirdSetEnd))
                        .addPath(BezierLine(thirdSetEnd, launchPoint))
                        .setTangentHeadingInterpolation()
                        .build()
                )
            ),
            Intake.reverseIntakeSlow, // Shoot once at launch point
            Outtake.flywheelOff,
            Intake.stopIntake
        )

        fun next(): SequentialGroup {
            return when(index++) {
                0 -> startToLaunch
                1 -> launchToFirstSet
                2 -> firstSetIntakeToLaunch
                3 -> launchToSecondSet
                4 -> secondSetIntakeToLaunch
                5 -> launchToThirdSet
                6 -> thirdSetIntakeToLaunch
                else -> SequentialGroup() // Done
            }
        }
    }
}