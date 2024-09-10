package cz.schrek.hopper.application.domain.model

import kotlin.math.abs

object Hooper {

    data class MovementAbility private constructor(
        private val jumpSizeX: Int,
        private val jumpSizeY: Int
    ) {

        init {
            require(jumpSizeX in MOVEMENT_RANGE) { "movement for x-axis must be in $MOVEMENT_RANGE" }
            require(jumpSizeY in MOVEMENT_RANGE) { "movement y-axis must be in $MOVEMENT_RANGE" }
        }

        fun getAllMoves() = setOf(
            MoveVector(jumpSizeX, jumpSizeY),
            MoveVector(-jumpSizeX, jumpSizeY),
            MoveVector(jumpSizeX, -jumpSizeY),
            MoveVector(-jumpSizeX, -jumpSizeY),
            MoveVector(jumpSizeY, jumpSizeX),
            MoveVector(-jumpSizeY, jumpSizeX),
            MoveVector(jumpSizeY, -jumpSizeX),
            MoveVector(-jumpSizeY, -jumpSizeX),
        )

        fun getRelativeMoveAbility() = Pair(abs(jumpSizeX), abs(jumpSizeY))

        fun isMoving() = jumpSizeX != 0 || jumpSizeY != 0

        companion object {
            const val MAX_MOVEMENT_SIZE = 3
            private val MOVEMENT_RANGE = -MAX_MOVEMENT_SIZE..MAX_MOVEMENT_SIZE

            val CHESS_KNIGHT_MOVEMENT_ABILITY = of(2, 1)
            val NOT_MOVING = of(0, 0)

            fun of(jumpSizeX: Int, jumpSizeY: Int) = MovementAbility(
                jumpSizeX = resolveVelocity(jumpSizeX),
                jumpSizeY = resolveVelocity(jumpSizeY)
            )

            private fun resolveVelocity(velocity: Int): Int {
                return when {
                    velocity > MAX_MOVEMENT_SIZE -> MAX_MOVEMENT_SIZE
                    velocity < -MAX_MOVEMENT_SIZE -> -MAX_MOVEMENT_SIZE
                    else -> velocity
                }
            }
        }
    }
}
