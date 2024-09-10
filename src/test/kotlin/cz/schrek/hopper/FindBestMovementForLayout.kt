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

        val results = mutableMapOf<Pair<Int, Int>, String>()

        for (x in 0 until gameBoardLayout.area.width) {
            for (y in 0 until gameBoardLayout.area.height) {
                if (results.containsKey(Pair(y, x)) || results.containsKey(Pair(x, y))) continue

                val result = useCase.searchShortestPath(
                    GameRequest(
                        gameBoardLayout = gameBoardLayout,
                        movementAbility = Hooper.MovementAbility.of(y, x)
                    )
                ).entries.first().value

                val output = when (result) {
                    is SearchHooperShortestPathResult.Success -> "found in ${result.hops.size} hoops"
                    is SearchHooperShortestPathResult.NotFound -> "not found - ${result.reason}"
                }

                results[Pair(x, y)] = output
            }
        }

        results.entries.sortedBy { it.value }.forEach {
            println("Movement: ${it.key.first}, ${it.key.second} - ${it.value}")
        }
    }
}
