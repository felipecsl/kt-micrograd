import com.google.common.truth.Truth.assertThat
import java.io.File
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
  fun `generate simple graph`() {
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
    val outFile = "src/test/resources/example-graph-actual.svg"
    L.generateGraph(outFile)
    val classLoader = javaClass.classLoader
    val file = File(classLoader.getResource("example-graph-expected.svg")!!.file)
    assertThat(File(outFile).readText()).isEqualTo(file.readText())
  }

  @Test
  fun `generate neuron graph`() {
    val x1 = Value(2.0, label = "x1")
    val x2 = Value(0.0, label = "x2")
    val w1 = Value(-3.0, label = "w1")
    val w2 = Value(1.0, label = "w2")
    val b = Value(6.881373587019543, label = "b")
    val x1w1 = x1 * w1; x1w1.label = "x1*w1"
    val x2w2 = x2 * w2; x2w2.label = "x2*w2"
    val x1w1x2w2 = x1w1 + x2w2; x1w1x2w2.label = "x1*w1 + x2*w2"
    val n = x1w1x2w2 + b; n.label = "n"
    val o = n.tanh(); o.label = "o"
    val outFile = "src/test/resources/neuron-graph-actual.svg"
    o.generateGraph(outFile)
  }

  @Test
  fun `backpropagation gradients`() {
    val x1 = Value(2.0, label = "x1")
    val x2 = Value(0.0, label = "x2")
    val w1 = Value(-3.0, label = "w1")
    val w2 = Value(1.0, label = "w2")
    val b = Value(6.881373587019543, label = "b")
    val x1w1 = x1 * w1; x1w1.label = "x1*w1"
    val x2w2 = x2 * w2; x2w2.label = "x2*w2"
    val x1w1x2w2 = x1w1 + x2w2; x1w1x2w2.label = "x1*w1 + x2*w2"
    val n = x1w1x2w2 + b; n.label = "n"
    val o = n.tanh(); o.label = "o"
    o.backward()
    val outFile = "src/test/resources/neuron-graph-backward-actual.svg"
    o.generateGraph(outFile)
    assertThat(x1.grad).isEqualTo(-1.4999999999999996)
    assertThat(w1.grad).isEqualTo(0.9999999999999998)
    assertThat(x2.grad).isEqualTo(0.4999999999999999)
    assertThat(w2.grad).isEqualTo(0.0)
    assertThat(x1w1.grad).isEqualTo(0.4999999999999999)
    assertThat(x2w2.grad).isEqualTo(0.4999999999999999)
    assertThat(x1w1x2w2.grad).isEqualTo(0.4999999999999999)
  }
}
