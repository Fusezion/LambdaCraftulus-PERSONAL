package me.chriss99.parse

fun lex(source: String): List<Token> {
    return source.replace(" ", "").map { stringToToken(it.toString()) ?: throw IllegalArgumentException("Unknown character: \"$it\"") }
}

private val tokenStringPairs = listOf(
    Token.Lambda to "\\",
    Token.Dot to ".",
    Token.LParen to "(",
    Token.RParen to ")",
    Token.EndOfFile to ""
)

private val charToTokenMap = tokenStringPairs.associate { it.second to it.first }
private val tokenToCharMap = tokenStringPairs.associate { it.first to it.second }

val stringToToken = { string: String ->
    if (string.fold(true) { a, b -> a && b.isLetter() })
        Token.Var(string)
    else
        charToTokenMap[string]
}
val tokenToString = { token: Token ->
    if (token is Token.Var)
        token.name
    else
        tokenToCharMap[token]
}