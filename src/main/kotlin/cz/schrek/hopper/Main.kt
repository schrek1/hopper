package cz.schrek.hopper

import cz.schrek.hopper.application.domain.model.GameBoard.Area
import cz.schrek.hopper.application.domain.model.GameBoard.Coordinates
import cz.schrek.hopper.application.domain.model.GameBoard.Layout
import cz.schrek.hopper.application.domain.model.GameBoard.ObstacleArea
import cz.schrek.hopper.application.domain.usecase.HooperShortPathUseCaseImpl
import kotlinx.coroutines.runBlocking

object Main {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val useCase = HooperShortPathUseCaseImpl()

        val layout = Layout(
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

        val result = useCase.searchShortestPath(layout)

        result.forEach { (layout, searchHooperShortestPathResult) ->
            println("Layout: $layout")
            println("Result: $searchHooperShortestPathResult")
        }
    }
}
