import kotlin.random.Random

// nin - number of inputs
fun Neuron(nin: Int): (List<Value>) -> Value {
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
fun Layer(nin: Int, nouts: Int): (List<Value>) -> List<Value> {
  val neurons = (0..<nouts).map { Neuron(nin) }
  return { x ->
    neurons.map { n -> n(x) }
  }
}

// multi-layer perceptron
fun MLP(nin: Int, nouts: List<Int>): (List<Double>) -> List<Value> {
  val sz = listOf(nin) + nouts
  val layers = nouts.indices.map { Layer(sz[it], sz[it+1]) }
  return { x ->
    var ret = x.map { Value(it) }
    for (layer in layers) {
      ret = layer(ret)
    }
    ret
  }
}
