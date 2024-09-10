package cz.schrek.hopper.application.port.`in`

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.GameTurnSnapshot
import cz.schrek.hopper.application.domain.model.Hooper
import cz.schrek.hopper.application.domain.usecase.HooperShortPathUseCaseImpl

interface HooperShortPathUseCase {

    companion object {
        val INSTANCE: HooperShortPathUseCase = HooperShortPathUseCaseImpl()
    }

    suspend fun searchShortestPath(vararg gameRequests: GameRequest): Map<GameRequest, SearchHooperShortestPathResult>

    suspend fun searchShortestPathInteractive(
        gameLayout: GameBoard.Layout,
        movementAbilityController: MovementAbilityController
    ): SearchHooperShortestPathResult
}

data class GameRequest(
    val gameBoardLayout: GameBoard.Layout,
    val movementAbility: Hooper.MovementAbility
)

sealed class SearchHooperShortestPathResult {
    data class Success(val hops: List<GameTurnSnapshot>) : SearchHooperShortestPathResult()
    data class NotFound(val reason: String) : SearchHooperShortestPathResult()
}

fun interface MovementAbilityController {
    fun getMovementAbility(turn: Int, currentMoveAbility: Hooper.MovementAbility): Hooper.MovementAbility
}
