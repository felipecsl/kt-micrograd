// multi-layer perceptron
class MLP(nin: Int, nouts: List<Int>) {
  val sz = listOf(nin) + nouts
  val layers = nouts.indices.map { Layer(sz[it], sz[it + 1]) }

  operator fun invoke(x: List<Double>): List<Value> {
    var ret = x.map { Value(it) }
    for (layer in layers) {
      ret = layer(ret)
    }
    return ret
  }

  fun zeroGrad() {
    parameters().forEach { it.grad = 0.0 }
  }

  fun parameters(): List<Value> {
    return layers.flatMap(Layer::parameters)
  }
}
