package cz.schrek.hopper

import cz.schrek.hopper.application.domain.model.GameBoard.Area
import cz.schrek.hopper.application.domain.model.GameBoard.Coordinates
import cz.schrek.hopper.application.domain.model.GameBoard.Layout
import cz.schrek.hopper.application.domain.model.GameBoard.ObstacleArea
import cz.schrek.hopper.application.domain.model.Hooper
import cz.schrek.hopper.application.domain.usecase.HooperShortPathUseCaseImpl
import cz.schrek.hopper.application.port.`in`.GameRequest
import cz.schrek.hopper.application.port.`in`.SearchHooperShortestPathResult
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class FindBestMovementForLayout {

    val useCase = HooperShortPathUseCaseImpl()

    @Test
    fun `print best solution`() = runBlocking {
        val gameBoardLayout = Layout(
            area = Area(width = 5, height = 5),
            startPosition = Coordinates(x = 0, y = 0),
            endPosition = Coordinates(x = 4, y = 4),
            obstacles = listOf(
                ObstacleArea(fromX = 1, toX = 1, fromY = 1, toY = 3),
                ObstacleArea(fromX = 2, toX = 2, fromY = 2, toY = 3),
                ObstacleArea(fromX = 3, toX = 3, fromY = 3, toY = 3)
            )
        )

        val allMovements = mutableSetOf<Pair<Int, Int>>()

        for (x in 0 until Hooper.MovementAbility.MAX_MOVEMENT_SIZE) {
            for (y in 0 until Hooper.MovementAbility.MAX_MOVEMENT_SIZE) {
                if (allMovements.contains(Pair(y, x)) || allMovements.contains(Pair(x, y))) continue
                allMovements.add(Pair(x, y))
            }
        }

        val requests = allMovements.map { (jumpX, jumpY) ->
            GameRequest(
                gameBoardLayout = gameBoardLayout,
                movementAbility = Hooper.MovementAbility.of(jumpX, jumpY)
            )
        }.toTypedArray()

        val pathSearchResults = useCase.searchShortestPath(*requests)

        data class Result(val hoops: Int, val movement: Hooper.MovementAbility, val output: String)

        val results = pathSearchResults.map { (request, result) ->
            val (hoops, output) = when (result) {
                is SearchHooperShortestPathResult.Success -> {
                    result.hops.size to "found in ${result.hops.size} hoops"
                }

                is SearchHooperShortestPathResult.NotFound -> {
                    Int.MAX_VALUE to "not found - ${result.reason}"
                }
            }

            Result(hoops, request.movementAbility, output)
        }

        results.sortedBy { it.hoops }.forEach {
            val relativeMoveAbility = it.movement.getRelativeMoveAbility()
            println("Movement: ${relativeMoveAbility.first}, ${relativeMoveAbility.second} - ${it.output}")
        }
    }
}
