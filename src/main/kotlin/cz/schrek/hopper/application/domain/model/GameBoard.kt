package cz.schrek.hopper.application.domain.model


object GameBoard {

    data class Layout(
        val area: Area,
        val startPosition: Coordinates,
        val endPosition: Coordinates,
        val obstacles: List<ObstacleArea>
    ) {
        init {
            require(area contains startPosition) { "start position must be within the game-board area" }
            require(area contains endPosition) { "end position must be within the game-board area" }

            obstacles.forEach { obstacle ->
                require(!obstacle.contains(startPosition)) { "start position $startPosition cannot be in an obstacle $obstacle" }
                require(!obstacle.contains(endPosition)) { "end position $endPosition cannot be in an obstacle $obstacle" }
                require(obstacle.allCoordinates.all { area contains it }) { "obstacle $obstacle must be within the game-board area" }
            }
        }

        fun getFieldType(position: Coordinates) = when {
            position == startPosition -> Field.Type.START
            position == endPosition -> Field.Type.END
            obstacles.any { it.contains(position) } -> Field.Type.OBSTACLE
            area contains position -> Field.Type.FREE
            else -> null
        }
    }

    data class Area(val width: Int, val height: Int) {
        private val xRange = 0..<width

        private val yRange = 0..<height

        init {
            require(width > 0) { "game-board width must be greater than 0" }
            require(height > 0) { "game-board height must be greater than 0" }
        }

        infix fun contains(position: Coordinates) = position.x in xRange && position.y in yRange
    }

    data class Field(val position: Coordinates, val type: Type) {
        enum class Type {
            FREE,
            OBSTACLE,
            START,
            END
        }
    }

    data class Coordinates(val x: Int, val y: Int) {
        operator fun plus(moveVector: MoveVector) = Coordinates(x + moveVector.jumpX, y + moveVector.jumpY)
    }

    data class ObstacleArea(
        val fromX: Int,
        val toX: Int,
        val fromY: Int,
        val toY: Int
    ) {
        val allCoordinates by lazy {
            (fromX..toX).flatMap { x ->
                (fromY..toY).map { y ->
                    Coordinates(x, y)
                }
            }.toSet()
        }

        init {
            require(fromX >= 0) { "obstacle x1 coordinate must be greater than or equal to 0" }
            require(toX >= 0) { "obstacle x2 coordinate must be greater than or equal to 0" }
            require(fromY >= 0) { "obstacle y1 coordinate must be greater than or equal to 0" }
            require(toY >= 0) { "obstacle y2 coordinate must be greater than or equal to 0" }
            require(fromX <= toX) { "coordinate x1 must be less than or equal to coordinate x2" }
            require(fromY <= toY) { "coordinate y1 must be less than or equal to coordinate y2" }
        }

        fun contains(position: Coordinates) = position.x in fromX..toX && position.y in fromY..toY
    }

}


