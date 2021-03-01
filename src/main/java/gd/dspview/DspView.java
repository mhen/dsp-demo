package gd.dspview;

import gd.geometry.BoundingBox;
import gd.geometry.Vector2D;
import gd.graph.DspEdge;
import gd.graph.DspVertex;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public final class DspView
{
    private final double VERTEX_SIZE = 30.0;

    private final Canvas canvas;

    private BoundingBox bounds;
    private double aspectRatio;

    public DspView(Canvas canvas)
    {
        this.canvas = canvas;
        this.bounds = new BoundingBox(
                new Vector2D(-1.0, -1.0),
                new Vector2D(1.0, 1.0)
        );
        aspectRatio = canvas.getWidth() / canvas.getHeight();

        canvas.widthProperty().addListener(o -> resize());
        canvas.heightProperty().addListener(o -> resize());
    }

    public void resize()
    {
        aspectRatio = canvas.getWidth() / canvas.getHeight();
        recalculateBoundingBox();
    }

    public void clear()
    {
        var context = canvas.getGraphicsContext2D();
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawLegend(Legend legend)
    {
        final double LEGEND_X = 40.0;
        final double LEGEND_Y = 20.0;
        final double CELL_HEIGHT = 40.0;
        final double CELL_WIDTH = 40.0;

        var entryPosition = new Vector2D(LEGEND_X, LEGEND_Y + CELL_HEIGHT / 2.0);

        for (var entry : legend.getEntries())
        {
            switch (entry.getType())
            {
                case VERTEX -> {
                    drawVertex(entryPosition, entry.getColor());
                    drawText(
                            entry.getDescription(),
                            entryPosition.add(new Vector2D(CELL_WIDTH, 0.0)),
                            14,
                            Color.BLACK,
                            TextAlignment.LEFT
                    );
                }
                case EDGE -> {
                    drawEdge(
                            entryPosition.add(new Vector2D(-CELL_WIDTH / 2.0, 0.0)),
                            entryPosition.add(new Vector2D(CELL_WIDTH / 2.0, 0.0)),
                            entry.getColor()
                    );
                    drawText(
                            entry.getDescription(),
                            entryPosition.add(new Vector2D(CELL_WIDTH, 0.0)),
                            14,
                            Color.BLACK,
                            TextAlignment.LEFT
                    );
                }
                default -> throw new UnsupportedOperationException("Unknown entry type " + entry.getType());
            }
            entryPosition = entryPosition.add(new Vector2D(0.0, CELL_HEIGHT));
        }
    }

    public void drawVertex(DspVertex vertex, Color color)
    {
        var vertexPosition = toScreenSpace(vertex.position());

        drawVertex(vertexPosition, color);
        drawText(vertex.label(), vertexPosition, 18, Color.BLACK, TextAlignment.CENTER);
    }

    public void drawEdge(
            DspVertex source,
            DspVertex target,
            DspEdge edge,
            Color color,
            Boolean curveEdge
    )
    {
        var sourcePosition = toScreenSpace(source.position());
        var targetPosition = toScreenSpace(target.position());

        var delta = targetPosition.subtract(sourcePosition);
        var unitTangent = delta.normalize();

        var edgeStart= sourcePosition.add(unitTangent.scale(VERTEX_SIZE / 2.0));
        var edgeEnd= targetPosition.subtract(unitTangent.scale(VERTEX_SIZE / 2.0));

        var context = canvas.getGraphicsContext2D();

        context.setLineWidth(2.0);
        context.setStroke(color);
        context.setFill(color);

        var midpoint = delta.scale(0.5).add(sourcePosition);
        if (curveEdge)
        {
            context.beginPath();
            context.moveTo(edgeStart.getX(), edgeStart.getY());
            var unitNormal = new Vector2D(-unitTangent.getY(), unitTangent.getX());
            var centerControlPoint = midpoint.add(unitNormal.scale(30));
            context.quadraticCurveTo(
                    centerControlPoint.getX(),
                    centerControlPoint.getY(),
                    edgeEnd.getX(),
                    edgeEnd.getY()
            );
            context.stroke();

            midpoint = computeCurvedEdgeMidpoint(edgeStart, centerControlPoint, edgeEnd);
            unitTangent = computeCurvedEdgeUnitTangent(centerControlPoint, edgeEnd);
            drawLabel(midpoint, Double.toString(edge.weight()), color);
            drawArrowHead(edgeEnd, unitTangent, color);
        }
        else
        {
            context.strokeLine(
                    edgeStart.getX(),
                    edgeStart.getY(),
                    edgeEnd.getX(),
                    edgeEnd.getY()
            );
            drawArrowHead(edgeEnd, unitTangent, color);
            drawLabel(midpoint, Double.toString(edge.weight()), color);
        }
    }

    public Boolean isPointWithinVertex(DspVertex vertex, Vector2D point)
    {
        var vertexPosition= toScreenSpace(vertex.position());
        return point.subtract(vertexPosition).length() <= VERTEX_SIZE / 2.0;
    }

    private void drawVertex(Vector2D position, Color color)
    {
        var context = canvas.getGraphicsContext2D();
        context.setLineWidth(3.0);
        context.setStroke(color);
        context.strokeOval(
                position.getX() - VERTEX_SIZE / 2,
                position.getY() - VERTEX_SIZE / 2,
                VERTEX_SIZE, VERTEX_SIZE
        );
    }

    private void drawEdge(Vector2D source, Vector2D target, Color color)
    {
        var delta = target.subtract(source);

        var unitTangent = delta.normalize();

        var context = canvas.getGraphicsContext2D();

        context.setLineWidth(2.0);
        context.setStroke(color);
        context.setFill(color);

        context.strokeLine(source.getX(), source.getY(), target.getX(), target.getY());

        drawArrowHead(target, unitTangent, color);
    }

    private void drawText(String text, Vector2D position, Integer fontSize, Color color, TextAlignment alignment)
    {
        var context = canvas.getGraphicsContext2D();
        context.setStroke(color);
        context.setLineWidth(1.0);
        context.setTextAlign(alignment);
        context.setFont(new Font(fontSize));
        context.strokeText(text, position.getX(), position.getY() + fontSize / 3.0);
    }

    private void drawArrowHead(Vector2D tip, Vector2D direction, Color color)
    {
        final double arrowLength = 10;
        final double arrowWidth = 4;

        var unitNormal = new Vector2D(-direction.getY(), direction.getX());

        var arrowHeadPoint1 =
                tip.subtract(direction.scale(arrowLength))
                        .add(unitNormal.scale(arrowWidth));
        var arrowHeadPoint2 =
                tip.subtract(direction.scale(arrowLength))
                        .subtract(unitNormal.scale(arrowWidth));

        var arrowHeadX = new double[] {
                tip.getX(),
                arrowHeadPoint1.getX(),
                arrowHeadPoint2.getX()
        };
        var arrowHeadY = new double[] {
                tip.getY(),
                arrowHeadPoint1.getY(),
                arrowHeadPoint2.getY()
        };

        var context = canvas.getGraphicsContext2D();
        context.setFill(color);
        context.fillPolygon(arrowHeadX, arrowHeadY, 3);
    }

    private void drawLabel(Vector2D midpoint, String text, Color color)
    {
        final double textRectWidth = 40.0;
        final double textRectHeight = 20.0;

        var context = canvas.getGraphicsContext2D();

        context.setFill(Color.WHITE);
        context.fillRect(
                midpoint.getX() - textRectWidth / 2.0,
                midpoint.getY() - textRectHeight / 2.0,
                textRectWidth,
                textRectHeight
        );
        context.strokeRect(
                midpoint.getX() - textRectWidth / 2.0,
                midpoint.getY() - textRectHeight / 2.0,
                textRectWidth,
                textRectHeight
        );

        drawText(text, midpoint, 14, color, TextAlignment.CENTER);
    }

    private Vector2D toScreenSpace(Vector2D vector)
    {
        var vbWidth = bounds.getUpperRight().getX() - bounds.getLowerLeft().getX();
        var vbHeight = bounds.getUpperRight().getY() - bounds.getLowerLeft().getY();
        var viewRelativePosition = vector.subtract(bounds.getLowerLeft());

        return new Vector2D(
                viewRelativePosition.getX() * (canvas.getWidth() / vbWidth),
                canvas.getHeight() - (viewRelativePosition.getY()) * (canvas.getHeight() / vbHeight)
        );
    }
    private void recalculateBoundingBox()
    {
        if (aspectRatio > 1.0)
        {
            bounds = new BoundingBox(
                    new Vector2D(-1.0 * aspectRatio, -1.0).scale(1.0),
                    new Vector2D(1.0 * aspectRatio, 1.0).scale(1.0)
            );
        }
        else
        {
            var invertedAspectRatio = 1.0 / aspectRatio;
            bounds = new BoundingBox(
                    new Vector2D(-1.0 , -1.0 * invertedAspectRatio).scale(1.0),
                    new Vector2D(1.0 , 1.0 * invertedAspectRatio).scale(1.0)
            );
        }
    }

    /**
     * Computes the unit tangent at the end of the bezier curve defined by second and third control points
     * point2 and point3, respectively.
     *
     * @param point2 Second control point
     * @param point3 Third control point
     * @return Unit tangent
     */
    private static Vector2D computeCurvedEdgeUnitTangent(Vector2D point2, Vector2D point3)
    {
       return point3.subtract(point2).scale(2.0).normalize();
    }

    /**
     * Computes the midpoint of a 3-point bezier curve with control points point1, point2, point3
     *
     * @param point1 First control point
     * @param point2 second control point
     * @param point3 third control point
     * @return Midpoint of the bezier curve formed by control points 1, 2 and 3
     */
    private static Vector2D computeCurvedEdgeMidpoint(Vector2D point1, Vector2D point2, Vector2D point3)
    {
        return point1.scale(0.25)
                .add(point2.scale(0.5))
                .add(point3.scale(0.25));
    }

}
