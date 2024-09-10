package cz.schrek.hopper.application.domain.usecase

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.GameBoard.Area
import cz.schrek.hopper.application.domain.model.GameBoard.Coordinates
import cz.schrek.hopper.application.domain.model.GameBoard.Field.Type.*
import cz.schrek.hopper.application.domain.model.GameBoard.Layout
import cz.schrek.hopper.application.domain.model.GameBoard.ObstacleArea
import cz.schrek.hopper.application.domain.model.GameTurnSnapshot
import cz.schrek.hopper.application.domain.model.Hooper
import cz.schrek.hopper.application.port.`in`.GameRequest
import cz.schrek.hopper.application.port.`in`.SearchHooperShortestPathResult
import cz.schrek.hopper.test.utils.CoroutineUtils.runTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HopperShortPathUseCaseTest {

    val useCase = HooperShortPathUseCaseImpl()

    @Nested
    inner class AutoSearch {
        @Test
        fun `start is on end - should have zero hops`(): Unit = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 10, height = 10),
                startPosition = Coordinates(x = 4, y = 7),
                endPosition = Coordinates(x = 4, y = 7),
                obstacles = listOf()
            )

            val gameRequest = GameRequest(gameBoardLayout, Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY)

            val result = useCase.searchShortestPath(gameRequest)

            result shouldContainExactly mapOf(
                gameRequest to SearchHooperShortestPathResult.Success(
                    listOf(
                        GameTurnSnapshot(
                            order = 0,
                            hopperPosition = GameBoard.Field(
                                position = Coordinates(x = 4, y = 7),
                                type = END
                            ),
                            hopperMovementAbility = Hooper.MovementAbility.of(jumpSizeX = 2, jumpSizeY = 1)
                        )
                    )
                )
            )
        }

        @Test
        fun `layout reachable with single hop`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 3, height = 3),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 2, y = 1),
                obstacles = emptyList()
            )

            val gameRequest = GameRequest(gameBoardLayout, Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY)

            val result = useCase.searchShortestPath(gameRequest)

            result[gameRequest].shouldBeInstanceOf<SearchHooperShortestPathResult.Success>().should {
                it.hops.shouldContainExactly(
                    GameTurnSnapshot(
                        order = 0,
                        hopperPosition = GameBoard.Field(
                            position = Coordinates(x = 0, y = 0),
                            type = START
                        ),
                        hopperMovementAbility = Hooper.MovementAbility.of(jumpSizeX = 2, jumpSizeY = 1)
                    ),
                    GameTurnSnapshot(
                        order = 1,
                        hopperPosition = GameBoard.Field(
                            position = Coordinates(x = 2, y = 1),
                            type = END
                        ),
                        hopperMovementAbility = Hooper.MovementAbility.of(jumpSizeX = 2, jumpSizeY = 1)
                    )
                )
            }
        }

        @Test
        fun `layout reachable with four hops`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 5, height = 5),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 4, y = 4),
                obstacles = listOf(
                    ObstacleArea(fromX = 2, toX = 2, fromY = 2, toY = 2)
                )
            )

            val gameRequest = GameRequest(gameBoardLayout, Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY)

            val result = useCase.searchShortestPath(gameRequest)

            result[gameRequest].shouldBeInstanceOf<SearchHooperShortestPathResult.Success>().should { result ->
                result.hops
                    .map { Triple(it.order, it.hopperPosition.position, it.hopperPosition.type) }
                    .shouldContainExactly(
                        Triple(0, Coordinates(x = 0, y = 0), START),
                        Triple(1, Coordinates(x = 2, y = 1), FREE),
                        Triple(2, Coordinates(x = 4, y = 2), FREE),
                        Triple(3, Coordinates(x = 2, y = 3), FREE),
                        Triple(4, Coordinates(x = 4, y = 4), END)
                    )
            }
        }

        @Test
        fun `path ends in obstacles`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 5, height = 5),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 4, y = 4),
                obstacles = listOf(
                    ObstacleArea(fromX = 1, toX = 4, fromY = 0, toY = 3),
                    ObstacleArea(fromX = 0, toX = 0, fromY = 1, toY = 4),
                    ObstacleArea(fromX = 0, toX = 3, fromY = 4, toY = 4)
                )
            )

            val gameRequest = GameRequest(gameBoardLayout, Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY)

            val result = useCase.searchShortestPath(gameRequest)

            result[gameRequest].shouldBeInstanceOf<SearchHooperShortestPathResult.NotFound>().should { result ->
                result.reason.shouldBe("All paths are blocked")
            }
        }

        @Test
        fun `layout with no solution`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 5, height = 5),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 4, y = 4),
                obstacles = listOf(
                    ObstacleArea(fromX = 1, toX = 3, fromY = 0, toY = 0),
                    ObstacleArea(fromX = 1, toX = 3, fromY = 2, toY = 4)
                )
            )

            val gameRequest = GameRequest(gameBoardLayout, Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY)

            val result = useCase.searchShortestPath(gameRequest)

            result[gameRequest].shouldBeInstanceOf<SearchHooperShortestPathResult.NotFound>().should { result ->
                result.reason.shouldBe("No path found")
            }
        }

        @Test
        fun `large layout with sparse obstacles`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 10, height = 10),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 9, y = 9),
                obstacles = listOf(
                    ObstacleArea(fromX = 3, toX = 3, fromY = 3, toY = 3),
                    ObstacleArea(fromX = 6, toX = 6, fromY = 6, toY = 6)
                )
            )

            val gameRequest = GameRequest(gameBoardLayout, Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY)

            val result = useCase.searchShortestPath(gameRequest)

            result[gameRequest].shouldBeInstanceOf<SearchHooperShortestPathResult.Success>().should { result ->
                result.hops
                    .map { Triple(it.order, it.hopperPosition.position, it.hopperPosition.type) }
                    .shouldContainExactly(
                        Triple(0, Coordinates(x = 0, y = 0), START),
                        Triple(1, Coordinates(x = 2, y = 1), FREE),
                        Triple(2, Coordinates(x = 4, y = 2), FREE),
                        Triple(3, Coordinates(x = 6, y = 3), FREE),
                        Triple(4, Coordinates(x = 7, y = 5), FREE),
                        Triple(5, Coordinates(x = 8, y = 7), FREE),
                        Triple(6, Coordinates(x = 9, y = 9), END)
                    )
            }
        }

        @Test
        fun `layout with dense obstacles`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 8, height = 8),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 7, y = 7),
                obstacles = listOf(
                    ObstacleArea(fromX = 1, toX = 1, fromY = 1, toY = 1),
                    ObstacleArea(fromX = 2, toX = 2, fromY = 2, toY = 2),
                    ObstacleArea(fromX = 3, toX = 3, fromY = 3, toY = 3),
                    ObstacleArea(fromX = 4, toX = 4, fromY = 4, toY = 4),
                    ObstacleArea(fromX = 5, toX = 5, fromY = 5, toY = 5),
                    ObstacleArea(fromX = 6, toX = 6, fromY = 6, toY = 6)
                )
            )

            val gameRequest = GameRequest(gameBoardLayout, Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY)

            val result = useCase.searchShortestPath(gameRequest)

            result[gameRequest].shouldBeInstanceOf<SearchHooperShortestPathResult.Success>().should { result ->
                result.hops
                    .map { Triple(it.order, it.hopperPosition.position, it.hopperPosition.type) }
                    .shouldContainExactly(
                        Triple(0, Coordinates(x = 0, y = 0), START),
                        Triple(1, Coordinates(x = 2, y = 1), FREE),
                        Triple(2, Coordinates(x = 4, y = 2), FREE),
                        Triple(3, Coordinates(x = 6, y = 3), FREE),
                        Triple(4, Coordinates(x = 7, y = 5), FREE),
                        Triple(5, Coordinates(x = 5, y = 6), FREE),
                        Triple(6, Coordinates(x = 7, y = 7), END)
                    )
            }
        }

        @Disabled("This test is only for benchmarking that we have set upper bound for hops")
        @Test
        fun `huge layout - max hops reached`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 30, height = 30),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 29, y = 29),
                obstacles = listOf(
                    ObstacleArea(fromX = 1, toX = 1, fromY = 2, toY = 28),
                    ObstacleArea(fromX = 3, toX = 3, fromY = 1, toY = 27),
                    ObstacleArea(fromX = 5, toX = 5, fromY = 2, toY = 28),
                    ObstacleArea(fromX = 7, toX = 7, fromY = 1, toY = 27),
                    ObstacleArea(fromX = 9, toX = 9, fromY = 2, toY = 28),
                    ObstacleArea(fromX = 11, toX = 11, fromY = 1, toY = 27),
                    ObstacleArea(fromX = 13, toX = 13, fromY = 2, toY = 28),
                    ObstacleArea(fromX = 15, toX = 15, fromY = 1, toY = 27),
                    ObstacleArea(fromX = 17, toX = 17, fromY = 2, toY = 28),
                    ObstacleArea(fromX = 19, toX = 19, fromY = 1, toY = 27),
                    ObstacleArea(fromX = 21, toX = 21, fromY = 2, toY = 28),
                    ObstacleArea(fromX = 23, toX = 23, fromY = 1, toY = 27),
                    ObstacleArea(fromX = 25, toX = 25, fromY = 2, toY = 28),
                    ObstacleArea(fromX = 27, toX = 27, fromY = 1, toY = 27),
                    ObstacleArea(fromX = 1, toX = 29, fromY = 28, toY = 28)
                )
            )

            val gameRequest = GameRequest(gameBoardLayout, Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY)

            val result = useCase.searchShortestPath(gameRequest)

            result[gameRequest].shouldBeInstanceOf<SearchHooperShortestPathResult.NotFound>().should { result ->
                result.reason.shouldBe("Max hops reached")
            }
        }
    }

    @Nested
    inner class InteractiveSearch {
        @Test
        fun `start is on end - should have zero hops`(): Unit = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 10, height = 10),
                startPosition = Coordinates(x = 4, y = 7),
                endPosition = Coordinates(x = 4, y = 7),
                obstacles = listOf()
            )

            val result = useCase.searchShortestPathInteractive(gameBoardLayout) { turn, _ ->
                error("Unexpected turn $turn")
            }

            result shouldBe SearchHooperShortestPathResult.Success(
                listOf(
                    GameTurnSnapshot(
                        order = 0,
                        hopperPosition = GameBoard.Field(
                            position = Coordinates(x = 4, y = 7),
                            type = END
                        ),
                        hopperMovementAbility = Hooper.MovementAbility.NOT_MOVING
                    )
                )
            )
        }

        @Test
        fun `layout reachable with single hop`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 3, height = 3),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 2, y = 1),
                obstacles = emptyList()
            )


            val result = useCase.searchShortestPathInteractive(gameBoardLayout) { turn, _ ->
                when (turn) {
                    1 -> Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY
                    else -> error("Unexpected turn $turn")
                }

            }

            result.shouldBeInstanceOf<SearchHooperShortestPathResult.Success>().should {
                it.hops.shouldContainExactly(
                    GameTurnSnapshot(
                        order = 0,
                        hopperPosition = GameBoard.Field(
                            position = Coordinates(x = 0, y = 0),
                            type = START
                        ),
                        hopperMovementAbility = Hooper.MovementAbility.NOT_MOVING
                    ),
                    GameTurnSnapshot(
                        order = 1,
                        hopperPosition = GameBoard.Field(
                            position = Coordinates(x = 2, y = 1),
                            type = END
                        ),
                        hopperMovementAbility = Hooper.MovementAbility.of(jumpSizeX = 2, jumpSizeY = 1)
                    )
                )
            }
        }

        @Test
        fun `layout reachable with four hops`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 5, height = 5),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 4, y = 4),
                obstacles = listOf(
                    ObstacleArea(fromX = 2, toX = 2, fromY = 2, toY = 2)
                )
            )

            val result = useCase.searchShortestPathInteractive(gameBoardLayout) { turn, _ ->
                when (turn) {
                    in 1..4 -> Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY
                    else -> error("Unexpected turn $turn")
                }
            }

            result.shouldBeInstanceOf<SearchHooperShortestPathResult.Success>().should { result ->
                result.hops
                    .map { Triple(it.order, it.hopperPosition.position, it.hopperPosition.type) }
                    .shouldContainExactly(
                        Triple(0, Coordinates(x = 0, y = 0), START),
                        Triple(1, Coordinates(x = 2, y = 1), FREE),
                        Triple(2, Coordinates(x = 4, y = 2), FREE),
                        Triple(3, Coordinates(x = 2, y = 3), FREE),
                        Triple(4, Coordinates(x = 4, y = 4), END)
                    )
            }
        }

        @Test
        fun `path ends in obstacles`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 5, height = 5),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 4, y = 4),
                obstacles = listOf(
                    ObstacleArea(fromX = 1, toX = 4, fromY = 0, toY = 3),
                    ObstacleArea(fromX = 0, toX = 0, fromY = 1, toY = 4),
                    ObstacleArea(fromX = 0, toX = 3, fromY = 4, toY = 4)
                )
            )

            val result = useCase.searchShortestPathInteractive(gameBoardLayout) { _, _ ->
                Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY
            }

            result.shouldBeInstanceOf<SearchHooperShortestPathResult.NotFound>().should { result ->
                result.reason.shouldBe("All paths are blocked")
            }
        }

        @Test
        fun `layout with no solution`() = runTest {
            val gameBoardLayout = Layout(
                area = Area(width = 5, height = 5),
                startPosition = Coordinates(x = 0, y = 0),
                endPosition = Coordinates(x = 4, y = 4),
                obstacles = listOf(
                    ObstacleArea(fromX = 1, toX = 3, fromY = 0, toY = 0),
                    ObstacleArea(fromX = 1, toX = 3, fromY = 2, toY = 4)
                )
            )

            val result = useCase.searchShortestPathInteractive(gameBoardLayout) { _, _ ->
                Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY
            }

            result.shouldBeInstanceOf<SearchHooperShortestPathResult.NotFound>().should { result ->
                result.reason.shouldBe("No path found")
            }
        }
    }

}
