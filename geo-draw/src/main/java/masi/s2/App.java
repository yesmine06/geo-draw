package masi.s2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Rotate;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import java.io.*;
import java.util.*;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import masi.s2.graph.*;
import javafx.scene.control.TextInputDialog;
import masi.s2.logging.*;
import java.awt.Desktop;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import masi.s2.observer.Observer;
import masi.s2.observer.Observable;
import masi.s2.geometryAdapter.ShapeAdapter;
import masi.s2.geometryAdapter.RectangleAdapter;
import masi.s2.geometryAdapter.CircleAdapter;
import masi.s2.geometryAdapter.LineAdapter;
import masi.s2.geometryAdapter.TriangleAdapter;
import masi.s2.geometryAdapter.StarAdapter;
import masi.s2.geometryAdapter.ShapeAdapterFactory;
import masi.s2.observer.CanvasObservable;
import masi.s2.observer.CanvasEvent;
import masi.s2.observer.StatusBarObserver;
import masi.s2.observer.LoggingObserver;

/**
 * Hello world!
 *
 */
public class App extends Application {
    private Canvas canvas;
    private GraphicsContext gc;
    private Color currentColor = Color.BLACK;
    private double strokeWidth = 2.0;
    private String currentShape = "Rectangle";
    private double startX, startY;
    private ActionLogger actionLogger;
    private boolean isDrawing = false;
    private boolean isFilled = true;
    private CheckBox filledCheckBox;
    private Button drawButton;
    private static final double DEFAULT_SHAPE_SIZE = 200;
    private double currentShapeSize = DEFAULT_SHAPE_SIZE;
    private double rotationAngle = 0;
    private Stack<BufferedImage> undoStack = new Stack<>();
    private Stack<BufferedImage> redoStack = new Stack<>();
    private Label statusLabel;
    private Label shapeLabel;
    private Label colorLabel;
    private Label strokeSizeLabel;
    private Label shapeSizeLabel;
    private Label rotationLabel;
    private Graph graph;
    private ShortestPathStrategy pathStrategy;
    private boolean isGraphMode = false;
    private boolean isAddingEdge = false;
    private Node firstNodeForEdge = null;
    private int nodeCounter = 1;
    private LoggingManager loggingManager;
    private ComboBox<String> loggingStrategySelector;
    private ComboBox<String> algorithmSelector;
    private VBox controls;
    private Button findPathButton;
    private Button addEdgeButton;
    private Button viewLogsButton;
    private Label modeIndicator;
    private VBox graphControls;
    private ComboBox<String> shapeSelector;
    private ColorPicker colorPicker;

    // Variables pour le thème actuel
    private boolean isDarkTheme = true;
    private VBox shapeControls;
    private VBox controlButtons;
    private HBox root;
    private Button clearButton;
    private Button undoButton;
    private Button redoButton;
    private Button saveButton;
    private Button loadButton;

    // Ajout d'un état pour la sélection dans le mode graphe
    private enum GraphAction { NONE, ADD_EDGE, CALC_PATH }
    private GraphAction graphAction = GraphAction.NONE;
    private Node selectedNode1 = null;
    private Node selectedNode2 = null;

    // Styles sophistiqués avec glassmorphisme et effets modernes
    private static final String GLASS_EFFECT_DARK = """
        -fx-background-color: rgba(36, 40, 59, 0.85);
        -fx-background-radius: 16px;
        -fx-border-color: rgba(65, 72, 104, 0.5);
        -fx-border-width: 1px;
        -fx-border-radius: 16px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 4);
    """;

    private static final String GLASS_EFFECT_LIGHT = """
        -fx-background-color: rgba(233, 236, 239, 0.85);
        -fx-background-radius: 16px;
        -fx-border-color: rgba(222, 226, 230, 0.5);
        -fx-border-width: 1px;
        -fx-border-radius: 16px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 4);
    """;

    // Styles des labels
    private static final String LIGHT_LABEL_STYLE = """
        -fx-text-fill: #212529;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 5px 0;
    """;

    private static final String DARK_LABEL_STYLE = """
        -fx-text-fill: #FFFFFF;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 5px 0;
    """;

    // Styles du ColorPicker
    private static final String LIGHT_COLOR_PICKER_STYLE = """
        -fx-background-color: rgba(255, 255, 255, 0.85);
        -fx-text-fill: #212529;
        -fx-padding: 8px;
        -fx-background-radius: 12px;
        -fx-font-size: 15px;
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
        -fx-border-color: rgba(222, 226, 230, 0.5);
        -fx-border-width: 2px;
        -fx-border-radius: 12px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
    """;

    private static final String DARK_COLOR_PICKER_STYLE = """
        -fx-background-color: #24283B;
        -fx-text-fill: #FFFFFF;
        -fx-prompt-text-fill: #FFFFFF;
        -fx-text-base-color: #FFFFFF;
        -fx-padding: 8px;
        -fx-background-radius: 12px;
        -fx-font-size: 15px;
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
        -fx-border-color: rgba(65, 72, 104, 0.5);
        -fx-border-width: 1px;
        -fx-border-radius: 12px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
    """;

    // Styles des CheckBox
    private static final String LIGHT_CHECK_BOX_STYLE = """
        -fx-text-fill: #212529;
        -fx-font-size: 15px;
        -fx-background-color: transparent;
        -fx-padding: 5px 0;
    """;

    private static final String DARK_CHECK_BOX_STYLE = """
        -fx-text-fill: #a9b1d6;
        -fx-font-size: 15px;
        -fx-background-color: transparent;
        -fx-padding: 5px 0;
    """;

    // Styles de la barre d'état
    private static final String LIGHT_STATUS_STYLE = """
        -fx-text-fill: #0d6efd;
        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-background-color: rgba(233, 236, 239, 0.85);
        -fx-padding: 10px;
        -fx-background-radius: 12px;
        -fx-border-color: rgba(222, 226, 230, 0.5);
        -fx-border-width: 1px;
        -fx-border-radius: 12px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
    """;

    private static final String DARK_STATUS_STYLE = """
        -fx-text-fill: #7aa2f7;
        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-background-color: rgba(36, 40, 59, 0.85);
        -fx-padding: 10px;
        -fx-background-radius: 12px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
    """;

    // Styles boutons colorés et très contrastés (texte noir sur fond clair)
    private static final String GREEN_BUTTON_STYLE = "-fx-background-color: #43b581; -fx-text-fill: #222; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-border-color: #23272a; -fx-border-width: 2px;";
    private static final String BLUE_BUTTON_STYLE = "-fx-background-color: #5865f2; -fx-text-fill: #fff; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-border-color: #23272a; -fx-border-width: 2px;";
    private static final String RED_BUTTON_STYLE = "-fx-background-color: #ed4245; -fx-text-fill: #fff; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-border-color: #23272a; -fx-border-width: 2px;";
    private static final String GRAY_BUTTON_STYLE = "-fx-background-color: #b9bbbe; -fx-text-fill: #222; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-border-color: #23272a; -fx-border-width: 2px;";

    // Styles glassmorphism et modernes
    private static final String GLASS_SIDEBAR_LIGHT = "-fx-background-color: rgba(255,255,255,0.65);-fx-background-radius: 28px;-fx-effect: dropshadow(gaussian, rgba(30,30,60,0.10), 24, 0.2, 0, 8);-fx-border-color: #e0e0e0;-fx-border-width: 1.5px;-fx-border-radius: 28px;";
    private static final String GLASS_SIDEBAR_DARK = "-fx-background-color: rgba(40,44,52,0.65);-fx-background-radius: 28px;-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 24, 0.2, 0, 8);-fx-border-color: #23272a;-fx-border-width: 1.5px;-fx-border-radius: 28px;";
    private static final String MODERN_BUTTON_GRADIENT_LIGHT = "-fx-background-color: linear-gradient(90deg, #43b581 0%, #5865f2 100%);-fx-text-fill: #fff;-fx-font-size: 18px;-fx-font-family: 'Segoe UI', 'Roboto', 'Arial', sans-serif;-fx-font-weight: bold;-fx-background-radius: 16px;-fx-effect: dropshadow(gaussian, rgba(30,30,60,0.18), 8, 0.2, 0, 2);-fx-cursor: hand;";
    private static final String MODERN_BUTTON_GRADIENT_DARK = "-fx-background-color: linear-gradient(90deg, #23272a 0%, #5865f2 100%);-fx-text-fill: #fff;-fx-font-size: 18px;-fx-font-family: 'Segoe UI', 'Roboto', 'Arial', sans-serif;-fx-font-weight: bold;-fx-background-radius: 16px;-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0.2, 0, 2);-fx-cursor: hand;";
    private static final String MODERN_COMBOBOX_LIGHT = "-fx-background-color: rgba(255,255,255,0.85);-fx-text-fill: #23272a;-fx-font-size: 16px;-fx-font-family: 'Segoe UI', 'Roboto', 'Arial', sans-serif;-fx-background-radius: 12px;-fx-border-color: #b9bbbe;-fx-border-width: 2px;-fx-border-radius: 12px;";
    private static final String MODERN_COMBOBOX_DARK = "-fx-background-color: rgba(40,44,52,0.85);-fx-text-fill: #fff;-fx-font-size: 16px;-fx-font-family: 'Segoe UI', 'Roboto', 'Arial', sans-serif;-fx-background-radius: 12px;-fx-border-color: #5865f2;-fx-border-width: 2px;-fx-border-radius: 12px;";
    private static final String MODERN_LABEL_LIGHT = "-fx-text-fill: #23272a;-fx-font-size: 17px;-fx-font-family: 'Segoe UI', 'Roboto', 'Arial', sans-serif;-fx-font-weight: bold;-fx-padding: 7px 0;";
    private static final String MODERN_LABEL_DARK = "-fx-text-fill: #fff;-fx-font-size: 17px;-fx-font-family: 'Segoe UI', 'Roboto', 'Arial', sans-serif;-fx-font-weight: bold;-fx-padding: 7px 0;";

    // Styles des boutons avec dégradés
    private static final String GRADIENT_BUTTON_DARK = """
        -fx-background-color: linear-gradient(to right, #24283b, #414868);
        -fx-text-fill: #a9b1d6;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 12px 20px;
        -fx-background-radius: 12px;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
    """;

    private static final String GRADIENT_BUTTON_LIGHT = """
        -fx-background-color: linear-gradient(to right, #e9ecef, #dee2e6);
        -fx-text-fill: #212529;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 12px 20px;
        -fx-background-radius: 12px;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
    """;

    private static final String ACCENT_BUTTON_DARK = """
        -fx-background-color: linear-gradient(to right, #7aa2f7, #bb9af7);
        -fx-text-fill: #1a1b26;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 12px 20px;
        -fx-background-radius: 12px;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
    """;

    private static final String ACCENT_BUTTON_LIGHT = """
        -fx-background-color: linear-gradient(to right, #0d6efd, #6f42c1);
        -fx-text-fill: #ffffff;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 12px 20px;
        -fx-background-radius: 12px;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
    """;

    // Styles du Canvas
    private static final String DARK_CANVAS_STYLE = """
        -fx-background-color: white;
        -fx-border-color: rgba(65, 72, 104, 0.5);
        -fx-border-width: 2px;
        -fx-border-radius: 16px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 4);
    """;

    private static final String LIGHT_CANVAS_STYLE = """
        -fx-background-color: white;
        -fx-border-color: rgba(222, 226, 230, 0.5);
        -fx-border-width: 2px;
        -fx-border-radius: 16px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 4);
    """;

    // Styles des ComboBox
    private static final String DARK_COMBO_BOX_STYLE = """
        -fx-background-color: #24283B;
        -fx-text-fill: #FFFFFF;
        -fx-prompt-text-fill: #FFFFFF;
        -fx-text-base-color: #FFFFFF;
        -fx-mark-color: #FFFFFF;
        -fx-padding: 8px;
        -fx-background-radius: 12px;
        -fx-font-size: 15px;
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
        -fx-border-color: rgba(65, 72, 104, 0.5);
        -fx-border-width: 1px;
        -fx-border-radius: 12px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
        -fx-cell-background-color: #24283B;
        -fx-cell-text-fill: #FFFFFF;
        -fx-popup-background-color: #24283B;
        -fx-background: #24283B;
        -fx-control-inner-background: #24283B;
    """;

    private static final String LIGHT_COMBO_BOX_STYLE = """
        -fx-background-color: rgba(255, 255, 255, 0.85);
        -fx-text-fill: #212529;
        -fx-padding: 8px;
        -fx-background-radius: 12px;
        -fx-font-size: 15px;
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
        -fx-border-color: rgba(222, 226, 230, 0.5);
        -fx-border-width: 2px;
        -fx-border-radius: 12px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
    """;

    // Styles des thèmes
    private static final String DARK_THEME = """
        -fx-background-color: linear-gradient(to bottom right, #1a1b26, #24283b);
        -fx-text-fill: #FFFFFF;
    """;

    private static final String LIGHT_THEME = """
        -fx-background-color: linear-gradient(to bottom right, #f8f9fa, #e9ecef);
        -fx-text-fill: #212529;
    """;

    // Styles des panneaux
    private static final String DARK_TOOLBAR_STYLE = GLASS_EFFECT_DARK;
    private static final String LIGHT_TOOLBAR_STYLE = GLASS_EFFECT_LIGHT;
    private static final String DARK_CONTROL_PANEL_STYLE = GLASS_EFFECT_DARK;
    private static final String LIGHT_CONTROL_PANEL_STYLE = GLASS_EFFECT_LIGHT;

    // Styles des boutons toggle
    private static final String DARK_TOGGLE_BUTTON_STYLE = """
        -fx-background-color: rgba(36, 40, 59, 0.85);
        -fx-text-fill: #a9b1d6;
        -fx-padding: 8px 16px;
        -fx-background-radius: 12px;
        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
    """;

    private static final String LIGHT_TOGGLE_BUTTON_STYLE = """
        -fx-background-color: rgba(255, 255, 255, 0.85);
        -fx-text-fill: #212529;
        -fx-padding: 8px 16px;
        -fx-background-radius: 12px;
        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-border-color: rgba(222, 226, 230, 0.5);
        -fx-border-width: 1px;
        -fx-border-radius: 12px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
    """;

    // Styles des sliders
    private static final String DARK_SLIDER_STYLE = """
        -fx-control-inner-background: #414868;
        -fx-background-color: #24283b;
        -fx-background-radius: 12px;
        -fx-padding: 8px;
        -fx-text-fill: #a9b1d6;
    """;

    private static final String LIGHT_SLIDER_STYLE = """
        -fx-control-inner-background: #fff;
        -fx-background-color: #e9ecef;
        -fx-background-radius: 12px;
        -fx-padding: 8px;
    """;

    // Styles des boutons standards
    private static final String DARK_BUTTON_STYLE = GRADIENT_BUTTON_DARK;
    private static final String LIGHT_BUTTON_STYLE = GRADIENT_BUTTON_LIGHT;

    // Styles des boutons de logs
    private static final String LOGS_BUTTON_DARK = """
        -fx-background-color: linear-gradient(to right, #24283b, #414868);
        -fx-text-fill: #FFFFFF;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 12px 20px;
        -fx-background-radius: 12px;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
    """;

    private static final String LOGS_BUTTON_LIGHT = """
        -fx-background-color: linear-gradient(to right, #e9ecef, #dee2e6);
        -fx-text-fill: #212529;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 12px 20px;
        -fx-background-radius: 12px;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
    """;

    private Map<String, ShapeAdapter> shapeAdapters;
    private CanvasObservable canvasObservable;

    @Override
    public void start(Stage primaryStage) {
        // Initialisation des adaptateurs de forme
        shapeAdapters = new HashMap<>();
        shapeAdapters.put("Rectangle", new RectangleAdapter());
        shapeAdapters.put("Cercle", new CircleAdapter());
        shapeAdapters.put("Ligne", new LineAdapter());
        shapeAdapters.put("Triangle", new TriangleAdapter());
        shapeAdapters.put("Étoile", new StarAdapter());

        // Initialisation de l'observable
        canvasObservable = new CanvasObservable();
        
        // Initialisation des composants de base
        canvas = new Canvas(900, 600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.setStyle(isDarkTheme ? DARK_CANVAS_STYLE : LIGHT_CANVAS_STYLE);

        // Initialisation du système de journalisation
        loggingManager = LoggingManager.getInstance(new ConsoleLoggingStrategy());
        actionLogger = new ActionLogger();

        // Ajout des observateurs
        canvasObservable.addObserver(new StatusBarObserver(statusLabel, isDarkTheme));
        canvasObservable.addObserver(new LoggingObserver(loggingManager));

        // Initialisation du graphe
        graph = new Graph();
        pathStrategy = new DijkstraStrategy();
        
        // Initialisation des labels avec une taille de police plus petite
        shapeLabel = new Label("Forme :");
        colorLabel = new Label("Couleur :");
        strokeSizeLabel = new Label("Épaisseur :");
        shapeSizeLabel = new Label("Taille :");
        rotationLabel = new Label("Rotation :");
        modeIndicator = new Label("Mode: Dessin");
        modeIndicator.setStyle("-fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.8); -fx-padding: 3px;");
        
        // Ajout du sélecteur d'algorithme
        algorithmSelector = new ComboBox<>(
            FXCollections.observableArrayList("Dijkstra", "A*", "Floyd-Warshall")
        );
        algorithmSelector.setValue("Dijkstra");
        algorithmSelector.setMinWidth(180);
        algorithmSelector.setPrefWidth(180);
        algorithmSelector.setStyle(isDarkTheme ? DARK_COMBO_BOX_STYLE : LIGHT_COMBO_BOX_STYLE);
        
        // Gestionnaire pour le sélecteur d'algorithme
        algorithmSelector.setOnAction(e -> {
            String selectedAlgorithm = algorithmSelector.getValue();
            switch (selectedAlgorithm) {
                case "Dijkstra":
                    pathStrategy = new DijkstraStrategy();
                    break;
                case "A*":
                    pathStrategy = new AStarStrategy();
                    break;
                case "Floyd-Warshall":
                    pathStrategy = new FloydWarshallStrategy();
                    break;
            }
            updateStatus("Algorithme sélectionné : " + selectedAlgorithm);
        });
        
        // Style des labels avec une taille de police plus petite
        String labelStyle = isDarkTheme ? 
            """
            -fx-text-fill: #cdd6f4;
            -fx-font-size: 12px;
            -fx-font-weight: bold;
            -fx-padding: 3px 0;
            """ : 
            """
            -fx-text-fill: #4c4f69;
            -fx-font-size: 12px;
            -fx-font-weight: bold;
            -fx-padding: 3px 0;
            """;
            
        shapeLabel.setStyle(LIGHT_LABEL_STYLE);
        colorLabel.setStyle(LIGHT_LABEL_STYLE);
        strokeSizeLabel.setStyle(LIGHT_LABEL_STYLE);
        shapeSizeLabel.setStyle(LIGHT_LABEL_STYLE);
        rotationLabel.setStyle(LIGHT_LABEL_STYLE);
        
        // Initialisation du sélecteur de forme avec une taille réduite
        shapeSelector = new ComboBox<>(
            FXCollections.observableArrayList("Rectangle", "Cercle", "Ligne", "Triangle", "Étoile", "Graphe")
        );
        shapeSelector.setValue("Rectangle");
        shapeSelector.setMinWidth(180);
        shapeSelector.setPrefWidth(180);
        shapeSelector.setStyle(isDarkTheme ? DARK_COMBO_BOX_STYLE : LIGHT_COMBO_BOX_STYLE);

        // Initialisation du sélecteur de couleur avec une taille réduite
        colorPicker = new ColorPicker(currentColor);
        colorPicker.setMinWidth(180);
        colorPicker.setPrefWidth(180);
        colorPicker.setStyle(isDarkTheme ? DARK_COLOR_PICKER_STYLE : LIGHT_COLOR_PICKER_STYLE);

        // Initialisation des sliders avec une taille réduite
        Slider strokeSizeSlider = createStyledSlider(1, 10, strokeWidth, 1);
        strokeSizeSlider.setMinWidth(180);
        strokeSizeSlider.setPrefWidth(180);
        
        Slider shapeSizeSlider = createStyledSlider(50, 400, currentShapeSize, 50);
        shapeSizeSlider.setMinWidth(180);
        shapeSizeSlider.setPrefWidth(180);
        
        Slider rotationSlider = createStyledSlider(0, 360, rotationAngle, 45);
        rotationSlider.setMinWidth(180);
        rotationSlider.setPrefWidth(180);

        // Initialisation de la case à cocher avec une taille réduite
        filledCheckBox = new CheckBox("Remplir");
        filledCheckBox.setSelected(true);
        filledCheckBox.setStyle(isDarkTheme ? DARK_CHECK_BOX_STYLE : LIGHT_CHECK_BOX_STYLE);
        
        // Initialisation du label de statut avec une taille réduite
        statusLabel = new Label("Prêt");
        statusLabel.setStyle(isDarkTheme ? DARK_STATUS_STYLE : LIGHT_STATUS_STYLE);
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setAlignment(Pos.CENTER);
        
        // Initialisation des boutons avec une taille réduite
        drawButton = new Button("🎨 Dessiner");
        drawButton.setStyle(GREEN_BUTTON_STYLE);
        clearButton = new Button("🗑️ Effacer");
        clearButton.setStyle(RED_BUTTON_STYLE);
        undoButton = new Button("↩️ Annuler");
        undoButton.setStyle(BLUE_BUTTON_STYLE);
        redoButton = new Button("↪️ Rétablir");
        redoButton.setStyle(BLUE_BUTTON_STYLE);
        saveButton = new Button("💾 Sauver");
        saveButton.setStyle(GREEN_BUTTON_STYLE);
        loadButton = new Button("📂 Charger");
        loadButton.setStyle(GREEN_BUTTON_STYLE);

        // Initialisation du sélecteur de thème
        ToggleButton themeToggle = new ToggleButton("🌙");
        themeToggle.setSelected(true);
        themeToggle.setStyle(isDarkTheme ? DARK_TOGGLE_BUTTON_STYLE : LIGHT_TOGGLE_BUTTON_STYLE);

        // Création du conteneur pour le thème
        HBox themeBox = new HBox(5);
        themeBox.setAlignment(Pos.CENTER_RIGHT);
        themeBox.getChildren().add(themeToggle);

        // Création du conteneur principal des contrôles avec largeur augmentée
        controls = new VBox(8);
        controls.setStyle(isDarkTheme ? DARK_CONTROL_PANEL_STYLE : LIGHT_CONTROL_PANEL_STYLE);
        controls.setPadding(new Insets(12));
        controls.setPrefWidth(200);
        
        // Ajout des contrôles de forme
        controls.getChildren().addAll(
            themeBox,
            shapeLabel,
            shapeSelector,
            colorLabel,
            colorPicker,
            strokeSizeLabel,
            strokeSizeSlider,
            shapeSizeLabel,
            shapeSizeSlider,
            rotationLabel,
            rotationSlider,
            filledCheckBox,
            drawButton
        );

        // --- Contrôles spécifiques au mode Graphe ---
        findPathButton = new Button("🔍 Calculer le chemin");
        findPathButton.setStyle(GREEN_BUTTON_STYLE);
        addEdgeButton = new Button("➕ Ajouter une arête");
        addEdgeButton.setStyle(BLUE_BUTTON_STYLE);
        graphControls = new VBox(8, addEdgeButton, findPathButton, new Label("Algorithme :"), algorithmSelector);
        graphControls.setAlignment(Pos.CENTER);
        graphControls.setPrefWidth(180);
        graphControls.setVisible(false);
        controls.getChildren().add(graphControls);
        // ---

        // Création de la barre d'outils avec boutons plus larges
        controlButtons = new VBox(12);
        controlButtons.setAlignment(Pos.CENTER);
        controlButtons.setStyle(isDarkTheme ? DARK_TOOLBAR_STYLE : LIGHT_TOOLBAR_STYLE);
        controlButtons.setPrefWidth(200);
        for (Button btn : new Button[]{clearButton, undoButton, redoButton, saveButton, loadButton}) {
            btn.setPrefWidth(180);
            btn.setStyle((isDarkTheme ? DARK_BUTTON_STYLE : LIGHT_BUTTON_STYLE) + "-fx-background-color: inherit;");
        }
        controlButtons.getChildren().setAll(clearButton, undoButton, redoButton, saveButton, loadButton);

        // Organisation principale
        HBox drawingArea = new HBox(15);
        drawingArea.setAlignment(Pos.CENTER);
        drawingArea.getChildren().setAll(canvas, controlButtons);

        // ComboBox et bouton de journalisation en bas du panneau
        loggingStrategySelector = new ComboBox<>();
        loggingStrategySelector.getItems().addAll("Console", "Fichier", "Base de données");
        loggingStrategySelector.setValue("Console");
        loggingStrategySelector.setStyle(isDarkTheme ? DARK_COMBO_BOX_STYLE : LIGHT_COMBO_BOX_STYLE);
        loggingStrategySelector.setPrefWidth(180);
        viewLogsButton = new Button("📋 Voir les logs");
        viewLogsButton.setStyle(isDarkTheme ? LOGS_BUTTON_DARK : LOGS_BUTTON_LIGHT);
        viewLogsButton.setPrefWidth(180);
        viewLogsButton.setOnAction(e -> {
            String selectedStrategy = loggingStrategySelector.getValue();
            try {
                switch (selectedStrategy) {
                    case "Console":
                        updateStatus("Les logs sont visibles dans la console");
                        Alert alertConsole = new Alert(Alert.AlertType.INFORMATION);
                        alertConsole.setTitle("Logs Console");
                        alertConsole.setHeaderText(null);
                        alertConsole.setContentText("Les logs sont affichés dans la console Java.");
                        alertConsole.showAndWait();
                        break;
                    case "Fichier":
                        File logFile = new File("app.log");
                        if (logFile.exists()) {
                            Desktop.getDesktop().open(logFile);
                            updateStatus("Fichier de log ouvert");
                        } else {
                            updateStatus("Aucun fichier de log trouvé");
                            Alert alertFile = new Alert(Alert.AlertType.WARNING);
                            alertFile.setTitle("Fichier de log");
                            alertFile.setHeaderText(null);
                            alertFile.setContentText("Aucun fichier de log trouvé.");
                            alertFile.showAndWait();
                        }
                        break;
                    case "Base de données":
                        showDatabaseLogs();
                        break;
                }
            } catch (Exception ex) {
                loggingManager.logError("Erreur lors de l'affichage des logs : " + ex.getMessage());
                updateStatus("Erreur lors de l'affichage des logs");
            }
        });

        VBox loggingControls = new VBox(8, loggingStrategySelector, viewLogsButton);
        loggingControls.setAlignment(Pos.CENTER);
        loggingControls.setPadding(new Insets(10, 0, 10, 0));
        loggingControls.setStyle("-fx-background-color: transparent;");

        // Organisation finale
        VBox mainControls = new VBox(8);
        mainControls.getChildren().setAll(controls);
        mainControls.setStyle(isDarkTheme ? GLASS_SIDEBAR_DARK : GLASS_SIDEBAR_LIGHT);
        mainControls.setPrefWidth(220);
        VBox.setVgrow(controls, Priority.ALWAYS);
        mainControls.getChildren().add(loggingControls);
        // Ajout du ScrollPane autour du panneau de gauche
        ScrollPane scrollPane = new ScrollPane(mainControls);
        scrollPane.setPrefWidth(220);
        scrollPane.setFitToWidth(true);

        // Création du HBox root avec padding réduit
        root = new HBox(10);
        root.getChildren().setAll(scrollPane, drawingArea);
        root.setPadding(new Insets(8));
        root.setStyle(isDarkTheme ? DARK_THEME : LIGHT_THEME);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Geo-Draw");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);

        // Ajout des gestionnaires d'événements
        setupEventHandlers(primaryStage, colorPicker, strokeSizeSlider, shapeSizeSlider, rotationSlider,
                         themeToggle, scene);

        primaryStage.show();
    }

    private void setupEventHandlers(Stage primaryStage, ColorPicker colorPicker,
                                  Slider strokeSizeSlider, Slider shapeSizeSlider, Slider rotationSlider,
                                  ToggleButton themeToggle, Scene scene) {
        // Gestionnaire pour le sélecteur de forme
        shapeSelector.setOnAction(e -> {
            currentShape = shapeSelector.getValue();
            isGraphMode = currentShape.equals("Graphe");
            graphControls.setVisible(isGraphMode);
            filledCheckBox.setVisible(!isGraphMode);
            drawButton.setVisible(!isGraphMode);
            if (isGraphMode) {
                updateStatus("Mode Graphe activé - Cliquez pour créer des nœuds. Utilisez les boutons pour ajouter des arêtes ou calculer un chemin.");
            } else {
                updateStatus("Mode Dessin activé");
            }
        });

        // Gestionnaire pour le sélecteur de couleur
        colorPicker.setOnAction(e -> {
            currentColor = colorPicker.getValue();
            gc.setStroke(currentColor);
            gc.setFill(currentColor);
            updateStatus("Couleur sélectionnée : " + currentColor.toString());
        });

        // Gestionnaires pour les sliders
        strokeSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            strokeWidth = newVal.doubleValue();
            gc.setLineWidth(strokeWidth);
            updateStatus("Épaisseur du trait : " + String.format("%.1f", strokeWidth));
        });

        shapeSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentShapeSize = newVal.doubleValue();
            updateStatus("Taille de la forme : " + String.format("%.0f", currentShapeSize));
        });

        rotationSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            rotationAngle = newVal.doubleValue();
            updateStatus("Rotation : " + String.format("%.0f", rotationAngle) + "°");
        });

        // Gestionnaire pour la case à cocher
        filledCheckBox.setOnAction(e -> {
            isFilled = filledCheckBox.isSelected();
            updateStatus("Remplissage : " + (isFilled ? "activé" : "désactivé"));
        });

        // Gestionnaire pour le bouton de thème
        themeToggle.setOnAction(e -> {
            isDarkTheme = themeToggle.isSelected();
            themeToggle.setText(isDarkTheme ? "🌙" : "☀️");
            updateTheme();
            updateStatus("Thème " + (isDarkTheme ? "sombre" : "clair") + " activé");
        });

        // Gestionnaire pour le bouton de dessin
        drawButton.setOnAction(e -> {
            saveToUndoStack();
            double centerX = canvas.getWidth() / 2;
            double centerY = canvas.getHeight() / 2;
            double halfSize = currentShapeSize / 2;
            drawShape(
                centerX - halfSize,
                centerY - halfSize,
                centerX + halfSize,
                centerY + halfSize
            );
            updateStatus("Forme dessinée");
        });

        // Gestionnaire pour le bouton d'effacement
        clearButton.setOnAction(e -> clearCanvas());

        // Gestionnaire pour le bouton d'annulation
        undoButton.setOnAction(e -> undo());

        // Gestionnaire pour le bouton de rétablissement
        redoButton.setOnAction(e -> redo());

        // Gestionnaire pour le bouton de sauvegarde
        saveButton.setOnAction(e -> saveDrawing(primaryStage));

        // Gestionnaire pour le bouton de chargement
        loadButton.setOnAction(e -> loadDrawing(primaryStage));

        // Raccourcis clavier
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case Z:
                        if (event.isShiftDown()) {
                            redo();
                        } else {
                            undo();
                        }
                        break;
                    case S:
                        saveDrawing(primaryStage);
                        break;
                    case O:
                        loadDrawing(primaryStage);
                        break;
                    case E:
                        clearButton.fire();
                        break;
                }
            }
        });

        // Gestionnaire pour les clics sur le canvas
        canvas.setOnMousePressed(e -> {
            if (isGraphMode) {
                double x = e.getX();
                double y = e.getY();
                Node clicked = graph.getNodeAt(x, y);
                if (graphAction == GraphAction.ADD_EDGE) {
                    if (clicked == null) {
                        updateStatus("Cliquez sur un nœud existant pour sélectionner.");
                        return;
                    }
                    if (selectedNode1 == null) {
                        selectedNode1 = clicked;
                        updateStatus("Premier nœud sélectionné : " + clicked.getId() + ". Cliquez sur le second nœud.");
                    } else if (selectedNode2 == null && clicked != selectedNode1) {
                        selectedNode2 = clicked;
                        // Demander le poids
                        TextInputDialog dialog = new TextInputDialog("1.0");
                        dialog.setTitle("Poids de l'arête");
                        dialog.setHeaderText("Entrez le poids de l'arête entre " + selectedNode1.getId() + " et " + selectedNode2.getId());
                        dialog.setContentText("Poids :");
                        dialog.showAndWait().ifPresent(weightStr -> {
                            try {
                                double weight = Double.parseDouble(weightStr);
                                graph.addEdge(selectedNode1, selectedNode2, weight);
                                updateStatus("Arête ajoutée entre " + selectedNode1.getId() + " et " + selectedNode2.getId() + " (" + weight + ")");
                            } catch (NumberFormatException ex) {
                                updateStatus("Poids invalide.");
                            }
                            selectedNode1 = null;
                            selectedNode2 = null;
                            graphAction = GraphAction.NONE;
                            redrawCanvas();
                        });
                    }
                } else if (graphAction == GraphAction.CALC_PATH) {
                    if (clicked == null) {
                        updateStatus("Cliquez sur un nœud existant pour sélectionner le départ.");
                        return;
                    }
                    if (selectedNode1 == null) {
                        selectedNode1 = clicked;
                        updateStatus("Départ : " + clicked.getId() + ". Cliquez sur le nœud d'arrivée.");
                    } else if (selectedNode2 == null && clicked != selectedNode1) {
                        selectedNode2 = clicked;
                        // Calculer le chemin
                        findShortestPath();
                    }
                } else {
                    // Ajout de nœud
                    if (clicked == null) {
                        Node newNode = new Node(x, y, String.valueOf(nodeCounter++));
                        graph.addNode(newNode);
                        updateStatus("Nœud ajouté : " + newNode.getId());
                        redrawCanvas();
                    }
                }
            } else {
                // Mode dessin normal
                startX = e.getX();
                startY = e.getY();
                isDrawing = true;
                saveToUndoStack();
            }
        });

        // Actions des boutons Graphe
        addEdgeButton.setOnAction(e -> {
            graphAction = GraphAction.ADD_EDGE;
            selectedNode1 = null;
            selectedNode2 = null;
            updateStatus("Mode ajout d'arête : sélectionnez deux nœuds.");
        });
        findPathButton.setOnAction(e -> {
            graphAction = GraphAction.CALC_PATH;
            selectedNode1 = null;
            selectedNode2 = null;
            updateStatus("Mode calcul de chemin : sélectionnez le départ puis l'arrivée.");
        });
    }

    private Slider createStyledSlider(double min, double max, double value, double majorTickUnit) {
        Slider slider = new Slider(min, max, value);
        slider.setPrefWidth(180);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(majorTickUnit);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setStyle(isDarkTheme ? DARK_SLIDER_STYLE : LIGHT_SLIDER_STYLE);
        return slider;
    }

    private Button createStyledButton(String text, String color, String emoji) {
        Button button = new Button(emoji + " " + text);
        button.setMaxWidth(Double.MAX_VALUE);
        
        String baseStyle = isDarkTheme ? 
            (color.equals("#27ae60") ? ACCENT_BUTTON_DARK : GRADIENT_BUTTON_DARK) :
            (color.equals("#27ae60") ? ACCENT_BUTTON_LIGHT : GRADIENT_BUTTON_LIGHT);
            
        button.setStyle(baseStyle);
        
        // Effet de survol
        button.setOnMouseEntered(e -> {
            button.setStyle(baseStyle + """
                -fx-scale-x: 1.05;
                -fx-scale-y: 1.05;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 4);
            """);
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(baseStyle);
        });
        
        return button;
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
        
        // Animation pour attirer l'attention sur le changement de statut
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), statusLabel);
        fadeTransition.setFromValue(0.5);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.play();
        
        loggingManager.log("Status", message);
    }

    private void saveToUndoStack() {
        BufferedImage image = SwingFXUtils.fromFXImage(canvas.snapshot(null, null), null);
        undoStack.push(image);
        redoStack.clear();
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            BufferedImage currentImage = SwingFXUtils.fromFXImage(canvas.snapshot(null, null), null);
            redoStack.push(currentImage);
            
            BufferedImage previousImage = undoStack.pop();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(SwingFXUtils.toFXImage(previousImage, null), 0, 0);
            updateStatus("Action annulée");
        }
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            BufferedImage currentImage = SwingFXUtils.fromFXImage(canvas.snapshot(null, null), null);
            undoStack.push(currentImage);
            
            BufferedImage nextImage = redoStack.pop();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(SwingFXUtils.toFXImage(nextImage, null), 0, 0);
            updateStatus("Action rétablie");
        }
    }

    private void saveDrawing(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder le dessin");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images PNG", "*.png")
        );
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try {
                BufferedImage image = SwingFXUtils.fromFXImage(canvas.snapshot(null, null), null);
                ImageIO.write(image, "png", file);
                updateStatus("Dessin sauvegardé : " + file.getName());
                logAction("Sauvegarde", "Dessin sauvegardé : " + file.getAbsolutePath());
            } catch (IOException e) {
                updateStatus("Erreur lors de la sauvegarde");
            }
        }
    }

    private void loadDrawing(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger un dessin");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images PNG", "*.png")
        );
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            try {
                BufferedImage image = ImageIO.read(file);
                saveToUndoStack();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.drawImage(SwingFXUtils.toFXImage(image, null), 0, 0);
                updateStatus("Dessin chargé : " + file.getName());
                logAction("Chargement", "Dessin chargé : " + file.getAbsolutePath());
            } catch (IOException e) {
                updateStatus("Erreur lors du chargement");
            }
        }
    }

    private void updateFillOption() {
        // Mise à jour de l'état de l'option de remplissage
        boolean isLine = currentShape.equals("Ligne");
        filledCheckBox.setDisable(isLine);
        if (isLine) {
            isFilled = false;
            filledCheckBox.setSelected(false);
        }
    }

    private void drawShape(double startX, double startY, double endX, double endY) {
        // Mise à jour des propriétés de dessin
        gc.setStroke(currentColor);
        gc.setFill(currentColor);
        gc.setLineWidth(strokeWidth);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        // Sauvegarder l'état du contexte graphique
        gc.save();

        // Calculer le centre de la forme
        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;

        // Appliquer la rotation
        gc.translate(centerX, centerY);
        gc.rotate(rotationAngle);
        gc.translate(-centerX, -centerY);

        // Utiliser la factory pour obtenir l'adaptateur de forme
        ShapeAdapter adapter = ShapeAdapterFactory.createAdapter(currentShape);
        if (adapter != null) {
            adapter.draw(gc, startX, startY, endX, endY, isFilled);
            canvasObservable.notifyObservers(new CanvasEvent(CanvasEvent.EventType.DRAW, null));
        }

        // Restaurer l'état du contexte graphique
        gc.restore();

        logAction("Dessin", "Forme : " + currentShape + ", Couleur : " + currentColor.toString());
    }

    private void updateTheme() {
        isDarkTheme = !isDarkTheme;
        String buttonStyle = isDarkTheme ? GRADIENT_BUTTON_DARK : GRADIENT_BUTTON_LIGHT;
        String accentButtonStyle = isDarkTheme ? ACCENT_BUTTON_DARK : ACCENT_BUTTON_LIGHT;
        String toolbarStyle = isDarkTheme ? GLASS_EFFECT_DARK : GLASS_EFFECT_LIGHT;
        String controlPanelStyle = isDarkTheme ? GLASS_EFFECT_DARK : GLASS_EFFECT_LIGHT;
        String rootStyle = isDarkTheme ? 
            "-fx-background-color: linear-gradient(to bottom right, #1a1b26, #24283b);" :
            "-fx-background-color: linear-gradient(to bottom right, #f8f9fa, #e9ecef);";

        root.setStyle(rootStyle);
        controls.setStyle(controlPanelStyle);
        controlButtons.setStyle(toolbarStyle);
        graphControls.setStyle(controlPanelStyle);

        // Mise à jour des labels
        shapeLabel.setStyle(isDarkTheme ? DARK_LABEL_STYLE : LIGHT_LABEL_STYLE);
        colorLabel.setStyle(isDarkTheme ? DARK_LABEL_STYLE : LIGHT_LABEL_STYLE);
        strokeSizeLabel.setStyle(isDarkTheme ? DARK_LABEL_STYLE : LIGHT_LABEL_STYLE);
        shapeSizeLabel.setStyle(isDarkTheme ? DARK_LABEL_STYLE : LIGHT_LABEL_STYLE);
        rotationLabel.setStyle(isDarkTheme ? DARK_LABEL_STYLE : LIGHT_LABEL_STYLE);

        // Mise à jour des boutons
        drawButton.setStyle(accentButtonStyle);
        clearButton.setStyle(accentButtonStyle);
        undoButton.setStyle(buttonStyle);
        redoButton.setStyle(buttonStyle);
        saveButton.setStyle(accentButtonStyle);
        loadButton.setStyle(accentButtonStyle);
        findPathButton.setStyle(accentButtonStyle);
        addEdgeButton.setStyle(accentButtonStyle);
        viewLogsButton.setStyle(isDarkTheme ? LOGS_BUTTON_DARK : LOGS_BUTTON_LIGHT);

        // Mise à jour des autres composants
        canvas.setStyle(isDarkTheme ? DARK_CANVAS_STYLE : LIGHT_CANVAS_STYLE);
        if (loggingStrategySelector != null) {
            loggingStrategySelector.setStyle(isDarkTheme ? DARK_COMBO_BOX_STYLE : LIGHT_COMBO_BOX_STYLE);
            loggingStrategySelector.setStyle(loggingStrategySelector.getStyle() + (isDarkTheme ? "-fx-text-fill: #FFFFFF;" : "-fx-text-fill: #212529;"));
        }
        if (shapeSelector != null) {
            shapeSelector.setStyle(isDarkTheme ? DARK_COMBO_BOX_STYLE : LIGHT_COMBO_BOX_STYLE);
            shapeSelector.setStyle(shapeSelector.getStyle() + (isDarkTheme ? "-fx-text-fill: #FFFFFF;" : "-fx-text-fill: #212529;"));
        }
        if (algorithmSelector != null) {
            algorithmSelector.setStyle(isDarkTheme ? DARK_COMBO_BOX_STYLE : LIGHT_COMBO_BOX_STYLE);
            algorithmSelector.setStyle(algorithmSelector.getStyle() + (isDarkTheme ? "-fx-text-fill: #FFFFFF;" : "-fx-text-fill: #212529;"));
        }
        if (colorPicker != null) {
            colorPicker.setStyle(isDarkTheme ? DARK_COLOR_PICKER_STYLE : LIGHT_COLOR_PICKER_STYLE);
            colorPicker.setStyle(colorPicker.getStyle() + (isDarkTheme ? "-fx-text-fill: #FFFFFF;" : "-fx-text-fill: #212529;"));
        }
        if (filledCheckBox != null) {
            filledCheckBox.setStyle(isDarkTheme ? DARK_CHECK_BOX_STYLE : LIGHT_CHECK_BOX_STYLE);
        }
        if (statusLabel != null) {
            statusLabel.setStyle(isDarkTheme ? DARK_STATUS_STYLE : LIGHT_STATUS_STYLE);
        }

        // Animation de transition
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), root);
        fadeTransition.setFromValue(0.8);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        canvasObservable.notifyObservers(new CanvasEvent(CanvasEvent.EventType.THEME_CHANGED, isDarkTheme));
    }

    private void redrawCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (isGraphMode && graph != null) {
            graph.draw(gc);
            // Afficher le chemin le plus court en vert
            List<Node> path = graph.getShortestPath();
            if (path != null && path.size() > 1) {
                gc.setStroke(Color.LIMEGREEN);
                gc.setLineWidth(5);
                for (int i = 0; i < path.size() - 1; i++) {
                    Node n1 = path.get(i);
                    Node n2 = path.get(i+1);
                    gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
                }
            }
        }
    }

    private void logAction(String action, String details) {
        loggingManager.log(action, details);
    }

    private void logError(String error) {
        loggingManager.logError(error);
        updateStatus("Erreur : " + error);
    }

    private void showDatabaseLogs() {
        Stage logStage = new Stage();
        logStage.setTitle("Logs de la base de données");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle(DARK_CONTROL_PANEL_STYLE);

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefRowCount(20);
        logArea.setPrefColumnCount(50);
        logArea.setStyle("""
            -fx-background-color: white;
            -fx-text-fill: black;
            -fx-font-family: monospace;
        """);

        try {
            // S'assurer que la stratégie est DatabaseLoggingStrategy
            if (!(loggingManager.getStrategy() instanceof DatabaseLoggingStrategy)) {
                loggingManager.setStrategy(new DatabaseLoggingStrategy("app.db"));
            }
            DatabaseLoggingStrategy dbStrategy = (DatabaseLoggingStrategy) loggingManager.getStrategy();
            List<String> logs = dbStrategy.getAllLogs();
            // Afficher les logs dans la zone de texte
            StringBuilder logText = new StringBuilder();
            for (String log : logs) {
                logText.append(log).append("\n");
            }
            logArea.setText(logText.toString());
        } catch (Exception e) {
            logArea.setText("Erreur lors de la récupération des logs : " + e.getMessage());
        }

        Button closeButton = createStyledButton("Fermer", "#27ae60", "❌");
        closeButton.setOnAction(e -> logStage.close());

        root.getChildren().addAll(logArea, closeButton);

        Scene scene = new Scene(root);
        logStage.setScene(scene);
        logStage.show();
    }

    private void showGraphModeInstructions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mode Graphe - Instructions");
        alert.setHeaderText("Bienvenue dans le mode Graphe");
        
        String content = 
            "Comment utiliser le mode Graphe :\n\n" +
            "1. Cliquez sur le canvas pour créer des nœuds\n" +
            "2. Utilisez le bouton 'Ajouter une arête' pour connecter deux nœuds\n" +
            "   - Sélectionnez le premier nœud\n" +
            "   - Sélectionnez le deuxième nœud\n" +
            "   - Entrez le poids de l'arête\n" +
            "3. Pour calculer le chemin le plus court :\n" +
            "   - Cliquez sur un nœud pour le définir comme point de départ\n" +
            "   - Cliquez sur un autre nœud pour le définir comme destination\n" +
            "   - Cliquez sur 'Calculer le chemin'\n\n" +
            "Le chemin le plus court sera affiché en vert sur le graphe.";
        
        alert.setContentText(content);
        
        // Ajouter une image d'exemple si disponible
        try {
            // Créer une image d'exemple de graphe avec chemin le plus court
            Canvas exampleCanvas = new Canvas(300, 200);
            GraphicsContext gc = exampleCanvas.getGraphicsContext2D();
            
            // Fond blanc
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, exampleCanvas.getWidth(), exampleCanvas.getHeight());
            
            // Dessiner un exemple de graphe
            gc.setFill(Color.BLUE);
            gc.fillOval(50, 50, 40, 40);  // Nœud 1
            gc.fillOval(200, 50, 40, 40); // Nœud 2
            gc.fillOval(50, 150, 40, 40); // Nœud 3
            gc.fillOval(200, 150, 40, 40); // Nœud 4
            
            // Dessiner les arêtes
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeLine(70, 70, 220, 70);    // 1-2
            gc.strokeLine(70, 70, 70, 170);    // 1-3
            gc.strokeLine(220, 70, 220, 170);  // 2-4
            gc.strokeLine(70, 170, 220, 170);  // 3-4
            
            // Dessiner le chemin le plus court (1-3-4)
            gc.setStroke(Color.GREEN);
            gc.setLineWidth(3);
            gc.strokeLine(70, 70, 70, 170);    // 1-3
            gc.strokeLine(70, 170, 220, 170);  // 3-4
            
            // Ajouter les IDs des nœuds
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 12));
            gc.fillText("1", 65, 75);
            gc.fillText("2", 215, 75);
            gc.fillText("3", 65, 175);
            gc.fillText("4", 215, 175);
            
            // Ajouter les poids
            gc.setFill(Color.BLACK);
            gc.fillText("5", 140, 60);  // 1-2
            gc.fillText("2", 50, 110);  // 1-3
            gc.fillText("3", 230, 110); // 2-4
            gc.fillText("1", 140, 190); // 3-4
            
            // Créer une image à partir du canvas
            WritableImage image = new WritableImage(
                (int) exampleCanvas.getWidth(), 
                (int) exampleCanvas.getHeight()
            );
            exampleCanvas.snapshot(null, image);
            
            // Ajouter l'image à la boîte de dialogue
            ImageView imageView = new ImageView(image);
            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(new Label(content), imageView);
            vbox.setPadding(new Insets(10));
            
            // Remplacer le contenu de l'alerte par notre VBox personnalisée
            alert.getDialogPane().setContent(vbox);
        } catch (Exception ex) {
            // En cas d'erreur, utiliser simplement le texte
            System.err.println("Erreur lors de la création de l'image d'exemple: " + ex.getMessage());
        }
        
        alert.showAndWait();
    }

    @Override
    public void stop() {
        if (loggingManager != null) {
            loggingManager.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Calcul de la longueur d'un chemin (somme des poids)
    private double calculatePathLength(List<Node> path) {
        double length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node next = path.get(i + 1);
            Double w = current.getNeighbors().get(next);
            if (w != null) length += w;
        }
        return length;
    }

    private void clearCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(currentColor);
        canvasObservable.notifyObservers(new CanvasEvent(CanvasEvent.EventType.CLEAR, null));

        // Réinitialiser le graphe et les variables associées
        graph = new Graph();
        nodeCounter = 1;
        selectedNode1 = null;
        selectedNode2 = null;
        graphAction = GraphAction.NONE;
        if (isGraphMode) {
            updateStatus("Graphe réinitialisé.");
        }
    }

    private void findShortestPath() {
        if (selectedNode1 == null || selectedNode2 == null) {
            showAlert("Erreur", "Veuillez sélectionner un nœud de départ et d'arrivée");
            return;
        }

        try {
            List<Node> path = pathStrategy.findShortestPath(graph, selectedNode1, selectedNode2);
            if (path.isEmpty()) {
                updateStatus("Aucun chemin trouvé entre " + selectedNode1.getId() + " et " + selectedNode2.getId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Aucun chemin trouvé");
                alert.setHeaderText("Impossible de trouver un chemin");
                alert.setContentText("Il n'existe pas de chemin entre le nœud " + selectedNode1.getId() + " et le nœud " + selectedNode2.getId());
                alert.showAndWait();
            } else {
                double pathLength = calculatePathLength(path);
                graph.setShortestPath(path);
                String chemin = path.stream().map(Node::getId).reduce((a,b)->a+"→"+b).orElse("");
                updateStatus("Chemin : " + chemin + " (longueur : " + String.format("%.2f", pathLength) + ")");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chemin le plus court");
                alert.setHeaderText("Chemin trouvé entre " + selectedNode1.getId() + " et " + selectedNode2.getId());
                alert.setContentText("Chemin : " + chemin + "\nLongueur totale : " + String.format("%.2f", pathLength));
                alert.showAndWait();
            }
            selectedNode1 = null;
            selectedNode2 = null;
            graphAction = GraphAction.NONE;
            redrawCanvas();
        } catch (PathNotFoundException e) {
            showAlert("Erreur", e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}