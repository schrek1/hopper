package cz.schrek.hopper.application.domain.usecase

import cz.schrek.hopper.application.domain.model.GameBoard
import cz.schrek.hopper.application.domain.model.GameTurnSnapshot
import cz.schrek.hopper.application.domain.model.Hooper
import cz.schrek.hopper.application.domain.model.SearchPathStack
import cz.schrek.hopper.application.domain.service.HopperJumpsCalculator
import cz.schrek.hopper.application.port.`in`.GameRequest
import cz.schrek.hopper.application.port.`in`.HooperShortPathUseCase
import cz.schrek.hopper.application.port.`in`.MovementAbilityController
import cz.schrek.hopper.application.port.`in`.SearchHooperShortestPathResult
import cz.schrek.hopper.utils.CoroutineUtils
import cz.schrek.hopper.utils.Logger.getLoggerForClass
import cz.schrek.hopper.utils.createHooperContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class HooperShortPathUseCaseImpl : HooperShortPathUseCase {

    private val logger = getLoggerForClass()

    override suspend fun searchShortestPath(
        vararg gameRequests: GameRequest
    ): Map<GameRequest, SearchHooperShortestPathResult> = coroutineScope {
        gameRequests.mapIndexed { idx, request ->
            async(context = createHooperContext(idx)) {
                request to performPathSearching(
                    gameLayout = request.gameBoardLayout,
                    initialMovementAbility = request.movementAbility
                ) { _, _ -> request.movementAbility }
            }
        }.awaitAll().toMap()
    }

    override suspend fun searchShortestPathInteractive(
        gameLayout: GameBoard.Layout,
        movementAbilityController: MovementAbilityController
    ): SearchHooperShortestPathResult = performPathSearching(
        gameLayout = gameLayout,
        initialMovementAbility = Hooper.MovementAbility.NOT_MOVING,
        movementAbilityController = movementAbilityController
    )

    private suspend fun performPathSearching(
        gameLayout: GameBoard.Layout,
        initialMovementAbility: Hooper.MovementAbility,
        movementAbilityController: MovementAbilityController
    ): SearchHooperShortestPathResult = coroutineScope {
        var paths = initPaths(gameLayout, initialMovementAbility)

        var solution = tryGetSolution(paths)

        while (solution == null) {
            paths = goDeeper(
                gameBoardLayout = gameLayout,
                paths = paths,
                movementAbilityController = movementAbilityController
            )
            solution = tryGetSolution(paths)
        }

        solution
    }

    private suspend fun goDeeper(
        gameBoardLayout: GameBoard.Layout,
        paths: List<SearchPathStack>,
        movementAbilityController: MovementAbilityController
    ): List<SearchPathStack> = coroutineScope {
        val lastPathItem = paths.first().getLast()

        val currentDeep = lastPathItem.order
        val currentMovementAbility = lastPathItem.hopperMovementAbility

        logger.info("Going deeper (pId=${coroutineContext[CoroutineUtils.ProcessIdKey]?.id}) - current deep: $currentDeep \tpaths: ${paths.size} \ttotalItemsInStacks: ${paths.sumOf { it.getFullStack().size }}")

        val requestedMovementAbility = movementAbilityController.getMovementAbility(
            turn = currentDeep + 1,
            currentMoveAbility = currentMovementAbility
        )

        paths.asSequence()
            .filter { it.getLast().hopperPosition.type != GameBoard.Field.Type.OBSTACLE && !it.hasAlreadyVisitedInStack() }
            .map { currentPathStack ->
                async(Dispatchers.Default) {
                    val lastTurn = currentPathStack.getLast()

                    val nextTurns = HopperJumpsCalculator.resolveJumpableFields(
                        gameBoardLayout = gameBoardLayout,
                        hopperActualPosition = lastTurn.hopperPosition.position,
                        movementAbility = requestedMovementAbility
                    )

                    val currentOrder = lastTurn.order + 1

                    nextTurns.map { nextTurn ->
                        val currentTurnSnapshot = GameTurnSnapshot(
                            order = currentOrder,
                            hopperPosition = nextTurn,
                            hopperMovementAbility = requestedMovementAbility
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

    private fun initPaths(
        gameLayout: GameBoard.Layout,
        movementAbility: Hooper.MovementAbility
    ): List<SearchPathStack> {
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
            hopperMovementAbility = movementAbility
        )

        return listOf(SearchPathStack.of(initialTurn))
    }

    companion object {
        const val MAX_DEEP = 17 // more than 17 hops is not possible to solve...
    }
}
