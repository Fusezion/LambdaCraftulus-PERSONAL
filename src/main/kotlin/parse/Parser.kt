package me.chriss99.parse

import me.chriss99.lambda.Expression
import java.util.HashMap
import java.util.LinkedList
import java.util.UUID

private inline fun <reified T> verifyToken(token: Token): T {
    if (token !is T)
        throw IllegalArgumentException("Unexpected Token! Expected ${T::class.simpleName}, got: $token")
    return token
}

fun parse(tokens: LinkedList<Token>, ids: HashMap<String, UUID> = HashMap()): Expression {
    when (val current = tokens.pop()) {
        is Token.Var -> return Expression.Var(current.name, idOf(current.name, ids))
        is Token.Lambda -> {
            val variable = verifyToken<Token.Var>(tokens.pop())
            verifyToken<Token.Dot>(tokens.pop())

            return Expression.Lambda(Expression.Var(variable.name, newID(variable.name, ids)), parse(tokens, ids))
        }
        is Token.LParen ->
            when (tokens.peek()) {
                is Token.Lambda -> {
                    tokens.pop()
                    val variable = verifyToken<Token.Var>(tokens.pop())
                    verifyToken<Token.Dot>(tokens.pop())
                    val lambda = Expression.Lambda(Expression.Var(variable.name, newID(variable.name, ids)), parse(tokens, ids))
                    if (tokens.peek() is Token.RParen){
                        tokens.pop()
                        return lambda
                    }
                    val apply = Expression.Apply(lambda, parse(tokens, ids))
                    verifyToken<Token.RParen>(tokens.pop())
                    return apply
                }
                else -> {
                    val expr = Expression.Apply(parse(tokens, ids), parse(tokens, ids))
                    verifyToken<Token.RParen>(tokens.pop())
                    return expr
                }
            }
        else -> throw IllegalArgumentException("Unexpected Token! Expected Var, Lambda or LParen, got: $current")
    }
}

fun <T> newID(name: T, ids: HashMap<T, UUID>): UUID {
    if (ids.containsKey(name))
        throw IllegalStateException("Non unique arguments! $name already exists, but found Lambda defining it as its variable!")
    val id = UUID.randomUUID()
    ids[name] = id
    return id
}

fun <T> idOf(name: T, ids: HashMap<T, UUID>): UUID {
    return ids[name] ?: UUID.randomUUID().also { ids[name] = it }
}