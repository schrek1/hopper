package cz.schrek.hopper.application.domain.model

data class GameTurnSnapshot(
    val order: Int,
    val layout: GameBoard.Layout,
    val currentPosition: GameBoard.Coordinates,
    val currentVelocity: HopperMovementAbility
)
