import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class nnTest {
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
    println(out.map { it.data })
  }

  @Test
  fun `test mlp`() {
    val mlp = MLP(3, listOf(4, 4, 1))
    val out = mlp(listOf(2.0, 3.0, -1.0))
    println(out.map { it.data })
  }
}
