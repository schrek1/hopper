package cz.schrek.hopper.adapter.input.console

import cz.schrek.hopper.adapter.input.console.ConsoleUtils.printLnError
import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.GameBoard.Area
import cz.schrek.hopper.application.domain.model.GameBoard.Coordinates
import cz.schrek.hopper.application.domain.model.GameBoard.Layout
import cz.schrek.hopper.application.port.SearchHooperShortestPathResult

class ConsoleAdapter {

    fun readGameLayout(): List<Layout> {
        val layoutsCount: Int = loadLayoutsCount()

        return (1..layoutsCount).map {
            val area = loadArea()

            val (start, end) = loadStartAndEnd()

            val obstacles = loadObstacles()

            Layout(
                area = area,
                startPosition = start,
                endPosition = end,
                obstacles = obstacles
            )
        }
    }

    private fun loadObstacles(): List<GameBoard.ObstacleArea> {
        var obstaclesCount: Int? = -1

        do {
            if (obstaclesCount == null) {
                printLnError("Invalid input. Please enter a number.")
            }
            println("Enter count of obstacles:")
            obstaclesCount = readln().toIntOrNull()
        } while (obstaclesCount == null)

        return (1..obstaclesCount).map { index ->
            var startX: Int? = -1
            var startY: Int? = -1
            var endX: Int? = -1
            var endY: Int? = -1

            do {
                if (startX == null || startY == null || endX == null || endY == null) {
                    printLnError("Invalid input. Please enter a start and end coordinates startX startY endX endY separated by space.")
                }
                println("Enter $index. obstacle start and end coordinates startX startY endX endY:")
                val numbers = readln().split(" ")
                when {
                    numbers.size != 4 -> {
                        startX = null
                        startY = null
                        endX = null
                        endY = null
                    }

                    else -> {
                        startX = numbers[0].toIntOrNull()
                        endX = numbers[1].toIntOrNull()
                        startY = numbers[2].toIntOrNull()
                        endY = numbers[3].toIntOrNull()
                    }
                }
            } while (startX == null || startY == null || endX == null || endY == null)

            GameBoard.ObstacleArea(fromX = startX, toX = endX, fromY = startY, toY = endY)
        }
    }

    private fun loadStartAndEnd(): Pair<Coordinates, Coordinates> {
        var startX: Int? = -1
        var startY: Int? = -1
        var endX: Int? = -1
        var endY: Int? = -1

        do {
            if (startX == null || startY == null || endX == null || endY == null) {
                printLnError("Invalid input. Please enter a start and end coordinates startX startY endX endY separated by space.")
            }
            println("Enter  start and end coordinates startX startY endX endY:")
            val numbers = readln().split(" ")
            when {
                numbers.size != 4 -> {
                    startX = null
                    startY = null
                    endX = null
                    endY = null
                }

                else -> {
                    startX = numbers[0].toIntOrNull()
                    startY = numbers[1].toIntOrNull()
                    endX = numbers[2].toIntOrNull()
                    endY = numbers[3].toIntOrNull()
                }
            }
        } while (startX == null || startY == null || endX == null || endY == null)

        return Coordinates(x = startX, y = startY) to Coordinates(x = endX, y = endY)
    }

    private fun loadArea(): Area {
        var width: Int? = -1
        var height: Int? = -1

        do {
            if (width == null || height == null) {
                printLnError("Invalid input. Please enter a coordinates x y separated by space.")
            }
            println("Enter game board dimension x y:")
            val numbers = readln().split(" ")
            when {
                numbers.size != 2 -> {
                    width = null
                    height = null
                }

                else -> {
                    width = numbers[0].toIntOrNull()
                    height = numbers[1].toIntOrNull()
                }
            }
        } while (width == null || height == null)

        return Area(width = width, height = height)
    }

    private fun loadLayoutsCount(): Int {
        var layoutsCount: Int? = -1

        do {
            if (layoutsCount == null) {
                printLnError("Invalid input. Please enter a number.")
            }
            println("Enter count of game board layouts:")
            layoutsCount = readln().toIntOrNull()
        } while (layoutsCount == null)
        return layoutsCount
    }


    fun printResults(results: Map<Layout, SearchHooperShortestPathResult>) {
        results.forEach { (layout, result) ->
            printResult(layout, result)
            if (result is SearchHooperShortestPathResult.Success) {
                drawVisualisationOfTurns(result, layout)
            }
        }
    }

    private fun printResult(
        layout: Layout,
        result: SearchHooperShortestPathResult
    ) {
        val obstacles = layout.obstacles.joinToString(", ") { "[${it.fromX}-${it.toX}; ${it.fromY}-${it.toY}]" }
        println("Solution for game layout: ${layout.area.width}x${layout.area.height} with start: [${layout.startPosition.x};${layout.startPosition.y}] end: [${layout.endPosition.x};${layout.endPosition.y}] obstacles: $obstacles")
        val resultMessage = when (result) {
            is SearchHooperShortestPathResult.Success ->
                """found - hops: 
                            |${
                    result.hops.joinToString("\n") {
                        val hopperPosition = it.hopperPosition
                        val coordinates = hopperPosition.position
                        "${it.order}. [${coordinates.x};${coordinates.y}] - ${hopperPosition.type}"
                    }
                }
                            |""".trimMargin()

            is SearchHooperShortestPathResult.NotFound -> "not found: ${result.reason}"
        }
        println(resultMessage)
    }

    private fun drawVisualisationOfTurns(result: SearchHooperShortestPathResult.Success, layout: Layout) {
        println("Details of turns:")

        result.hops.forEach { turn ->
            println("Turn ${turn.order}:")
            layout.area.height.let { height ->
                layout.area.width.let { width ->
                    for (y in 0..<height) {
                        for (x in 0..<width) {
                            val fieldChar = when (Coordinates(x, y)) {
                                turn.hopperPosition.position -> "♘"
                                else -> when (layout.getFieldType(Coordinates(x, y))!!) {
                                    GameBoard.Field.Type.START -> "◎"
                                    GameBoard.Field.Type.END -> "◉"
                                    GameBoard.Field.Type.OBSTACLE -> "▦"
                                    GameBoard.Field.Type.FREE -> "▢"
                                }
                            }

                            print("$fieldChar ")
                        }
                        println()
                    }
                }
            }
        }
    }
}
