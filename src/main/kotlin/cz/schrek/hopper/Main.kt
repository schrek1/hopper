package cz.schrek.hopper

import cz.schrek.hopper.adapter.input.console.ConsoleAdapter
import cz.schrek.hopper.adapter.input.console.dto.GameType
import cz.schrek.hopper.application.port.`in`.HooperShortPathUseCase
import kotlinx.coroutines.runBlocking

object Main {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val consoleAdapter = ConsoleAdapter()
        val useCase = HooperShortPathUseCase.INSTANCE

        val gameTyp = consoleAdapter.readGameType()

        when (gameTyp) {
            GameType.INTERACTIVE -> {
                val gamBoardLayout = consoleAdapter.readInteractiveGame()
                val result = useCase.searchShortestPathInteractive(
                    gameLayout = gamBoardLayout,
                    movementAbilityController = consoleAdapter::readMovementAbility
                )
                consoleAdapter.printResult(gamBoardLayout, result)
            }

            GameType.AUTO -> useCase.searchShortestPath(*consoleAdapter.readGameRequests().toTypedArray()).forEach {
                consoleAdapter.printResult(it.key.gameBoardLayout, it.value)
            }
        }
    }
}
