package reducers

import kotlin.math.PI

internal interface IFinalize<INTERMEDIATE, RET> {
    fun finalize(result: INTERMEDIATE): RET
}

internal interface IReduce1<INTERMEDIATE, INPUT> {
    fun reduce1(result: INTERMEDIATE, input: INPUT): INTERMEDIATE
}

internal interface IReduceInit<INTERMEDIATE, INPUT>
    : IReduce1<INTERMEDIATE, INPUT> {
    fun reduce(init: INTERMEDIATE, coll: Iterable<INPUT>): INTERMEDIATE {
        return reduce(init, coll.iterator())
    }

    fun reduce(init: INTERMEDIATE, iterator: Iterator<INPUT>): INTERMEDIATE {
        var ret = init

        while (iterator.hasNext()) {
            ret = reduce1(ret, iterator.next())
        }

        return ret
    }
}

internal interface IReduce<T>
    : IReduceInit<T, T> {
    fun reduce(coll: Iterable<T>): T {
        return reduce(coll.iterator())
    }

    fun reduce(iterator: Iterator<T>): T {
        val x = if (iterator.hasNext()) {
            iterator.next()
        } else {
            throw NoSuchElementException("Cannot reduce-init with <2 item coll, 0 provided")
        }
        val y = if (iterator.hasNext()) {
            iterator.next()
        } else {
            throw NoSuchElementException("Cannot reduce-init with <2 coll, 1 provided")
        }

        return reduce(reduce1(x, y), iterator)
    }
}

internal interface ITransduceInit<INTERMEDIATE, INPUT, RET>
    : IReduceInit<INTERMEDIATE, INPUT>,
    IFinalize<INTERMEDIATE, RET> {
    fun transduce(init: INTERMEDIATE, coll: Iterator<INPUT>): RET {
        return finalize(super.reduce(init, coll))
    }

    fun transduce(init: INTERMEDIATE, coll: Iterable<INPUT>): RET {
        return transduce(init, coll.iterator())
    }
}

internal interface ITransduce<T, RET>
    : ITransduceInit<T, T, RET> {
    fun transduce(coll: Iterable<T>): RET {
        return transduce(coll.iterator())
    }

    fun transduce(iterator: Iterator<T>): RET {
        val x = if (iterator.hasNext()) {
            iterator.next()
        } else {
            throw NoSuchElementException("Cannot reduce-init with <2 item coll, 0 provided")
        }
        val y = if (iterator.hasNext()) {
            iterator.next()
        } else {
            throw NoSuchElementException("Cannot reduce-init with <2 coll, 1 provided")
        }

        return transduce(reduce1(x, y), iterator)
    }
}

object AdderReducer : IReduce<Int> {
    override fun reduce1(result: Int, input: Int): Int {
        return result + input
    }
}

object PlusPiAndDoubler : ITransduce<Int, Double> {
    override fun finalize(result: Int): Double {
        return (result + PI) * 2
    }

    override fun reduce1(result: Int, input: Int): Int {
        return result + input
    }

}

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val range = IntRange(1, 4)
        println("Sum of numbers 1..4 is ${AdderReducer.reduce(range)}")

        println("Sum of numbers 1..4 + PI and all times 2 = ${PlusPiAndDoubler.transduce(range)}")

        println("Sum of numbers 1..4 with init 7 is ${AdderReducer.reduce(7, range)}")

        println("Sum of numbers 1..4 with init 7, + PI and * 2 = ${PlusPiAndDoubler.transduce(7, range)}")


        val emptyRange = IntRange.EMPTY
        println("Sum of empty range is ${AdderReducer.reduce(0, emptyRange)}")

        println("Sum of empty range + PI and times 2 is ${PlusPiAndDoubler.transduce(0, emptyRange)}")
    }
}
