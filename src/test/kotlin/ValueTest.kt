import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class ValueTest {
  @Test
  fun `test toString`() {
    val value = Value(1.0)
    assertThat(value.data).isEqualTo(1)
    assertThat(value.toString()).isEqualTo("Value(data=1, children=[])")
  }

  @Test
  fun `test add`() {
    val a = Value(2.0)
    val b = Value(-3.0)
    assertThat((a + b).data).isEqualTo(-1)
  }

  @Test
  fun `test multiply`() {
    val a = Value(2.0)
    val b = Value(-3.0)
    assertThat((a * b).data).isEqualTo(-6)
  }

  @Test
  fun `add and multiply`() {
    val a = Value(2.0)
    val b = Value(-3.0)
    val c = Value(10.0)
    assertThat((a * b + c).data).isEqualTo(4)
  }

  @Test
  fun `test children`() {
    val a = Value(2.0)
    val b = Value(-3.0)
    val c = Value(10.0)
    val d = a * b + c
    assertThat(d.children).isEqualTo(setOf(a * b, c))
  }

  @Test
  fun `test op`() {
    val a = Value(2.0)
    val b = Value(-3.0)
    val c = Value(10.0)
    val d = a * b + c
    assertThat((a * b).op).isEqualTo("*")
    assertThat(d.op).isEqualTo("+")
  }

  @Test
  fun `generateGraph`() {
    val a = Value(2.0, label = "a")
    val b = Value(-3.0, label = "b")
    val c = Value(10.0, label = "c")
    val e = a * b
    e.label = "e"
    val d = e + c
    d.label = "d"
    val f = Value(-2.0, label = "f")
    val L = d * f
    L.label = "L"
    L.generateGraph("example-graph.svg")
  }
}
