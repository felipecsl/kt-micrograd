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

  fun parameters(): List<Value> {
    return w + listOf(b)
  }
}
