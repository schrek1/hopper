package cz.schrek.hopper

import cz.schrek.hopper.adapter.input.console.ConsoleAdapter
import cz.schrek.hopper.adapter.input.console.dto.GameType.AUTO
import cz.schrek.hopper.adapter.input.console.dto.GameType.INTERACTIVE
import cz.schrek.hopper.application.port.`in`.HooperShortPathUseCase
import kotlinx.coroutines.runBlocking

object Main {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val consoleAdapter = ConsoleAdapter()
        val useCase = HooperShortPathUseCase.INSTANCE

        val gameType = consoleAdapter.readGameType()

        when (gameType) {
            INTERACTIVE -> {
                val gamBoardLayout = consoleAdapter.readInteractiveGameLayout()
                val result = useCase.searchShortestPathInteractive(
                    gameLayout = gamBoardLayout,
                    movementAbilityController = consoleAdapter::readMovementAbility
                )
                consoleAdapter.printResult(gamBoardLayout, result)
            }

            AUTO -> useCase.searchShortestPath(*consoleAdapter.readAutoGameRequests().toTypedArray()).forEach {
                consoleAdapter.printResult(it.key.gameBoardLayout, it.value)
            }
        }
    }
}
