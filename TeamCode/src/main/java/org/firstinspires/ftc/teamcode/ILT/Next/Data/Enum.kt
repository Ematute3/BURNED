@file:Suppress("PackageName")

package org.firstinspires.ftc.teamcode.ILT.Next.Data

import com.pedropathing.geometry.Pose
import com.skeletonarmy.marrow.zones.Point
import com.skeletonarmy.marrow.zones.PolygonZone
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data object ROBOT {
    private const val FIELD_WIDTH: Double = 141.5
    private const val FIELD_LENGTH: Double = 141.5
    private const val TURRET_Y_OFFSET: Double = -0.6
    private const val FAR_ZONE_BUFFER: Double = 12.0
    private const val CLOSE_ZONE_BUFFER: Double = 11.0
    private const val ROBOT_WIDTH = 18.0
    private const val ROBOT_LENGTH = 18.0

    internal var currAlliance: Alliance = Alliance.BLUE
    internal var currStage: Stage = Stage.TELEOP
    internal var currTeleOpStartPose: Pose = Pose(72.0,72.0,0.0)

    internal fun correctedPose(predictionTime: Double): Pose {
        val v = follower.velocity
        val predictedPose = Pose(
            v.xComponent * predictionTime,  // distance = velocity × time
            v.yComponent * predictionTime,
            follower.angularVelocity * predictionTime  // angle change
        )
        return shooterPose() + predictedPose
    }

    internal fun shooterPose(): Pose {
        val a: Double = follower.heading
        return follower.pose + Pose(
            TURRET_Y_OFFSET * cos(a),
            TURRET_Y_OFFSET * sin(a)
        )
    }

    internal fun getDistanceFromGoal(): Double = shooterPose().distanceFrom(currAlliance.goalPoses.flywheelGoalPose)
     val closeLaunchZone = PolygonZone(
        Point(144.0, 144.0),  // Top-right corner (goal area)
        Point(72.0, 72.0),    // Field center
        Point(0.0, 144.0)     // Top-left corner
    )

     val farLaunchZone = PolygonZone(
        Point(48.0, 0.0),     // Bottom-left
        Point(72.0, 24.0),    // Middle
        Point(96.0, 0.0)      // Bottom-right
    )

    private val blueBase = PolygonZone(Point(105.5, 33.5), 20.0, 20.0)
    private val redBase = PolygonZone(Point(38.5, 33.5), 20.0, 20.0)
    private val robotZone = PolygonZone(ROBOT_WIDTH, ROBOT_LENGTH)
}

enum class Alliance {
    BLUE {
        override val resetPoses: ResetPoses  =
            ResetPoses(Pose(9.5,8.9,-PI).mirror(),
            Pose(117.0,129.0,-2.448).mirror(),
            Pose(116.0,130.0,-0.939).mirror())
        override val goalPoses: GoalPoses =
            GoalPoses(Pose(141.0,140.0).mirror(),
                Pose(141.0,140.0).mirror(),
                Pose(140.0,140.0).mirror())
    },
    RED{
        override val resetPoses: ResetPoses  =
            ResetPoses(Pose(9.5,8.9,-PI),
                Pose(117.0,129.0,-2.448),
                Pose(116.0,130.0,-0.939))
        override val goalPoses: GoalPoses =
            GoalPoses(Pose(136.0,140.0),
                Pose(136.0,140.0),
                Pose(132.0,140.0))
    };
    abstract val resetPoses: ResetPoses
    abstract val goalPoses: GoalPoses
}

enum class Motif {
    UNKNOWN { override val tagID: Int = 0 },
    PPG { override val tagID: Int = 23 },
    PGP { override val tagID: Int = 22 },
    GPP { override val tagID: Int = 21 };
    abstract val tagID: Int
}

enum class Stage {
    TELEOP,
    AUTONOMOUS;
    open var currMotif: Motif = Motif.UNKNOWN
    open var useFlywheelVel: Boolean = false
}

data class GoalPoses(val turretGoalPoseFar: Pose, val turretGoalPoseClose: Pose, val flywheelGoalPose: Pose)

data class ResetPoses(val resetPose1: Pose, val resetPose2: Pose, val resetPose3: Pose)