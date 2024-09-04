package cz.schrek.hopper.application.domain.service

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.HopperMovementAbility


object JumpsCalculator {

    fun calculateAllPossibleJumpFields(
        gameBoardLayout: GameBoard.Layout,
        jumperActualPosition: GameBoard.Coordinates,
        velocity: HopperMovementAbility
    ): Set<GameBoard.Field> =
        velocity.getAllMoves().mapNotNull { moveVector ->
            val newPosition = jumperActualPosition + moveVector
            gameBoardLayout.getFieldType(newPosition)?.let {
                GameBoard.Field(position = newPosition, type = it)
            }
        }.toSet()

}
