package cz.schrek.hopper.application.domain.usecase

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.port.HooperShortPathUseCase
import cz.schrek.hopper.application.domain.model.HopperMovementAbility
import cz.schrek.hopper.application.port.SearchHooperShortestPathResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class HooperShortPathUseCaseImpl : HooperShortPathUseCase {

    override suspend fun searchShortestPath(
        vararg gameLayouts: GameBoard.Layout
    ): Map<GameBoard.Layout, SearchHooperShortestPathResult> = coroutineScope {
        gameLayouts.map { async { it to searchShortestPath(it) } }.awaitAll().toMap()
    }

    private fun searchShortestPath(gameLayout: GameBoard.Layout): SearchHooperShortestPathResult {
        return SearchHooperShortestPathResult.NotFound("")
    }

    companion object {
        val DEFAULT_VELOCITY = HopperMovementAbility.of(2, 3)
    }
}
