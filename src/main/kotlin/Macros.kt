package me.chriss99

import me.chriss99.lambda.Expression
import java.util.UUID

fun namedVar(name: String): Expression.Var {
    return Expression.Var(name, UUID.randomUUID())
}

fun idExpr(): Expression {
    val a = namedVar("a")
    return Expression.Lambda(a ,a)
}

fun trueExpr(): Expression {
    val x = namedVar("x")
    val y = namedVar("y")
    return Expression.Lambda(x, Expression.Lambda(y, x))
}

fun falseExpr(): Expression {
    val x = namedVar("x")
    val y = namedVar("y")
    return Expression.Lambda(x, Expression.Lambda(y, y))
}

fun notExpr(): Expression {
    val b = namedVar("b")
    return Expression.Lambda(b, Expression.Apply(Expression.Apply(b, falseExpr()), trueExpr()))
}

fun numberExpr(num: Int): Expression {
    val f = namedVar("f")
    val v = namedVar("v")
    var expr: Expression = v

    for (i in 1..num)
        expr = Expression.Apply(f, expr)

    return Expression.Lambda(f, Expression.Lambda(v, expr))
}

fun succExpr(): Expression {
    val n = namedVar("n")
    val f = namedVar("f")
    val v = namedVar("v")

    return Expression.Lambda(n, Expression.Lambda(f, Expression.Lambda(v, Expression.Apply(f, Expression.Apply(Expression.Apply(n, f), v)))))
}

fun addExpr(): Expression {
    val m = namedVar("m")
    val n = namedVar("n")
    val f = namedVar("f")
    val v = namedVar("v")

    return Expression.Lambda(m, Expression.Lambda(n, Expression.Lambda(f, Expression.Lambda(v, Expression.Apply(Expression.Apply(m, f), Expression.Apply(Expression.Apply(n, f), v))))))
}