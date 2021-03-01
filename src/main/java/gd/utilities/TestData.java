package gd.utilities;

import gd.geometry.*;
import gd.graph.*;

import java.util.*;

public final class TestData
{
    public static Graph<DspVertex, DspEdge> generateTestGraph2()
    {
        final var graph = new Graph<DspVertex, DspEdge>();
        final var labelGun = new LabelGun();
        final var rand = new Random();
        final var n = 6;

        var xStart= -0.75;
        var yStart = 0.75;
        var xEnd = 0.75;
        var yEnd = -0.75;

        var xStep = (xEnd - xStart) / (n - 1);
        var yStep = (yEnd - yStart) / (n - 1);

        DspVertex[][] matrix = new DspVertex[n][n];

        for (int i = 0; i < n; ++i)
        {
            for (int j = 0; j < n; ++j)
            {
                matrix[i][j] = new DspVertex(
                        String.valueOf(i) + j,
                        new Vector2D(xStart + j * xStep, yStart + i * yStep),
                        labelGun.shoot()
                );

                graph.addVertex(matrix[i][j]);

                if (i - 1 >= 0)
                {
                    var edgeWeight = (double) rand.nextInt(10) + 1;
                    graph.addEdge(matrix[i-1][j], matrix[i][j], new DspEdge(edgeWeight));
                }

                if (j - 1 >= 0)
                {
                    var edgeWeight = (double) rand.nextInt(10) + 1;
                    graph.addEdge(matrix[i][j-1], matrix[i][j], new DspEdge(edgeWeight));
                }

                if (i - 1 >= 0 && j - 1 >= 0)
                {
                    var edgeWeight = (double) rand.nextInt(10) + 1;
                    graph.addEdge(matrix[i-1][j-1], matrix[i][j], new DspEdge(edgeWeight));
                }
            }
        }

        return graph;
    }

    public static Graph<DspVertex, DspEdge> generateTestGraph()
    {
        var graph = new Graph<DspVertex, DspEdge>();

        var labelGun = new LabelGun();

        var n1 = new DspVertex("k1", new Vector2D(-0.25, 0.75), labelGun.shoot());
        graph.addVertex(n1);
        var n2 = new DspVertex("k2", new Vector2D(0.25, 0.75), labelGun.shoot());
        graph.addVertex(n2);
        var n3 = new DspVertex("k3", new Vector2D(0.75, 0.25), labelGun.shoot());
        graph.addVertex(n3);
        var n4 = new DspVertex("k4", new Vector2D(0.75, -0.25), labelGun.shoot());
        graph.addVertex(n4);
        var n5 = new DspVertex("k5", new Vector2D(0.25, -0.75), labelGun.shoot());
        graph.addVertex(n5);
        var n6 = new DspVertex("k6", new Vector2D(-0.25, -0.75), labelGun.shoot());
        graph.addVertex(n6);
        var n7 = new DspVertex("k7", new Vector2D(-0.75, -0.25), labelGun.shoot());
        graph.addVertex(n7);
        var n8 = new DspVertex("k8", new Vector2D(-0.75, 0.25), labelGun.shoot());
        graph.addVertex(n8);

        graph.addEdge(n1, n2, new DspEdge(1.0));
        graph.addEdge(n2, n3, new DspEdge(1.0));
        graph.addEdge(n3, n4, new DspEdge(1.0));
        graph.addEdge(n4, n5, new DspEdge(1.0));
        graph.addEdge(n5, n6, new DspEdge(1.0));
        graph.addEdge(n6, n7, new DspEdge(1.0));
        graph.addEdge(n7, n8, new DspEdge(1.0));
        graph.addEdge(n8, n1, new DspEdge(1.0));

        graph.addEdge(n1, n3, new DspEdge(1.0));
        graph.addEdge(n3, n1, new DspEdge(2.0));
        graph.addEdge(n3, n5, new DspEdge(3.0));
        graph.addEdge(n5, n7, new DspEdge(1.0));
        graph.addEdge(n7, n1, new DspEdge(3.0));

        return graph;
    }
}
