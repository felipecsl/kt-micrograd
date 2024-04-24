import kotlin.random.Random

// nin - number of inputs
fun Neuron(nin: Int): (List<Double>) -> Value {
  // weights
  val w = (0..<nin).map { Value(Random.nextDouble(-1.0, 1.0)) }
  // bias
  val b = Value(Random.nextDouble(-1.0, 1.0))

  return { x ->
    // activation
    val act = w.zip(x)
      .map { (wi, xi) -> wi * xi }
      .reduce { acc, v -> acc + v } + b
    act.tanh()
  }
}

// nin - number of inputs
// nout - number of outputs
fun Layer(nin: Int, nout: Int): (List<Double>) -> List<Value> {
  val neurons = (0..<nout).map { Neuron(nin) }
  return { x ->
    neurons.map { n -> n(x) }
  }
}
