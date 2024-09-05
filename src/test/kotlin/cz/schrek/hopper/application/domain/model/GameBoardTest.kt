package cz.schrek.hopper.application.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class GameBoardTest {

    @Nested
    inner class Layout {

        @ParameterizedTest
        @CsvSource(
            delimiterString = ";",
            value = [
                "11;11",
                "11;1",
                "1;11",
                "-1;1",
                "1;-1",
                "-1;-1"
            ]
        )
        fun `start position - out of game board area - should throw exception`(x: Int, y: Int) {
            shouldThrow<IllegalArgumentException> {
                GameBoard.Layout(
                    area = GameBoard.Area(width = 10, height = 10),
                    startPosition = GameBoard.Coordinates(x = x, y = y),
                    endPosition = GameBoard.Coordinates(x = 4, y = 7),
                    obstacles = listOf()
                )
            }.message shouldBe "start position must be within the game-board area"
        }

        @Test
        fun `start position - in obstacle - should throw exception`() {
            shouldThrow<IllegalArgumentException> {
                GameBoard.Layout(
                    area = GameBoard.Area(width = 10, height = 10),
                    startPosition = GameBoard.Coordinates(x = 4, y = 7),
                    endPosition = GameBoard.Coordinates(x = 1, y = 2),
                    obstacles = listOf(
                        GameBoard.ObstacleArea(fromX = 3, toX = 5, fromY = 6, toY = 8)
                    )
                )
            }.message shouldBe "start position Coordinates(x=4, y=7) cannot be in an obstacle ObstacleArea(fromX=3, toX=5, fromY=6, toY=8)"
        }

        @ParameterizedTest
        @CsvSource(
            delimiterString = ";",
            value = [
                "11;11",
                "11;1",
                "1;11",
                "-1;1",
                "1;-1",
                "-1;-1"
            ]
        )
        fun `end position - out of game board area - should throw exception`(x: Int, y: Int) {
            shouldThrow<IllegalArgumentException> {
                GameBoard.Layout(
                    area = GameBoard.Area(width = 10, height = 10),
                    startPosition = GameBoard.Coordinates(x = 4, y = 7),
                    endPosition = GameBoard.Coordinates(x = x, y = y),
                    obstacles = listOf()
                )
            }.message shouldBe "end position must be within the game-board area"

        }

        @Test
        fun `end position - in obstacle - should throw exception`() {
            shouldThrow<IllegalArgumentException> {
                GameBoard.Layout(
                    area = GameBoard.Area(width = 10, height = 10),
                    startPosition = GameBoard.Coordinates(x = 1, y = 2),
                    endPosition = GameBoard.Coordinates(x = 4, y = 7),
                    obstacles = listOf(
                        GameBoard.ObstacleArea(fromX = 3, toX = 5, fromY = 6, toY = 8)
                    )
                )
            }.message shouldBe "end position Coordinates(x=4, y=7) cannot be in an obstacle ObstacleArea(fromX=3, toX=5, fromY=6, toY=8)"
        }

        @ParameterizedTest
        @CsvSource(
            delimiterString = ";",
            value = [
                "0;11;1;1",
                "11;12;1;1",
                "1;1;11;12",
                "2;2;1;11",
            ]
        )
        fun `obstacles outside of area - should throw exception`(fromX: Int, toX: Int, fromY: Int, toY: Int) {
            shouldThrow<IllegalArgumentException> {
                GameBoard.Layout(
                    area = GameBoard.Area(width = 10, height = 10),
                    startPosition = GameBoard.Coordinates(x = 1, y = 2),
                    endPosition = GameBoard.Coordinates(x = 4, y = 7),
                    obstacles = listOf(
                        GameBoard.ObstacleArea(fromX = fromX, toX = toX, fromY = fromY, toY = toY)
                    )
                )
            }.message shouldBe "obstacle ObstacleArea(fromX=$fromX, toX=$toX, fromY=$fromY, toY=$toY) must be within the game-board area"
        }

        @ParameterizedTest
        @CsvSource(
            delimiterString = ";",
            value = [
                "0;0;FREE", "1;0;FREE", "2;0;FREE", "3;0;FREE", "4;0;FREE", "5;0;FREE", "6;0;FREE", "7;0;FREE", "8;0;FREE", "9;0;FREE",
                "0;1;FREE", "1;1;FREE", "2;1;FREE", "3;1;FREE", "4;1;FREE", "5;1;FREE", "6;1;FREE", "7;1;FREE", "8;1;FREE", "9;1;FREE",
                "0;2;FREE", "1;2;START", "2;2;FREE", "3;2;FREE", "4;2;FREE", "5;2;FREE", "6;2;FREE", "7;2;FREE", "8;2;FREE", "9;2;FREE",
                "0;3;FREE", "1;3;FREE", "2;3;FREE", "3;3;FREE", "4;3;FREE", "5;3;FREE", "6;3;FREE", "7;3;FREE", "8;3;FREE", "9;3;FREE",
                "0;4;FREE", "1;4;FREE", "2;4;FREE", "3;4;FREE", "4;4;FREE", "5;4;FREE", "6;4;FREE", "7;4;END", "8;4;FREE", "9;4;FREE",
                "0;5;FREE", "1;5;FREE", "2;5;FREE", "3;5;FREE", "4;5;FREE", "5;5;FREE", "6;5;FREE", "7;5;FREE", "8;5;FREE", "9;5;FREE",
                "0;6;FREE", "1;6;FREE", "2;6;FREE", "3;6;OBSTACLE", "4;6;OBSTACLE", "5;6;OBSTACLE", "6;6;FREE", "7;6;FREE", "8;6;FREE",
                "0;7;FREE", "1;7;FREE", "2;7;FREE", "3;7;OBSTACLE", "4;7;OBSTACLE", "5;7;OBSTACLE", "6;7;FREE", "7;7;FREE", "8;7;FREE", "9;7;FREE",
                "0;8;FREE", "1;8;FREE", "2;8;FREE", "3;8;OBSTACLE", "4;8;OBSTACLE", "5;8;OBSTACLE", "6;8;FREE", "7;8;FREE", "8;8;FREE", "9;8;FREE",
                "0;9;FREE", "1;9;FREE", "2;9;FREE", "3;9;FREE", "4;9;FREE", "5;9;FREE", "6;9;FREE", "7;9;FREE", "8;9;FREE", "9;9;FREE",
            ]
        )
        fun `get field type`(x: Int, y: Int, typeName: String) {
            val layout = GameBoard.Layout(
                area = GameBoard.Area(width = 10, height = 10),
                startPosition = GameBoard.Coordinates(x = 1, y = 2),
                endPosition = GameBoard.Coordinates(x = 7, y = 4),
                obstacles = listOf(
                    GameBoard.ObstacleArea(fromX = 3, toX = 5, fromY = 6, toY = 8)
                )
            )

            layout.getFieldType(GameBoard.Coordinates(x = x, y = y)) shouldBe GameBoard.Field.Type.valueOf(typeName)
        }


        @ParameterizedTest
        @CsvSource(
            delimiterString = ";",
            value = [
                "-1;0", "0;-1", "-1;-1", "11;0", "0;11", "11;11"
            ]
        )
        fun `get field typ - out of area - should be null`(x: Int, y: Int) {
            val layout = GameBoard.Layout(
                area = GameBoard.Area(width = 10, height = 10),
                startPosition = GameBoard.Coordinates(x = 1, y = 2),
                endPosition = GameBoard.Coordinates(x = 7, y = 4),
                obstacles = listOf(
                    GameBoard.ObstacleArea(fromX = 3, toX = 5, fromY = 6, toY = 8)
                )
            )

            layout.getFieldType(GameBoard.Coordinates(x = x, y = y)).shouldBeNull()
        }
    }

    @Nested
    inner class Area {

        @ParameterizedTest
        @ValueSource(ints = [-1, 0])
        fun `width has not allowed dimension - should throw error`(width: Int) {
            shouldThrow<IllegalArgumentException> {
                GameBoard.Area(width = width, height = 10)
            }.message shouldBe "game-board width must be greater than 0"
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, 0])
        fun `height has not allowed dimension - should throw error`(height: Int) {
            shouldThrow<IllegalArgumentException> {
                GameBoard.Area(width = 10, height = height)
            }.message shouldBe "game-board height must be greater than 0"
        }
    }

    @Nested
    inner class Coordinates {
        @ParameterizedTest
        @CsvSource(
            delimiterString = ";",
            value = [
                "0;0;2;1;2;1",
                "1;1;2;1;3;2",
                "10;10;2;1;12;11",
                "10;10;-2;-1;8;9",
                "10;10;-2;1;8;11",
                "10;10;2;-1;12;9",
            ]
        )
        fun `plus vector test`(cX: Int, cY: Int, jumpX: Int, jumpY: Int, resultX: Int, resultY: Int) {
            val result = GameBoard.Coordinates(x = cX, y = cY) + MoveVector(jumpX = jumpX, jumpY = jumpY)
            result shouldBe GameBoard.Coordinates(x = resultX, y = resultY)
        }
    }

    @Nested
    inner class ObstacleArea {
        @ParameterizedTest
        @CsvSource(
            delimiterString = ";",
            value = [
                "-1;0;0;0;x1",
                "0;-1;0;0;x2",
                "0;0;-1;0;y1",
                "0;0;0;-1;y2",
            ]
        )
        fun `creation - coordinate is negative - should throw exception`(
            fromX: Int,
            toX: Int,
            fromY: Int,
            toY: Int,
            failingCoord: String
        ) {
            shouldThrow<IllegalArgumentException> {
                GameBoard.ObstacleArea(fromX = fromX, toX = toX, fromY = fromY, toY = toY)
            }.message shouldBe "obstacle $failingCoord coordinate must be greater than or equal to 0"
        }

        @ParameterizedTest
        @CsvSource(
            delimiterString = ";",
            value = [
                "1;0;0;0;x",
                "0;0;1;0;y",
            ]
        )
        fun `creation - start coord is higher than end - should throw exception`(
            fromX: Int,
            toX: Int,
            fromY: Int,
            toY: Int,
            failingAxe: String
        ) {
            shouldThrow<IllegalArgumentException> {
                GameBoard.ObstacleArea(fromX = fromX, toX = toX, fromY = fromY, toY = toY)
            }.message shouldBe "coordinate ${failingAxe}1 must be less than or equal to coordinate ${failingAxe}2"

        }

        @Test
        fun `get all coordinates`() {
            val obstacle = GameBoard.ObstacleArea(fromX = 3, toX = 5, fromY = 6, toY = 8)
            obstacle.allCoordinates.shouldContainExactlyInAnyOrder(
                GameBoard.Coordinates(x = 3, y = 6),
                GameBoard.Coordinates(x = 3, y = 7),
                GameBoard.Coordinates(x = 3, y = 8),
                GameBoard.Coordinates(x = 4, y = 6),
                GameBoard.Coordinates(x = 4, y = 7),
                GameBoard.Coordinates(x = 4, y = 8),
                GameBoard.Coordinates(x = 5, y = 6),
                GameBoard.Coordinates(x = 5, y = 7),
                GameBoard.Coordinates(x = 5, y = 8),
            )
        }

        @Test
        fun `contains test - should contain`() {
            val obstacle = GameBoard.ObstacleArea(fromX = 3, toX = 5, fromY = 6, toY = 8)
            obstacle.contains(GameBoard.Coordinates(x = 4, y = 7)) shouldBe true
        }

        @Test
        fun `contains test - should not contain`() {
            val obstacle = GameBoard.ObstacleArea(fromX = 3, toX = 5, fromY = 6, toY = 8)
            obstacle.contains(GameBoard.Coordinates(x = 6, y = 7)) shouldBe false
        }
    }
}
