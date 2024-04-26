// nin - number of inputs
// nout - number of outputs
class Layer(nin: Int, nouts: Int) {
  val neurons = (0..<nouts).map { Neuron(nin) }

  operator fun invoke(x: List<Value>): List<Value> {
    return neurons.map { n -> n(x) }
  }

  fun parameters(): List<Value> {
    return neurons.flatMap(Neuron::parameters)
  }
}
