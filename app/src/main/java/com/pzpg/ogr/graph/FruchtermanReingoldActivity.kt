package com.pzpg.ogr.graph

import de.blox.graphview.Graph
import de.blox.graphview.GraphView
import de.blox.graphview.Node
import de.blox.graphview.edgerenderer.StraightEdgeRenderer

/**
 * Code taken from https://github.com/Team-Blox/GraphView/blob/master/sample/src/main/java/de/blox/graphview/sample/Algorithms/FruchtermanReingoldActivity.java
 *
 */

class FruchtermanReingoldActivity: GraphActivity() {

    /**
     * Create a test graph to testing GraphView library
     */
    override fun createGraph(): Graph{
        val graph = Graph()
        val a = Node("1")
        val b = Node("2")
        val c = Node("3")
        val d = Node("4")
        val e = Node("5")
        val f = Node("6")
        val g = Node("7")
        val h = Node("8")

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
        val configurations = Algorithm(1000)
        configurations.setEdgeRenderer(StraightEdgeRenderer())
        view.setLayout(configurations)
    }
}