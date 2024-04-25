import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class NeuralNetTest {
  @Test
  fun `test neuron`() {
    val n = Neuron(2)
    val act = n(listOf(Value(2.0), Value(3.0)))
    val data = act.data
    assertThat(data).isNotEqualTo(0.0)
  }

  @Test
  fun `test layer`() {
    val layer = Layer(2, 3)
    val out = layer(listOf(Value(2.0), Value(3.0)))
    val outData = out.map { it.data }
    assertThat((outData.size)).isEqualTo(3)
    assertThat((outData.all { it != 0.0 })).isTrue()
  }

  @Test
  fun `test mlp`() {
    val mlp = MLP(3, listOf(4, 4, 1))
    val out = mlp(listOf(2.0, 3.0, -1.0))
    assertThat(out.map { it.data }.single()).isNotEqualTo(0)
  }

  @Test
  fun `mlp backpropagation`() {
    // inputs
    val xs = listOf(
      listOf(2.0, 3.0, -1.0),
      listOf(3.0, -1.0, 0.5),
      listOf(0.5, 1.0, -1.0),
      listOf(1.0, 1.0, -1.0),
    )
    // desired targets
    val ys = listOf(1.0, -1.0, -1.0, 1.0)
    val n = MLP(3, listOf(4, 4, 1))
    val ypred = xs.flatMap { x -> n(x) }
    println(ypred.map { it.data })
    val loss = ys.zip(ypred)
      .map { (ygt, yout) -> (yout - ygt).pow(2.0) }
      .reduce { acc, value -> acc + value }
    println(loss.data)
    println("grad before=" + n.layers[0].neurons[0].w[0].grad)
    loss.backward()
    println("grad after=" + n.layers[0].neurons[0].w[0].grad)
    loss.generateGraph("src/test/resources/mlp.svg")
  }
}
