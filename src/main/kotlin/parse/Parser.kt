package me.chriss99.parse

import me.chriss99.lambda.Expression
import java.util.HashMap
import java.util.UUID

private inline fun <reified T> verifyToken(token: Token): T {
    if (token !is T)
        throw IllegalArgumentException("Unexpected Token! Expected ${T::class.simpleName}, got: $token")
    return token
}


fun parse(tokens: List<Token>): Expression {
    return parse(tokens, 0, HashMap()).first
}

fun parse(tokens: List<Token>, i: Int, ids: HashMap<String, UUID>): Pair<Expression, Int> {
    when (val current = tokens[i]) {
        is Token.Var -> return Expression.Var(current.name, idOf(current.name, ids)) to i
        is Token.Lambda -> {
            val variable = verifyToken<Token.Var>(tokens[i+1])
            val varExpr = Expression.Var(variable.name, newID(variable.name, ids))
            verifyToken<Token.Dot>(tokens[i+2])

            val (body, i) = parse(tokens, i+3, ids)
            return Expression.Lambda(varExpr, body) to i
        }
        is Token.LParen -> {
            if (tokens[i+1] is Token.Lambda) {
                val variable = verifyToken<Token.Var>(tokens[i+2])
                val varExpr = Expression.Var(variable.name, newID(variable.name, ids))
                verifyToken<Token.Dot>(tokens[i+3])
                val (body, i) = parse(tokens, i+4, ids)

                val lambda = Expression.Lambda(varExpr, body)
                if (tokens[i+1] is Token.RParen)
                    return lambda to i+1


                val (expr, next) = parse(tokens, i+1, ids)

                val apply = Expression.Apply(lambda, expr)
                verifyToken<Token.RParen>(tokens[next+1])
                return apply to next+1
            }

            val (apply, i) = parse(tokens, i+1, ids)
            val (to, next) = parse(tokens, i+1, ids)
            verifyToken<Token.RParen>(tokens[next+1])

            return Expression.Apply(apply, to) to next+1
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