import kotlin.test.Test

class OptimizationTest {
  @Test fun `optimization test`() {
    val model = MLP(3, listOf(4, 4, 1))
    val inputs = listOf(
      listOf(2.0, 3.0, -1.0),
      listOf(3.0, -1.0, 0.5),
      listOf(0.5, 1.0, -1.0),
      listOf(1.0, 1.0, -1.0),
    )
    // desired targets
    val ys = listOf(1.0, -1.0, -1.0, 1.0)
    println("number of parameters: " + model.parameters().size)

    fun loss(): Pair<Value, List<Value>> {
      // Assuming `model` is a function that takes a list of `Value` and returns a `Value`
      val scores = inputs.flatMap { model(it) }
      val loss = ys.zip(scores)
        .map { (ygt, yout) -> (yout - ygt).pow(2.0) }
        .reduce { acc, value -> acc + value }
      return loss to scores
    }

    // gradient descent
    val maxSteps = 1000
    for (k in 0.rangeTo(maxSteps)) {
      // forward
      val (totalLoss, predictions) = loss()
      // backward
      model.zeroGrad()
      totalLoss.backward()
      // update(sgd)
      val learningRate = 0.01//1.0 - 0.9 * k / 100
      for (p in model.parameters()) {
        p.data -= learningRate * p.grad
      }
      println("step $k loss ${totalLoss.data}")
      if (k == maxSteps - 1) {
        println("predictions: ${predictions.map { it.data }}")
      }
    }
  }
}
