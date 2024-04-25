import kotlin.random.Random

// nin - number of inputs
class Neuron(nin: Int) {
  // weights
  val w = (0..<nin).map { Value(Random.nextDouble(-1.0, 1.0)) }
  // bias
  val b = Value(Random.nextDouble(-1.0, 1.0))

  operator fun invoke(x: List<Value>): Value {
    // activation
    val act = w.zip(x)
      .map { (wi, xi) -> wi * xi }
      .reduce { acc, v -> acc + v } + b
    return act.tanh()
  }
}

// nin - number of inputs
// nout - number of outputs
class Layer(nin: Int, nouts: Int) {
  val neurons = (0..<nouts).map { Neuron(nin) }

  operator fun invoke(x: List<Value>): List<Value> {
    return neurons.map { n -> n(x) }
  }
}

// multi-layer perceptron
class MLP(nin: Int, nouts: List<Int>) {
  val sz = listOf(nin) + nouts
  val layers = nouts.indices.map { Layer(sz[it], sz[it+1]) }

  operator fun invoke(x: List<Double>): List<Value> {
    var ret = x.map { Value(it) }
    for (layer in layers) {
      ret = layer(ret)
    }
    return ret
  }
}
