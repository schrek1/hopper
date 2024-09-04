package cz.schrek.hopper.application.domain.model

data class HopperMovementAbility private constructor(
    private val jumpSizeX: Int,
    private val jumpSizeY: Int
) {

    init {
        require(jumpSizeX in VELOCITY_RANGE) { "velocity for x-axis must be in $VELOCITY_RANGE" }
        require(jumpSizeY in VELOCITY_RANGE) { "velocity y-axis must be in $VELOCITY_RANGE" }
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

    companion object {
        private const val MAX_VELOCITY = 3
        val VELOCITY_RANGE = -MAX_VELOCITY..MAX_VELOCITY

        fun of(jumpSizeX: Int, jumpSizeY: Int) = HopperMovementAbility(
            jumpSizeX = resolveVelocity(jumpSizeX),
            jumpSizeY = resolveVelocity(jumpSizeY)
        )

        private fun resolveVelocity(velocity: Int): Int {
            return when {
                velocity > MAX_VELOCITY -> MAX_VELOCITY
                velocity < -MAX_VELOCITY -> -MAX_VELOCITY
                else -> velocity
            }
        }
    }
}
