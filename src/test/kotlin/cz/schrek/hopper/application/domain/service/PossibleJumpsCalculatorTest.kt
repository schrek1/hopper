package cz.schrek.hopper.application.domain.service

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.HopperMovementAbility
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import org.junit.jupiter.api.Test

class PossibleJumpsCalculatorTest {

    @Test
    fun `calculate all possible jump position - all in game board area`() {
        JumpsCalculator.calculateAllPossibleJumpFields(
            gameBoardLayout = GameBoard.Layout(
                area = GameBoard.Area(width = 10, height = 10),
                startPosition = GameBoard.Coordinates(x = 4, y = 7),
                endPosition = GameBoard.Coordinates(x = 10, y = 10),
                obstacles = listOf()
            ),
            jumperActualPosition = GameBoard.Coordinates(4, 7),
            velocity = HopperMovementAbility.of(2, 1)
        ) shouldContainExactlyInAnyOrder setOf(
            GameBoard.Field(GameBoard.Coordinates(6, 8), GameBoard.Field.Type.FREE),
            GameBoard.Field(GameBoard.Coordinates(2, 8), GameBoard.Field.Type.FREE),
            GameBoard.Field(GameBoard.Coordinates(6, 6), GameBoard.Field.Type.FREE),
            GameBoard.Field(GameBoard.Coordinates(2, 6), GameBoard.Field.Type.FREE),
            GameBoard.Field(GameBoard.Coordinates(5, 9), GameBoard.Field.Type.FREE),
            GameBoard.Field(GameBoard.Coordinates(3, 9), GameBoard.Field.Type.FREE),
            GameBoard.Field(GameBoard.Coordinates(5, 5), GameBoard.Field.Type.FREE),
            GameBoard.Field(GameBoard.Coordinates(3, 5), GameBoard.Field.Type.FREE)
        )
    }

    @Test
    fun `calculate all possible jump position - some is outside of game board area`() {
        JumpsCalculator.calculateAllPossibleJumpFields(
            gameBoardLayout = GameBoard.Layout(
                area = GameBoard.Area(width = 10, height = 10),
                startPosition = GameBoard.Coordinates(x = 4, y = 7),
                endPosition = GameBoard.Coordinates(x = 10, y = 10),
                obstacles = listOf()
            ),
            jumperActualPosition = GameBoard.Coordinates(0, 0),
            velocity = HopperMovementAbility.of(2, 1)
        ) shouldContainExactlyInAnyOrder setOf(
            GameBoard.Field(GameBoard.Coordinates(2, 1), GameBoard.Field.Type.FREE),
            GameBoard.Field(GameBoard.Coordinates(1, 2), GameBoard.Field.Type.FREE),
        )
    }
}
