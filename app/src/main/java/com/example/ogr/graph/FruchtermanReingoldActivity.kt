package com.example.ogr.graph

import com.example.ogr.GraphActivity
import de.blox.graphview.Graph
import de.blox.graphview.GraphView
import de.blox.graphview.Node
import de.blox.graphview.energy.FruchtermanReingoldAlgorithm

class FruchtermanReingoldActivity: GraphActivity() {
    override fun createGraph(): Graph{
        val graph = Graph()
        val a = Node(getNodeText())
        val b = Node(getNodeText())
        val c = Node(getNodeText())
        val d = Node(getNodeText())
        val e = Node(getNodeText())
        val f = Node(getNodeText())
        val g = Node(getNodeText())
        val h = Node(getNodeText())

        graph.addEdge(a, b)
        graph.addEdge(a, c)
        graph.addEdge(a, d)
        graph.addEdge(c, e)
        graph.addEdge(d, f)
        graph.addEdge(f, c)
        graph.addEdge(g, c)
        graph.addEdge(h, g)

        return graph
    }

    override fun setLayout(view: GraphView) {
        view.setLayout(FruchtermanReingoldAlgorithm(1000))
    }
}