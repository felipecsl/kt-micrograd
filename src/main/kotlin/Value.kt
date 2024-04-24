import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.attribute.Records
import guru.nidi.graphviz.attribute.Records.rec
import guru.nidi.graphviz.attribute.Records.turn
import guru.nidi.graphviz.attribute.Shape
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.model.MutableNode
import java.io.File

data class Value(
  val data: Double,
  val children: Set<Value> = setOf(),
  val op: String = "",
  var label: String = "",
) {
  override fun toString(): String {
    return "Value(data=$data, children=$children)"
  }

  operator fun plus(other: Value): Value {
    return Value(data + other.data, setOf(this, other), "+")
  }

  operator fun times(other: Value): Value {
    return Value(data * other.data, setOf(this, other), "*")
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
        val dataNodeLabel = "data ${String.format("%.4f", data)}"
        val node = mutNode(dataNodeLabel)
          .add(Records.of(turn(rec(label), rec("data ${String.format("%.4f", data)}"))))
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
