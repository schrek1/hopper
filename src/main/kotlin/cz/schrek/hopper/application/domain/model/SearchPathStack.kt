package cz.schrek.hopper.application.domain.model

data class SearchPathStack(
    private var stack: List<GameTurnSnapshot> = listOf(),
) {
    companion object {
        fun of(vararg snapshot: GameTurnSnapshot) = SearchPathStack().apply { stack += snapshot }
    }

    fun push(snapshot: GameTurnSnapshot) {
        stack += snapshot
    }

    fun getLast() = stack.last()

    fun getFullStack() = stack.toList()

    fun hasAlreadyVisitedInStack(): Boolean {
        val seenPositions = HashSet<GameBoard.Coordinates>()
        for (snapshot in stack) {
            if (!seenPositions.add(snapshot.hopperPosition.position)) return true
        }
        return false
    }
}
