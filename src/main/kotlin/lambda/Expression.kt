package me.chriss99.lambda

import java.util.UUID

sealed class Expression {
    class Var(val name: String, val id: UUID) : Expression()
    class Lambda(val variable: Var, val body: Expression) : Expression()
    class Apply(val apply: Expression, val to: Expression) : Expression()

    override fun toString(): String {
        return when (this) {
            is Var -> name
            is Lambda -> "\\$variable.$body"
            is Apply -> "(${if (apply is Lambda) "($apply)" else apply.toString()}$to)"
        }
    }
}