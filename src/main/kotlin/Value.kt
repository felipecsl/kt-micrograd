import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.attribute.Records
import guru.nidi.graphviz.attribute.Records.rec
import guru.nidi.graphviz.attribute.Records.turn
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.Factory.mutGraph
import guru.nidi.graphviz.model.Factory.mutNode
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.model.MutableNode
import java.io.File
import kotlin.math.exp
import kotlin.math.pow

data class Value(
  val data: Double,
  val children: Set<Value> = setOf(),
  val op: String = "",
  var label: String = "",
  val _backward: (Value) -> Unit = {},
) {
  var grad = 0.0

  override fun toString(): String {
    return "Value(data=$data, children=$children)"
  }

  operator fun plus(other: Value): Value {
    val self = this
    return Value(data + other.data, setOf(this, other), "+", _backward = {
      self.grad += it.grad
      other.grad += it.grad
    })
  }

  operator fun times(other: Value): Value {
    val self = this
    return Value(data * other.data, setOf(this, other), "*", _backward = {
      self.grad += other.data * it.grad
      other.grad += self.data * it.grad
    })
  }

  fun backward() {
    val topo = mutableListOf<Value>()
    val visited = mutableSetOf<Value>()
    fun buildTopologicalSort(v: Value) {
      if (v in visited) return
      visited.add(v)
      for (child in v.children) {
        buildTopologicalSort(child)
      }
      topo.add(v)
    }
    buildTopologicalSort(this)
    // go one variable at a time and apply the chain rule to get its gradient
    grad = 1.0
    for (v in topo.asReversed()) {
      v._backward(v)
    }
  }

  fun tanh(): Value {
    val x = data
    val t = (exp(2*x) - 1)/(exp(2*x) + 1)
    val self = this
    return Value(t, setOf(this), "tanh", _backward = {
      self.grad += (1 - t.pow(2)) * it.grad
    })
  }

  fun generateGraph(outFile: String) {
    val graph = mutGraph("graph")
      .setDirected(true)
      .graphAttrs()
      .add(Rank.dir(Rank.RankDir.LEFT_TO_RIGHT))
    processValue(this, graph)
    Graphviz.fromGraph(graph)
      .render(Format.SVG)
      .toFile(File(outFile))
  }

  companion object {
    fun processValue(
      currentValue: Value,
      graph: MutableGraph,
      nodesMap: MutableMap<Value, MutableNode> = mutableMapOf()
    ): MutableNode {
      return nodesMap.getOrPut(currentValue) {
        val (data, children, op, label) = currentValue
        var opNode: MutableNode? = null
        if (op != "") {
          opNode = mutNode("$label$op").add(Label.html(op))
          graph.add(opNode!!)
        }
        val node = mutNode("$label$data")
          .add(Records.of(turn(
            rec(label),
            rec("data ${String.format("%.4f", data)}"),
            rec("grad ${String.format("%.4f", currentValue.grad)}")
          )))
        graph.add(node)
        opNode?.addLink(node)
        children.forEach { child ->
          val childNode = processValue(child, graph, nodesMap)
          graph.add(childNode)
          if (opNode != null) {
            childNode.addLink(opNode)
          }
        }
        node
      }
    }
  }
}
