package me.chriss99.parse

import me.chriss99.lambda.Expression
import java.util.LinkedList

private inline fun <reified T> verifyToken(token: Token): T {
    if (token !is T)
        throw IllegalArgumentException("Unexpected Token! Expected ${T::class.simpleName}, got: $token")
    return token
}

fun parse(tokens: LinkedList<Token>): Expression {
    when (val current = tokens.pop()) {
        is Token.Var -> return Expression.Var(current.name)
        is Token.Lambda -> {
            val variable = verifyToken<Token.Var>(tokens.pop())
            verifyToken<Token.Dot>(tokens.pop())

            return Expression.Lambda(Expression.Var(variable.name), parse(tokens))
        }
        is Token.LParen ->
            when (tokens.peek()) {
                is Token.Lambda -> {
                    tokens.pop()
                    val variable = verifyToken<Token.Var>(tokens.pop())
                    verifyToken<Token.Dot>(tokens.pop())
                    val expr = parse(tokens)
                    val lambda = Expression.Lambda(Expression.Var(variable.name), expr)
                    if (tokens.peek() is Token.RParen){
                        tokens.pop()
                        return lambda
                    }
                    val apply = Expression.Apply(lambda, parse(tokens))
                    verifyToken<Token.RParen>(tokens.pop())
                    return apply
                }
                else -> {
                    val expr = Expression.Apply(parse(tokens), parse(tokens))
                    verifyToken<Token.RParen>(tokens.pop())
                    return expr
                }
            }
        else -> throw IllegalArgumentException("Unexpected Token! Expected Var, Lambda or LParen, got: $current")
    }
}