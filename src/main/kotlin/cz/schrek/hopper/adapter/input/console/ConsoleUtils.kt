package cz.schrek.hopper.adapter.input.console

object ConsoleUtils {

    fun clearConsole() {
        print("\u001B[H\u001B[2J")
        System.out.flush()
    }

    fun printLnError(message: String) {
        System.err.println(message)
    }
}
