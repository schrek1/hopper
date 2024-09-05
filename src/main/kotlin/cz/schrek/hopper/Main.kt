package cz.schrek.hopper

import cz.schrek.hopper.adapter.input.console.ConsoleAdapter
import cz.schrek.hopper.application.port.HooperShortPathUseCase
import kotlinx.coroutines.runBlocking

object Main {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val consoleAdapter = ConsoleAdapter()
        val useCase = HooperShortPathUseCase.INSTANCE

        val gameLayouts = consoleAdapter.readGameLayout()

        val results = useCase.searchShortestPath(*gameLayouts.toTypedArray())

        consoleAdapter.printResults(results)
    }
}
