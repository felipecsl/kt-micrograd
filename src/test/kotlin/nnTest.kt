import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class nnTest {
  @Test
  fun `test neuron`() {
    val n = Neuron(2)
    val act = n(listOf(2.0, 3.0))
    val data = act.data
    assertThat(data).isNotEqualTo(0.0)
  }

  @Test
  fun `test layer`() {
    val layer = Layer(2, 3)
    val out = layer(listOf(2.0, 3.0))
    println(out.map { it.data })
  }
}
