package cz.schrek.hopper.application.domain.model

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class VelocityTest {


    @ParameterizedTest
    @CsvSource(
        delimiterString = ";",
        value = [
            "0;0;0;0",
            "3;3;3;3",
            "-3;-3;-3;-3",
            "4;4;3;3",
            "-4;-4;-3;-3",
            "4;-4;3;-3",
            "-4;4;-3;3",
            "1;1;1;1",
        ]
    )
    fun `input should match expected moves`(inputX: Int, inputY: Int, expectedX: Int, expectedY: Int) {
        HopperMovementAbility.of(
            jumpSizeX = inputX,
            jumpSizeY = inputY
        ).getAllMoves() shouldContainExactlyInAnyOrder setOf(
            MoveVector(jumpX = expectedX, jumpY = expectedY),
            MoveVector(jumpX = -expectedX, jumpY = expectedY),
            MoveVector(jumpX = expectedX, jumpY = -expectedY),
            MoveVector(jumpX = -expectedX, jumpY = -expectedY),
            MoveVector(jumpX = expectedY, jumpY = expectedX),
            MoveVector(jumpX = -expectedY, jumpY = expectedX),
            MoveVector(jumpX = expectedY, jumpY = -expectedX),
            MoveVector(jumpX = -expectedY, jumpY = -expectedX)
        )
    }
}
