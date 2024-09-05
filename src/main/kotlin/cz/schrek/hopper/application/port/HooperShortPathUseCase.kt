package cz.schrek.hopper.application.port

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.GameTurnSnapshot
import cz.schrek.hopper.application.domain.usecase.HooperShortPathUseCaseImpl

interface HooperShortPathUseCase {

    companion object {
        val INSTANCE: HooperShortPathUseCase = HooperShortPathUseCaseImpl()
    }

    suspend fun searchShortestPath(vararg gameLayouts: GameBoard.Layout): Map<GameBoard.Layout, SearchHooperShortestPathResult>
}

sealed class SearchHooperShortestPathResult {
    data class Success(val hops: List<GameTurnSnapshot>) : SearchHooperShortestPathResult()
    data class NotFound(val reason: String) : SearchHooperShortestPathResult()
}
