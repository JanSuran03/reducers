package reducers

interface Reducer<TIntermediate, TInput, TRet> {
    fun init(): TIntermediate
    fun finalize(result: TIntermediate): TRet
    fun reduce1(result: TIntermediate, input: TInput): TIntermediate
}

interface Transducer<TIn, TMid> {
    fun <TOut> compose(xf2: Transducer<TMid, TOut>): Transducer<TIn, TOut>

    fun <Tntermediate, TInput, TRet> build(reducer: Reducer<Tntermediate, TInput, TRet>)
    : Reducer<>
}

fun <T> filter(pred: (x: T) -> Boolean): Any {
    return object : Transducer<T, T> {
        override fun <Out> compose(xf2: Transducer<T, Out>): Transducer<T, Out> {
            TODO("Not yet implemented")
        }

        override fun <TIntermediate, T2, TRet> build(reducer: Reducer<TIntermediate, T2, TRet>): Reducer<TIntermediate, T2, TRet> {
            return object : Reducer<TIntermediate, T2, TRet> {
                override fun init(): TIntermediate {
                    return reducer.init()
                }

                @Suppress("UNCHECKED_CAST")
                override fun reduce1(
                    result: TIntermediate,
                    input: T2, /* T = T2, ale potrebuju oba typove parametry... */
                ): TIntermediate {
                    @Suppress("UNCHECKED_CAST") // yep, tady to vadi
                    // idealne tam mit 2 "ruzne" typove parametry, ale type checkovat, ze se rovnaji...
                    return if (pred(input as T)) {
                        reducer.reduce1(result, input as T2)
                    } else {
                        result
                    }
                }

                override fun finalize(result: TIntermediate): TRet {
                    return reducer.finalize(result)
                }
            }
        }
    }
}

val intInserter = object : Reducer<MutableSet<Int>, Int, Set<Int>> {
    override fun init(): MutableSet<Int> {
        return mutableSetOf()
    }

    override fun reduce1(result: MutableSet<Int>, input: Int): MutableSet<Int> {
        result.add(input)
        return result
    }

    override fun finalize(result: MutableSet<Int>): Set<Int> {
        return result.toSet()
    }
}


fun main() {

}
