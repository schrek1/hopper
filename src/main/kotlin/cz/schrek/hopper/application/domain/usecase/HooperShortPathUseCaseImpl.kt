package cz.schrek.hopper.application.domain.usecase

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.GameTurnSnapshot
import cz.schrek.hopper.application.domain.model.Hooper
import cz.schrek.hopper.application.domain.model.SearchPathStack
import cz.schrek.hopper.application.domain.service.HopperJumpsCalculator
import cz.schrek.hopper.application.port.HooperShortPathUseCase
import cz.schrek.hopper.application.port.SearchHooperShortestPathResult
import cz.schrek.hopper.utils.CoroutineUtils
import cz.schrek.hopper.utils.Logger.getLoggerForClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class HooperShortPathUseCaseImpl : HooperShortPathUseCase {

    private val logger = getLoggerForClass()

    override suspend fun searchShortestPath(
        vararg gameLayouts: GameBoard.Layout
    ): Map<GameBoard.Layout, SearchHooperShortestPathResult> = coroutineScope {
        gameLayouts.mapIndexed { idx, layout ->
            async(context = coroutineContext + CoroutineUtils.ProcessIdElement(idx)) {
                layout to searchShortestPath(layout)
            }
        }.awaitAll().toMap()
    }

    private suspend fun searchShortestPath(
        gameLayout: GameBoard.Layout
    ): SearchHooperShortestPathResult = coroutineScope {
        var paths = initPaths(gameLayout)

        var solution = tryGetSolution(paths)

        while (solution == null) {
            paths = goDeeper(gameLayout, paths)
            solution = tryGetSolution(paths)
        }

        solution
    }

    private suspend fun goDeeper(
        gameBoardLayout: GameBoard.Layout,
        paths: List<SearchPathStack>
    ): List<SearchPathStack> = coroutineScope {
        val currentDeep = paths.first().getLast().order
        logger.info("Going deeper (pId=${coroutineContext[CoroutineUtils.ProcessIdKey]?.id}) - current deep: $currentDeep \tpaths: ${paths.size} \ttotalItemsInStacks: ${paths.sumOf { it.getFullStack().size }}")

        paths.asSequence()
            .filter { it.getLast().hopperPosition.type != GameBoard.Field.Type.OBSTACLE && !it.hasAlreadyVisitedInStack() }
            .map { currentPathStack ->
                async(Dispatchers.Default) {
                    val lastTurn = currentPathStack.getLast()

                    val nextTurns = HopperJumpsCalculator.resolveJumpableFields(
                        gameBoardLayout = gameBoardLayout,
                        hopperActualPosition = lastTurn.hopperPosition.position,
                        movementAbility = lastTurn.hopperMovementAbility
                    )

                    val currentOrder = lastTurn.order + 1

                    nextTurns.map { nextTurn ->
                        val currentTurnSnapshot = GameTurnSnapshot(
                            order = currentOrder,
                            hopperPosition = nextTurn,
                            hopperMovementAbility = DEFAULT_VELOCITY
                        )

                        currentPathStack.copy().apply { push(currentTurnSnapshot) }
                    }
                }
            }.toList().awaitAll().flatten()
    }

    private fun tryGetSolution(paths: List<SearchPathStack>): SearchHooperShortestPathResult? {
        if (paths.isEmpty()) return SearchHooperShortestPathResult.NotFound("No path found")

        val solution = paths.firstOrNull { it.getLast().hopperPosition.type == GameBoard.Field.Type.END }
        if (solution != null) return SearchHooperShortestPathResult.Success(solution.getFullStack())

        val deep = paths.first().getLast().order
        if (deep >= MAX_DEEP) {
            return SearchHooperShortestPathResult.NotFound("Max hops reached")
        }

        val allPathIsBlocked = paths.all { it.getLast().hopperPosition.type == GameBoard.Field.Type.OBSTACLE }
        if (allPathIsBlocked) return SearchHooperShortestPathResult.NotFound("All paths are blocked")

        return null
    }

    private fun initPaths(gameLayout: GameBoard.Layout): List<SearchPathStack> {
        val initialTurn = GameTurnSnapshot(
            order = 0,
            hopperPosition = GameBoard.Field(
                position = gameLayout.startPosition,
                type = when {
                    gameLayout.startPosition == gameLayout.endPosition -> GameBoard.Field.Type.END
                    else -> gameLayout.getFieldType(gameLayout.startPosition)
                        ?: error("start position is not in the game-board area")
                }
            ),
            hopperMovementAbility = DEFAULT_VELOCITY
        )

        return listOf(SearchPathStack.of(initialTurn))
    }

    companion object {
        val DEFAULT_VELOCITY = Hooper.MovementAbility.of(2, 1)
        const val MAX_DEEP = 17 // more than 17 hops is not possible to solve...
    }
}
