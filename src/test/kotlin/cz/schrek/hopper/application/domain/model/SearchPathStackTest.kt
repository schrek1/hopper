package cz.schrek.hopper.application.domain.model

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SearchPathStackTest {

    @Test
    fun `creation test`() {
        val snapshot1 = GameTurnSnapshot(
            order = 1,
            hopperPosition = GameBoard.Field(GameBoard.Coordinates(1, 4), GameBoard.Field.Type.FREE),
            hopperMovementAbility = Hooper.MovementAbility.of(1, 1)
        )
        val snapshot2 = GameTurnSnapshot(
            order = 2,
            hopperPosition = GameBoard.Field(GameBoard.Coordinates(5, 2), GameBoard.Field.Type.OBSTACLE),
            hopperMovementAbility = Hooper.MovementAbility.of(2, 1)
        )

        val stack = SearchPathStack.of(snapshot1, snapshot2)

        val copyOfStack = stack.getFullStack()
        copyOfStack shouldContainExactly listOf(snapshot1, snapshot2)
    }

    @Test
    fun `get full stack test`() {
        val snapshot1 = GameTurnSnapshot(
            order = 1,
            hopperPosition = GameBoard.Field(GameBoard.Coordinates(1, 4), GameBoard.Field.Type.FREE),
            hopperMovementAbility = Hooper.MovementAbility.of(1, 1)
        )
        val snapshot2 = GameTurnSnapshot(
            order = 2,
            hopperPosition = GameBoard.Field(GameBoard.Coordinates(5, 2), GameBoard.Field.Type.OBSTACLE),
            hopperMovementAbility = Hooper.MovementAbility.of(2, 1)
        )

        val snapshot3 = GameTurnSnapshot(
            order = 3,
            hopperPosition = GameBoard.Field(GameBoard.Coordinates(5, 2), GameBoard.Field.Type.OBSTACLE),
            hopperMovementAbility = Hooper.MovementAbility.of(2, 1)
        )

        val stack = SearchPathStack.of(snapshot1, snapshot2)

        val copyOfOriginalStack = stack.getFullStack()
        copyOfOriginalStack shouldContainExactly listOf(snapshot1, snapshot2)

        stack.push(snapshot3)
        val copyOfUpdatedStack = stack.getFullStack()

        copyOfOriginalStack shouldContainExactly listOf(snapshot1, snapshot2)
        copyOfUpdatedStack shouldContainExactly listOf(snapshot1, snapshot2, snapshot3)
    }

    @Test
    fun `get last test`() {
        val snapshot1 = GameTurnSnapshot(
            order = 1,
            hopperPosition = GameBoard.Field(GameBoard.Coordinates(1, 4), GameBoard.Field.Type.FREE),
            hopperMovementAbility = Hooper.MovementAbility.of(1, 1)
        )
        val snapshot2 = GameTurnSnapshot(
            order = 2,
            hopperPosition = GameBoard.Field(GameBoard.Coordinates(5, 2), GameBoard.Field.Type.OBSTACLE),
            hopperMovementAbility = Hooper.MovementAbility.of(2, 1)
        )

        val stack = SearchPathStack.of(snapshot1)

        stack.getLast() shouldBe snapshot1

        stack.push(snapshot2)

        stack.getLast() shouldBe snapshot2
    }

    @Nested
    inner class HasAlreadyVisitedInStack {
        @Test
        fun `empty - should return false`() {
            SearchPathStack().hasAlreadyVisitedInStack() shouldBe false
        }

        @Test
        fun `has duplicity - should return true`() {
            val snapshot = GameTurnSnapshot(
                order = 1,
                hopperPosition = GameBoard.Field(GameBoard.Coordinates(1, 4), GameBoard.Field.Type.FREE),
                hopperMovementAbility = Hooper.MovementAbility.of(1, 1)
            )

            SearchPathStack.of(snapshot, snapshot.copy(order = 2)).hasAlreadyVisitedInStack() shouldBe true
        }

        @Test
        fun `no duplicity - should return false`() {
            val snapshot1 = GameTurnSnapshot(
                order = 1,
                hopperPosition = GameBoard.Field(GameBoard.Coordinates(1, 4), GameBoard.Field.Type.FREE),
                hopperMovementAbility = Hooper.MovementAbility.of(1, 1)
            )

            val snapshot2 = GameTurnSnapshot(
                order = 2,
                hopperPosition = GameBoard.Field(GameBoard.Coordinates(5, 8), GameBoard.Field.Type.FREE),
                hopperMovementAbility = Hooper.MovementAbility.of(1, 1)
            )

            SearchPathStack.of(snapshot1, snapshot2).hasAlreadyVisitedInStack() shouldBe false
        }
    }


}
