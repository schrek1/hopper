package cz.schrek.hopper.application.domain.model

data class GameTurnSnapshot(
    val order: Int,
    val hopperPosition: GameBoard.Field,
    val hopperMovementAbility: Hooper.MovementAbility
)
