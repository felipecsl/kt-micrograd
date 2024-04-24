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

data class Value(
  val data: Double,
  val children: Set<Value> = setOf(),
  val op: String = "",
  var label: String = "",
) {
  var grad = 0.0

  override fun toString(): String {
    return "Value(data=$data, children=$children)"
  }

  operator fun plus(other: Value): Value {
    return Value(data + other.data, setOf(this, other), "+")
  }

  operator fun times(other: Value): Value {
    return Value(data * other.data, setOf(this, other), "*")
  }

  fun tanh(): Value {
    val x = data
    val t = (exp(2*x) - 1)/(exp(2*x) + 1)
    return Value(t, setOf(this), "tanh")
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
          graph.add(opNode)
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
