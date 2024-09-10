package cz.schrek.hopper.adapter.input.console

import cz.schrek.hopper.adapter.input.console.ConsoleUtils.printLnError
import cz.schrek.hopper.adapter.input.console.dto.GameType
import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.GameBoard.Area
import cz.schrek.hopper.application.domain.model.GameBoard.Coordinates
import cz.schrek.hopper.application.domain.model.GameBoard.Layout
import cz.schrek.hopper.application.domain.model.Hooper
import cz.schrek.hopper.application.port.`in`.GameRequest
import cz.schrek.hopper.application.port.`in`.SearchHooperShortestPathResult

class ConsoleAdapter {

    fun readGameType(): GameType {
        println("Do you want to change movement ability in each turn? (y/n)")
        while (true) {
            when (readln().lowercase()) {
                "y" -> return GameType.INTERACTIVE
                "n" -> return GameType.AUTO
                else -> {
                    printLnError("Invalid input. Please enter y or n.")
                }
            }
        }
    }

    fun readInteractiveGame(): Layout {
        val area = loadArea()

        val (start, end) = loadStartAndEnd()

        val obstacles = loadObstacles()

        return Layout(
            area = area,
            startPosition = start,
            endPosition = end,
            obstacles = obstacles
        )
    }

    fun readGameRequests(): List<GameRequest> {
        val layoutsCount: Int = loadLayoutsCount()

        return (1..layoutsCount).map {
            val area = loadArea()

            val (start, end) = loadStartAndEnd()

            val obstacles = loadObstacles()

            val movementAbility = loadMovementAbility()

            GameRequest(
                gameBoardLayout = Layout(
                    area = area,
                    startPosition = start,
                    endPosition = end,
                    obstacles = obstacles
                ),
                movementAbility = movementAbility
            )

        }
    }

    private fun loadMovementAbility(): Hooper.MovementAbility {
        var jumpX: Int? = -1
        var jumpY: Int? = -1

        println("Do you want to use default movement ability like Chess knight [2;1]? (y/n)")
        val useDefault = readln().lowercase() == "y"

        if (useDefault) return Hooper.MovementAbility.CHESS_KNIGHT_MOVEMENT_ABILITY

        do {
            if (jumpX == null || jumpY == null) {
                printLnError("Invalid input. Please enter a vector x y separated by space.")
            }
            println("Enter hooper movement ability as vector x y:")
            val numbers = readln().split(" ")
            when {
                numbers.size != 2 -> {
                    jumpX = null
                    jumpY = null
                }

                else -> {
                    jumpX = numbers[0].toIntOrNull()
                    jumpY = numbers[1].toIntOrNull()
                }
            }
        } while (jumpX == null || jumpY == null)

        return Hooper.MovementAbility.of(jumpSizeX = jumpX, jumpSizeY = jumpY)
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
                printLnError("Invalid input. Please enter coordinates x y separated by space.")
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

    fun printResult(
        layout: Layout,
        result: SearchHooperShortestPathResult
    ) {
        val obstacles = layout.obstacles.joinToString(", ") { "[${it.fromX}-${it.toX}; ${it.fromY}-${it.toY}]" }
        val obstaclesMessage = if (obstacles.isNotEmpty()) "obstacles: $obstacles" else "any"
        println("Solution for game layout: ${layout.area.width}x${layout.area.height} with start: [${layout.startPosition.x};${layout.startPosition.y}] end: [${layout.endPosition.x};${layout.endPosition.y}] obstacles: $obstaclesMessage")
        val resultMessage = when (result) {
            is SearchHooperShortestPathResult.Success ->
                """found - hops: 
                            |${
                    result.hops.joinToString("\n") {
                        val hopperPosition = it.hopperPosition
                        val coordinates = hopperPosition.position
                        val movementAbility = it.hopperMovementAbility.getRelativeMoveAbility()
                        "${it.order}. [${coordinates.x};${coordinates.y}] - ${hopperPosition.type} - move ability (${movementAbility.first}:${movementAbility.second})"
                    }
                }
                            |""".trimMargin()

            is SearchHooperShortestPathResult.NotFound -> "not found: ${result.reason}"
        }
        println(resultMessage)

        if (result is SearchHooperShortestPathResult.Success) {
            drawVisualisationOfTurns(result, layout)
        }
    }

    private fun drawVisualisationOfTurns(result: SearchHooperShortestPathResult.Success, layout: Layout) {
        println("Details of turns:")

        result.hops.forEach { turn ->
            val moving = turn.hopperMovementAbility.getRelativeMoveAbility()
            println("Turn ${turn.order} - move ability (${moving.first}:${moving.second})")
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

    fun readMovementAbility(turn: Int, currentMovementAbility: Hooper.MovementAbility): Hooper.MovementAbility {
        if (currentMovementAbility.isMoving()) {
            val relativeMoveAbility = currentMovementAbility.getRelativeMoveAbility()

            println("Do you want change movement ability (${relativeMoveAbility.first}:${relativeMoveAbility.second}) for turn $turn? (y/n)")
            val useDefault = readln().lowercase() == "n"

            if (useDefault) return currentMovementAbility
        }

        var jumpX: Int? = -1
        var jumpY: Int? = -1

        do {
            if (jumpX == null || jumpY == null) {
                printLnError("Invalid input. Please enter a vector x y separated by space.")
            }
            println("Input movement ability vector x y (with max value 3) for turn $turn :")
            val numbers = readln().split(" ")
            when {
                numbers.size != 2 -> {
                    jumpX = null
                    jumpY = null
                }

                else -> {
                    jumpX = numbers[0].toIntOrNull()
                    jumpY = numbers[1].toIntOrNull()

                    if (jumpX == 0 && jumpY == 0) {
                        printLnError("Hooper can't move...")
                        jumpX = null
                        jumpY = null
                    }
                }
            }
        } while (jumpX == null || jumpY == null)

        return Hooper.MovementAbility.of(jumpSizeX = jumpX, jumpSizeY = jumpY)
    }
}
