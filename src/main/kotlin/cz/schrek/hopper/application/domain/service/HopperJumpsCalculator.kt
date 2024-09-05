package cz.schrek.hopper.application.domain.service

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.Hooper


object HopperJumpsCalculator {

    fun resolveJumpableFields(
        gameBoardLayout: GameBoard.Layout,
        hopperActualPosition: GameBoard.Coordinates,
        movementAbility: Hooper.MovementAbility
    ): Set<GameBoard.Field> = movementAbility.getAllMoves().mapNotNull { moveVector ->
        val newPosition = hopperActualPosition + moveVector
        gameBoardLayout.getFieldType(newPosition)?.let {
            GameBoard.Field(position = newPosition, type = it)
        }
    }.toSet()

}
