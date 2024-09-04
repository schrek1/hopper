package cz.schrek.hopper.application.port

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.GameTurnSnapshot

interface HooperShortPathUseCase {

    suspend fun searchShortestPath(vararg gameLayouts: GameBoard.Layout): Map<GameBoard.Layout, SearchHooperShortestPathResult>
}

sealed class SearchHooperShortestPathResult {
    class Success(val hops: List<GameTurnSnapshot>) : SearchHooperShortestPathResult()
    class NotFound(val reason: String) : SearchHooperShortestPathResult()
}
