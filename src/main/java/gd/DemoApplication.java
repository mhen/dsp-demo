package gd;

import gd.dspview.*;
import gd.filesystem.*;
import gd.geometry.Vector2D;
import gd.graph.*;
import gd.graphalgorithms.*;
import gd.utilities.*;
import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;
import javafx.util.StringConverter;
import org.controlsfx.control.*;

import java.io.*;
import java.util.Comparator;
import java.util.stream.Collectors;

public final class DemoApplication extends Application
{
    private Graph<DspVertex, DspEdge> currentGraph;
    private DijkstraShortestPaths<DspVertex, DspEdge> dijkstraShortestPaths;

    private DspView dspView;
    private SearchableComboBox<DspVertex> sourceVertexSelector;
    private SearchableComboBox<DspVertex> targetVertexSelector;
    private Text totalCostInfo;

    public static void main(String[] args)
    {
        launch();
    }

    @Override
    public void start(Stage stage)
    {
        sourceVertexSelector = new SearchableComboBox<>();
        targetVertexSelector = new SearchableComboBox<>();
        totalCostInfo = new Text("N/A");

        var canvas = new Canvas();
        dspView = new DspView(canvas);

        currentGraph = TestData.generateTestGraph2();

        updateControls();
        dijkstraShortestPaths = new DijkstraShortestPaths<>(currentGraph, sourceVertexSelector.getValue(), DspEdge::weight);

        initializeLayout(stage, canvas);
        draw();
    }

    private void draw()
    {
        final Color COLOR_PENDING = Color.BLACK;
        final Color COLOR_FRONTIER = Color.YELLOW;
        final Color COLOR_SOURCE = Color.GREEN;
        final Color COLOR_TARGET = Color.BLUE;
        final Color COLOR_EXPANDED = Color.RED;
        final Color COLOR_PATH = Color.BLUE;

        dspView.clear();

        for (var vState : dijkstraShortestPaths.getState().entrySet())
        {
            var source = vState.getKey();
            switch (vState.getValue())
            {
                case PENDING -> {
                    dspView.drawVertex(source, COLOR_PENDING);
                    currentGraph.getNeighbours(source).forEach(neighbour ->
                            dspView.drawEdge(
                                    source, neighbour,
                                    currentGraph.getEdge(source, neighbour),
                                    COLOR_PENDING,
                                    currentGraph.hasEdge(neighbour, source)
                            )
                    );
                }
                case FRONTIER -> {
                    dspView.drawVertex(vState.getKey(), COLOR_FRONTIER);
                    currentGraph.getNeighbours(source).forEach(neighbour ->
                            dspView.drawEdge(
                                    source, neighbour,
                                    currentGraph.getEdge(source, neighbour),
                                    COLOR_PENDING,
                                    currentGraph.hasEdge(neighbour, source)
                            )
                    );
                }
                case EXPANDED -> {
                    dspView.drawVertex(vState.getKey(), COLOR_EXPANDED);
                    currentGraph.getNeighbours(source).forEach(neighbour ->
                            dspView.drawEdge(
                                    source, neighbour,
                                    currentGraph.getEdge(source, neighbour),
                                    COLOR_EXPANDED,
                                    currentGraph.hasEdge(neighbour, source)
                            )
                    );
                }
            }
        }

        if (dijkstraShortestPaths.isCompleted())
        {
            var shortestPath = dijkstraShortestPaths.getShortestPathTo(targetVertexSelector.getValue());
            for (int i = 0; i < shortestPath.size()-1; i++)
            {
                var currentPathVertex = shortestPath.get(i);
                var nextPathVertex = shortestPath.get(i+1);

                dspView.drawEdge(
                        currentPathVertex, nextPathVertex,
                        currentGraph.getEdge(currentPathVertex, nextPathVertex),
                        COLOR_PATH,
                        currentGraph.hasEdge(nextPathVertex, currentPathVertex)
                );
            }
        }

        var source = dijkstraShortestPaths.getSourceVertex();
        if (source != null)
        {
            dspView.drawVertex(source, COLOR_SOURCE);
        }

        var target = targetVertexSelector.getValue();
        if (target != null)
        {
            dspView.drawVertex(target, COLOR_TARGET);
        }

        dspView.drawLegend(new Legend(
                new LegendEntry(LegendEntryType.VERTEX, COLOR_FRONTIER, "Frontier vertex"),
                new LegendEntry(LegendEntryType.VERTEX, COLOR_EXPANDED, "Expanded vertex"),
                new LegendEntry(LegendEntryType.VERTEX, COLOR_SOURCE, "Source vertex"),
                new LegendEntry(LegendEntryType.VERTEX, COLOR_TARGET, "Target vertex"),
                new LegendEntry(LegendEntryType.EDGE, COLOR_EXPANDED, "Visited edge"),
                new LegendEntry(LegendEntryType.EDGE, COLOR_PATH, "Shortest path edge")
        ));
    }

    private void updateControls()
    {
        initializeVertexSelector(sourceVertexSelector, (s, o, n) ->
        {
            dijkstraShortestPaths = new DijkstraShortestPaths<>(currentGraph, n, DspEdge::weight);
            draw();
        });
        initializeVertexSelector(targetVertexSelector, (s, o, n) -> {
            updateTotalCost();
            draw();
        });
    }

    private void reset()
    {
        dijkstraShortestPaths = new DijkstraShortestPaths<>(currentGraph, sourceVertexSelector.getValue(), DspEdge::weight);
        sourceVertexSelector.setDisable(false);
        draw();
    }

    private void updateTotalCost()
    {
        var totalCost = dijkstraShortestPaths.getShortestPathCostTo(targetVertexSelector.getValue());
        totalCostInfo.setText(totalCost == Double.MAX_VALUE ? "N/A" : Double.toString(totalCost));
    }

    private void initializeLayout(Stage stage, Canvas canvas)
    {
        var parent = new BorderPane();
        parent.setPrefWidth(1280);
        parent.setPrefHeight(800);

        /* CANVAS */
        var pane = new Pane();

        pane.getChildren().add(canvas);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.widthProperty().addListener(e -> draw());
        canvas.heightProperty().addListener(e -> draw());
        canvas.setOnMousePressed(this::onCanvasClicked);

        parent.setCenter(pane);

        /* DEMO CONTROLS */
        var controls = new GridPane();
        controls.setPrefWidth(200);
        controls.setHgap(10.0);
        controls.setVgap(10.0);
        controls.setPadding(new Insets(10, 10, 10, 10));
        controls.setAlignment(Pos.TOP_CENTER);

        var columnConstraints = new ColumnConstraints();
        columnConstraints.setFillWidth(true);
        columnConstraints.setHgrow(Priority.SOMETIMES);
        columnConstraints.setPercentWidth(50);

        controls.getColumnConstraints().setAll(
                columnConstraints,
                columnConstraints
        );

        var sourceLabel = new Label("Source:");
        sourceLabel.setLabelFor(sourceVertexSelector);
        controls.add(sourceLabel, 0, 0);
        controls.add(sourceVertexSelector, 1, 0);

        var demoControls = new HBox(10.0);
        demoControls.setAlignment(Pos.CENTER);

        var resetButton = new Button("Reset");
        resetButton.setOnAction(this::onResetButtonPressed);

        var stepButton = new Button("Step");
        stepButton.setOnAction(this::onStepButtonPressed);

        var stepAllButton = new Button("Step All");
        stepAllButton.setOnAction(this::onStepAllButtonPressed);

        demoControls.getChildren().add(resetButton);
        demoControls.getChildren().add(stepButton);
        demoControls.getChildren().add(stepAllButton);

        controls.add(demoControls, 0, 1, 2, 1);

        controls.add(new Separator(), 0, 2, 2, 1);

        var targetLabel = new Label("Target:");
        targetLabel.setLabelFor(targetVertexSelector);
        controls.add(targetLabel, 0, 3);
        controls.add(targetVertexSelector, 1, 3);

        var totalCostLabel = new Label("Total Cost:");
        totalCostLabel.setLabelFor(totalCostInfo);
        controls.add(totalCostLabel, 0, 4);

        controls.add(totalCostInfo, 1, 4);

        parent.setLeft(controls);

        //MENU BAR
        MenuBar menubar = new MenuBar();

        var fileMenu = new Menu("File");
        var fromFileMenuItem = new MenuItem("Open...");
        fromFileMenuItem.setOnAction(this::onFileButtonPressed);

        var helpMenu = new Menu("Help");
        var aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setOnAction(this::onAboutButtonPressed);

        fileMenu.getItems().add(fromFileMenuItem);
        menubar.getMenus().add(fileMenu);

        helpMenu.getItems().add(aboutMenuItem);
        menubar.getMenus().add(helpMenu);

        parent.setTop(menubar);
        //---

        stage.setScene(new Scene(parent));
        stage.setTitle("Dijkstra Shortest Paths Demo");
        stage.setResizable(true);
        stage.show();
    }

    private void onAboutButtonPressed(ActionEvent event)
    {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("About Dijkstra Shortest Paths Demo");
        dialog.initStyle(StageStyle.UTILITY);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        GridPane aboutInfo = new GridPane();
        aboutInfo.setPadding(new Insets(10, 10, 10, 10));
        aboutInfo.setMaxWidth(Double.MAX_VALUE);

        var aboutText = new Text();

        final String text = """
                A dead-simple demo of the shortest paths algorithm by Edsger W. Dijkstra. 
                
                Use the controls on the left to select a starting point, and run the algorithm either by individual
                steps or all at once. When you're done, you can reset and try again!
                
                The generated shortest paths may be inspected by selecting a target node on the left, or by clicking the
                desired node in the view.
                
                You can import and run the algorithm on your own graphs defined in the GraphML format, by selecting the
                'Open...' option in the file menu. Any directed graph without self-referencing edges may be defined 
                within a 2x2 unit 2D coordinate system with ranges [-1,1] by [-1,1] - there is no explicit support
                for other graph types.
                
                Expand this dialog to see an example of a minimal GraphML file with all required attributes defined, or 
                go to http://graphml.graphdrawing.org/ to learn more.
                
                Have fun!
                /Morten
                """;

        aboutText.setText(text);
        aboutText.setTextAlignment(TextAlignment.LEFT);

        aboutInfo.add(aboutText, 0, 0);

        dialog.getDialogPane().setContent(aboutInfo);

        final String graphMlExampleText = """
            <?xml version="1.0" encoding="UTF-8"?>
            <graphml xmlns="http://graphml.graphdrawing.org/xmlns"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns
            http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">
                <key id="d0" for="node" attr.name="x" attr.type="double"/>
                <key id="d1" for="node" attr.name="y" attr.type="double"/>
                <key id="d2" for="node" attr.name="label" attr.type="string"/>
                <key id="d4" for="edge" attr.name="weight" attr.type="double">
                    <default>0.0</default>
                </key>
                <graph id="G" edgedefault="directed">
                    <node id="n0">
                        <data key="d0">-0.5</data>
                        <data key="d1">0.5</data>
                        <data key="d2">A</data>
                    </node>
                    <node id="n1">
                        <data key="d0">0.5</data>
                        <data key="d1">0.5</data>
                        <data key="d2">B</data>
                    </node>
                    <edge id="e0" source="n0" target="n1">
                        <data key="d4">1.0</data>
                    </edge>
                </graph>
            </graphml> 
            """;

        var graphMlExample = new TextArea(graphMlExampleText);

        dialog.getDialogPane().setExpandableContent(graphMlExample);
        dialog.showAndWait();
    }

    private void onCanvasClicked(MouseEvent event)
    {
        var clickPoint = new Vector2D(event.getX(), event.getY());
        for (var vertex : currentGraph.getVertices())
        {
            if (dspView.isPointWithinVertex(vertex, clickPoint))
            {
                targetVertexSelector.setValue(vertex);
                return;
            }
        }
    }

    private void onResetButtonPressed(ActionEvent event)
    {
        totalCostInfo.setText("N/A");
        reset();
    }

    private void onStepButtonPressed(ActionEvent event)
    {
        if (!dijkstraShortestPaths.isCompleted())
        {
            sourceVertexSelector.setDisable(true);
            dijkstraShortestPaths.step();
            updateTotalCost();
            draw();
        }
    }

    private void onStepAllButtonPressed(ActionEvent event)
    {
        sourceVertexSelector.setDisable(true);
        while (!dijkstraShortestPaths.isCompleted())
        {
            dijkstraShortestPaths.step();
        }

        updateTotalCost();
        draw();
    }

    private void onFileButtonPressed(ActionEvent event)
    {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        var selectedFile = fileChooser.showOpenDialog(Stage.getWindows().get(0));
        if (selectedFile != null)
        {
            try {
                currentGraph = GraphReader.readGraphMl(selectedFile.getAbsolutePath());
                updateControls();
                reset();
            }
            catch (NumberFormatException | GraphReaderException e)
            {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "Error reading " + selectedFile.getName() + ":\n" + e
                );
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        }
    }

    private void initializeVertexSelector(
        ComboBox<DspVertex> comboBox,
        ChangeListener<DspVertex> onSelect
)
    {
        comboBox.setItems(
                FXCollections
                        .observableArrayList(currentGraph.getVertices())
                        .sorted(Comparator
                                .comparing((DspVertex v) -> v.label().length())
                                .thenComparing(DspVertex::label)
                        )
        );
        comboBox.getSelectionModel().selectedItemProperty().addListener(onSelect);

        var reverseLabelMap = currentGraph.getVertices()
                .stream()
                .collect(Collectors.toMap(DspVertex::label, v -> v));

        comboBox.setConverter(new StringConverter<>()
        {
            @Override
            public String toString(DspVertex vertex)
            {
                if (vertex != null)
                {
                    return vertex.label();
                }
                return "";
            }

            @Override
            public DspVertex fromString(String label)
            {
                return reverseLabelMap.get(label);
            }
        });

        if (!comboBox.getItems().isEmpty())
        {
            comboBox.setValue(comboBox.getItems().get(0));
        }
    }
}
