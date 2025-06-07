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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
import masi.s2.adapter.ShapeAdapter;
import masi.s2.adapter.RectangleAdapter;
import masi.s2.adapter.CircleAdapter;
import masi.s2.adapter.LineAdapter;
import masi.s2.adapter.TriangleAdapter;
import masi.s2.adapter.StarAdapter;
import masi.s2.adapter.ShapeAdapterFactory;

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

    // Thème clair moderne et très contrasté
    private static final String LIGHT_THEME = """
        -fx-background-color: #f5f6fa;
        -fx-text-fill: #222;
    """;
    // Thème sombre moderne et très contrasté
    private static final String DARK_THEME = """
        -fx-background-color: #23272e;
        -fx-text-fill: #fff;
    """;
    // Style des boutons - thème sombre
    private static final String DARK_BUTTON_STYLE = """
        -fx-font-size: 16px;
        -fx-text-fill: #fff;
        -fx-background-color: #3b4252;
        -fx-padding: 12px 20px;
        -fx-background-radius: 8px;
        -fx-cursor: hand;
        -fx-font-weight: bold;
    """;
    // Style des boutons - thème clair
    private static final String LIGHT_BUTTON_STYLE = """
        -fx-font-size: 16px;
        -fx-text-fill: #23272e;
        -fx-background-color: #e1e5ee;
        -fx-padding: 12px 20px;
        -fx-background-radius: 8px;
        -fx-cursor: hand;
        -fx-font-weight: bold;
    """;
    // Style des labels - thème sombre
    private static final String DARK_LABEL_STYLE = """
        -fx-text-fill: #fff;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 5px 0;
    """;
    // Style des labels - thème clair
    private static final String LIGHT_LABEL_STYLE = """
        -fx-text-fill: #222;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 5px 0;
    """;
    // Style de la barre d'état - thème sombre
    private static final String DARK_STATUS_STYLE = """
        -fx-text-fill: #fff;
        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-background-color: #3b4252;
        -fx-padding: 10px;
        -fx-background-radius: 5px;
    """;
    // Style de la barre d'état - thème clair
    private static final String LIGHT_STATUS_STYLE = """
        -fx-text-fill: #23272e;
        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-background-color: #e1e5ee;
        -fx-padding: 10px;
        -fx-background-radius: 5px;
        -fx-border-color: #bcc0cc;
        -fx-border-width: 1px;
        -fx-border-radius: 5px;
    """;
    // Style des ComboBox - thème sombre
    private static final String DARK_COMBO_BOX_STYLE = """
        -fx-background-color: #444857;
        -fx-text-fill: #fff;
        -fx-prompt-text-fill: #fff;
        -fx-padding: 8px;
        -fx-background-radius: 8px;
        -fx-font-size: 15px;
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
        -fx-border-color: #888;
        -fx-border-width: 1px;
        -fx-border-radius: 8px;
    """;
    // Style des ComboBox - thème clair
    private static final String LIGHT_COMBO_BOX_STYLE = """
        -fx-background-color: #fff;
        -fx-text-fill: #222;
        -fx-padding: 8px;
        -fx-background-radius: 8px;
        -fx-font-size: 15px;
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
        -fx-border-color: #b9bbbe;
        -fx-border-width: 2px;
        -fx-border-radius: 8px;
    """;
    // Style des CheckBox - thème sombre
    private static final String DARK_CHECK_BOX_STYLE = """
        -fx-text-fill: #fff;
        -fx-font-size: 15px;
        -fx-background-color: transparent;
        -fx-padding: 5px 0;
    """;
    // Style des CheckBox - thème clair
    private static final String LIGHT_CHECK_BOX_STYLE = """
        -fx-text-fill: #23272e;
        -fx-font-size: 15px;
        -fx-background-color: transparent;
        -fx-padding: 5px 0;
    """;
    // Style des ToggleButton - thème sombre
    private static final String DARK_TOGGLE_BUTTON_STYLE = """
        -fx-background-color: #3b4252;
        -fx-text-fill: #fff;
        -fx-padding: 8px 16px;
        -fx-background-radius: 8px;
        -fx-font-size: 15px;
        -fx-font-weight: bold;
    """;
    // Style des ToggleButton - thème clair
    private static final String LIGHT_TOGGLE_BUTTON_STYLE = """
        -fx-background-color: #fff;
        -fx-text-fill: #23272e;
        -fx-padding: 8px 16px;
        -fx-background-radius: 8px;
        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-border-color: #bcc0cc;
        -fx-border-width: 1px;
        -fx-border-radius: 8px;
    """;
    // Style de la barre d'outils - thème sombre
    private static final String DARK_TOOLBAR_STYLE = """
        -fx-background-color: #313244;
        -fx-padding: 12px;
        -fx-spacing: 12px;
        -fx-background-radius: 10px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);
    """;
    // Style de la barre d'outils - thème clair
    private static final String LIGHT_TOOLBAR_STYLE = """
        -fx-background-color: #dce0e8;
        -fx-padding: 12px;
        -fx-spacing: 12px;
        -fx-background-radius: 10px;
        -fx-border-color: #bcc0cc;
        -fx-border-width: 1px;
        -fx-border-radius: 10px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);
    """;
    // Style du panneau de contrôle - thème sombre
    private static final String DARK_CONTROL_PANEL_STYLE = """
        -fx-background-color: #313244;
        -fx-padding: 18px;
        -fx-spacing: 15px;
        -fx-background-radius: 10px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);
    """;
    // Style du panneau de contrôle - thème clair
    private static final String LIGHT_CONTROL_PANEL_STYLE = """
        -fx-background-color: #dce0e8;
        -fx-padding: 18px;
        -fx-spacing: 15px;
        -fx-background-radius: 10px;
        -fx-border-color: #bcc0cc;
        -fx-border-width: 1px;
        -fx-border-radius: 10px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);
    """;
    // Style des Sliders - thème sombre
    private static final String DARK_SLIDER_STYLE = """
        -fx-control-inner-background: #45475a;
        -fx-background-color: #313244;
        -fx-background-radius: 8px;
    """;
    // Style des Sliders - thème clair
    private static final String LIGHT_SLIDER_STYLE = """
        -fx-control-inner-background: #ffffff;
        -fx-background-color: #dce0e8;
        -fx-background-radius: 8px;
    """;
    // Style du Canvas - thème sombre
    private static final String DARK_CANVAS_STYLE = """
        -fx-background-color: white;
        -fx-border-color: #45475a;
        -fx-border-width: 2px;
        -fx-border-radius: 10px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);
    """;
    // Style du Canvas - thème clair
    private static final String LIGHT_CANVAS_STYLE = """
        -fx-background-color: white;
        -fx-border-color: #bcc0cc;
        -fx-border-width: 2px;
        -fx-border-radius: 10px;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);
    """;
    // Style du ColorPicker - thème sombre
    private static final String DARK_COLOR_PICKER_STYLE = """
        -fx-background-color: #444857;
        -fx-text-fill: #fff;
        -fx-prompt-text-fill: #fff;
        -fx-padding: 8px;
        -fx-background-radius: 8px;
        -fx-font-size: 15px;
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
        -fx-border-color: #888;
        -fx-border-width: 1px;
        -fx-border-radius: 8px;
    """;
    // Style du ColorPicker - thème clair
    private static final String LIGHT_COLOR_PICKER_STYLE = """
        -fx-background-color: #fff;
        -fx-text-fill: #222;
        -fx-padding: 8px;
        -fx-background-radius: 8px;
        -fx-font-size: 15px;
        -fx-min-width: 180px;
        -fx-pref-width: 180px;
        -fx-border-color: #b9bbbe;
        -fx-border-width: 2px;
        -fx-border-radius: 8px;
    """;

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

    private Map<String, ShapeAdapter> shapeAdapters;
    private Observable canvasObservable;

    @Override
    public void start(Stage primaryStage) {
        // Initialisation des adaptateurs de forme
        shapeAdapters = new HashMap<>();
        shapeAdapters.put("Rectangle", new RectangleAdapter());
        shapeAdapters.put("Cercle", new CircleAdapter());
        shapeAdapters.put("Ligne", new LineAdapter());
        shapeAdapters.put("Triangle", new TriangleAdapter());
        shapeAdapters.put("Étoile", new StarAdapter());
        // Ajouter d'autres adaptateurs ici...

        // Initialisation de l'observable
        canvasObservable = new Observable();
        canvasObservable.addObserver(new Observer() {
            @Override
            public void update(String event, Object data) {
                switch (event) {
                    case "DRAW":
                        updateStatus("Forme dessinée");
                        break;
                    case "CLEAR":
                        updateStatus("Canvas effacé");
                        break;
                    case "THEME_CHANGED":
                        updateStatus("Thème " + (isDarkTheme ? "sombre" : "clair") + " activé");
                        break;
                }
            }
        });

        // Initialisation des composants de base
        canvas = new Canvas(900, 600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.setStyle(isDarkTheme ? DARK_CANVAS_STYLE : LIGHT_CANVAS_STYLE);

        // Initialisation du système de journalisation
        loggingManager = LoggingManager.getInstance(new ConsoleLoggingStrategy());
        actionLogger = new ActionLogger();

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
        algorithmSelector.setMinWidth(120);
        algorithmSelector.setPrefWidth(120);
        algorithmSelector.setStyle(LIGHT_COMBO_BOX_STYLE);
        
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
        ComboBox<String> shapeSelector = new ComboBox<>(
            FXCollections.observableArrayList("Rectangle", "Cercle", "Ligne", "Triangle", "Étoile", "Graphe")
        );
        shapeSelector.setValue("Rectangle");
        shapeSelector.setMinWidth(120);
        shapeSelector.setPrefWidth(120);
        shapeSelector.setStyle(LIGHT_COMBO_BOX_STYLE);

        // Initialisation du sélecteur de couleur avec une taille réduite
        ColorPicker colorPicker = new ColorPicker(currentColor);
        colorPicker.setMinWidth(120);
        colorPicker.setPrefWidth(120);
        colorPicker.setStyle(LIGHT_COLOR_PICKER_STYLE);

        // Initialisation des sliders avec une taille réduite
        Slider strokeSizeSlider = createStyledSlider(1, 10, strokeWidth, 1);
        strokeSizeSlider.setMinWidth(120);
        strokeSizeSlider.setPrefWidth(120);
        
        Slider shapeSizeSlider = createStyledSlider(50, 400, currentShapeSize, 50);
        shapeSizeSlider.setMinWidth(120);
        shapeSizeSlider.setPrefWidth(120);
        
        Slider rotationSlider = createStyledSlider(0, 360, rotationAngle, 45);
        rotationSlider.setMinWidth(120);
        rotationSlider.setPrefWidth(120);

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

        // Création du conteneur principal des contrôles avec largeur réduite
        controls = new VBox(8);
        controls.setStyle(isDarkTheme ? DARK_CONTROL_PANEL_STYLE : LIGHT_CONTROL_PANEL_STYLE);
        controls.setPadding(new Insets(8));
        controls.setPrefWidth(180);
        
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
        graphControls.setVisible(false);
        controls.getChildren().add(graphControls);
        // ---

        // Création de la barre d'outils avec boutons plus larges
        controlButtons = new VBox(12);
        controlButtons.setAlignment(Pos.CENTER);
        controlButtons.setStyle(isDarkTheme ? DARK_TOOLBAR_STYLE : LIGHT_TOOLBAR_STYLE);
        controlButtons.setPrefWidth(170);
        for (Button btn : new Button[]{clearButton, undoButton, redoButton, saveButton, loadButton}) {
            btn.setPrefWidth(150);
            btn.setStyle((isDarkTheme ? DARK_BUTTON_STYLE : LIGHT_BUTTON_STYLE) + "-fx-background-color: inherit;");
        }
        controlButtons.getChildren().setAll(clearButton, undoButton, redoButton, saveButton, loadButton);

        // Organisation principale
        HBox drawingArea = new HBox(20);
        drawingArea.setAlignment(Pos.CENTER);
        drawingArea.getChildren().setAll(canvas, controlButtons);

        // ComboBox et bouton de journalisation en bas du panneau
        loggingStrategySelector = new ComboBox<>();
        loggingStrategySelector.getItems().addAll("Console", "Fichier", "Base de données");
        loggingStrategySelector.setValue("Console");
        loggingStrategySelector.setStyle("-fx-background-color: #fff; -fx-text-fill: #23272a; -fx-font-size: 15px; -fx-border-color: #b9bbbe; -fx-border-width: 2px; -fx-border-radius: 8px;");
        loggingStrategySelector.setPrefWidth(120);
        viewLogsButton = new Button("📋 Voir les logs");
        viewLogsButton.setStyle(GRAY_BUTTON_STYLE);
        viewLogsButton.setPrefWidth(120);
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
        HBox loggingControls = new HBox(8, loggingStrategySelector, viewLogsButton);
        loggingControls.setAlignment(Pos.CENTER);
        loggingControls.setPadding(new Insets(10, 0, 10, 0));
        loggingControls.setStyle("-fx-background-color: transparent;");

        // Organisation finale
        VBox mainControls = new VBox(8);
        mainControls.getChildren().setAll(controls);
        mainControls.setStyle(isDarkTheme ? GLASS_SIDEBAR_DARK : GLASS_SIDEBAR_LIGHT);
        mainControls.setPrefWidth(260);
        VBox.setVgrow(controls, Priority.ALWAYS);
        mainControls.getChildren().add(loggingControls);
        // Ajout du ScrollPane autour du panneau de gauche
        ScrollPane scrollPane = new ScrollPane(mainControls);
        scrollPane.setPrefWidth(260);
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
        setupEventHandlers(primaryStage, shapeSelector, colorPicker, strokeSizeSlider, shapeSizeSlider, rotationSlider,
                         themeToggle, scene);

        primaryStage.show();
    }

    private void setupEventHandlers(Stage primaryStage, ComboBox<String> shapeSelector, ColorPicker colorPicker,
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
        slider.setPrefWidth(150);
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
        
        // Couleurs plus vives pour les boutons
        String darkColor = color;
        String lightColor = color;
        
        // Ajuster les couleurs selon le thème
        switch (color) {
            case "#27ae60": // Vert
                darkColor = "#a6e3a1"; // Vert plus clair pour le thème sombre
                lightColor = "#40a02b"; // Vert plus foncé pour le thème clair
                break;
            case "#e74c3c": // Rouge
                darkColor = "#f38ba8"; // Rouge plus clair pour le thème sombre
                lightColor = "#d20f39"; // Rouge plus foncé pour le thème clair
                break;
            case "#3498db": // Bleu
                darkColor = "#89b4fa"; // Bleu plus clair pour le thème sombre
                lightColor = "#1e66f5"; // Bleu plus foncé pour le thème clair
                break;
        }
        
        String buttonColor = isDarkTheme ? darkColor : lightColor;
        button.setStyle((isDarkTheme ? DARK_BUTTON_STYLE : LIGHT_BUTTON_STYLE) + 
                        "-fx-background-color: " + buttonColor + ";");
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
            canvasObservable.notifyObservers("DRAW", null);
        }

        // Restaurer l'état du contexte graphique
        gc.restore();

        logAction("Dessin", "Forme : " + currentShape + ", Couleur : " + currentColor.toString());
    }

    private void updateTheme() {
        // Utiliser les variables existantes
        String buttonStyle = isDarkTheme ? DARK_BUTTON_STYLE : LIGHT_BUTTON_STYLE;
        String toolbarStyle = isDarkTheme ? DARK_TOOLBAR_STYLE : LIGHT_TOOLBAR_STYLE;
        String controlPanelStyle = isDarkTheme ? DARK_CONTROL_PANEL_STYLE : LIGHT_CONTROL_PANEL_STYLE;
        String rootStyle = isDarkTheme ? DARK_THEME : LIGHT_THEME;
        String sliderStyle = isDarkTheme ? DARK_SLIDER_STYLE : LIGHT_SLIDER_STYLE;
        String canvasStyle = isDarkTheme ? DARK_CANVAS_STYLE : LIGHT_CANVAS_STYLE;

        // Mise à jour des styles
        root.setStyle(rootStyle);
        controls.setStyle(controlPanelStyle);
        controlButtons.setStyle(toolbarStyle);
        graphControls.setStyle(controlPanelStyle);

        // Mise à jour des labels avec styles inline
        String labelStyle = isDarkTheme ? 
            """
            -fx-text-fill: #cdd6f4;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-padding: 5px 0;
            """ : 
            """
            -fx-text-fill: #4c4f69;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-padding: 5px 0;
            """;
            
        shapeLabel.setStyle(isDarkTheme ? MODERN_LABEL_DARK : MODERN_LABEL_LIGHT);
        colorLabel.setStyle(isDarkTheme ? MODERN_LABEL_DARK : MODERN_LABEL_LIGHT);
        strokeSizeLabel.setStyle(isDarkTheme ? MODERN_LABEL_DARK : MODERN_LABEL_LIGHT);
        shapeSizeLabel.setStyle(isDarkTheme ? MODERN_LABEL_DARK : MODERN_LABEL_LIGHT);
        rotationLabel.setStyle(isDarkTheme ? MODERN_LABEL_DARK : MODERN_LABEL_LIGHT);
        
        // Style de statut amélioré
        statusLabel.setStyle(isDarkTheme ? 
            """
            -fx-text-fill: #a6e3a1;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-color: #313244;
            -fx-padding: 8px;
            -fx-background-radius: 5px;
            """ : 
            """
            -fx-text-fill: #40a02b;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-color: #dce0e8;
            -fx-padding: 8px;
            -fx-background-radius: 5px;
            -fx-border-color: #bcc0cc;
            -fx-border-width: 1px;
            -fx-border-radius: 5px;
            """);
            
        modeIndicator.setStyle(labelStyle + "-fx-background-color: " + 
                              (isGraphMode ? "rgba(166,227,161,0.8)" : "rgba(205,214,244,0.8)") + 
                              "; -fx-padding: 8px; -fx-background-radius: 8px;");

        // Mise à jour de la case à cocher
        filledCheckBox.setStyle(isDarkTheme ? 
            """
            -fx-text-fill: #cdd6f4;
            -fx-font-size: 15px;
            -fx-background-color: transparent;
            -fx-padding: 5px 0;
            """ : 
            """
            -fx-text-fill: #4c4f69;
            -fx-font-size: 15px;
            -fx-background-color: transparent;
            -fx-padding: 5px 0;
            """);

        // Mise à jour des boutons
        updateButtonStyles(buttonStyle);

        // Mise à jour du canvas
        canvas.setStyle(canvasStyle);
        
        // Mise à jour des ComboBox et ColorPicker
        if (loggingStrategySelector != null) {
            loggingStrategySelector.setStyle(isDarkTheme ? MODERN_COMBOBOX_DARK : MODERN_COMBOBOX_LIGHT);
        }

        canvasObservable.notifyObservers("THEME_CHANGED", isDarkTheme);
    }

    private void updateButtonStyles(String baseStyle) {
        // Mise à jour des styles des boutons
        drawButton.setStyle(baseStyle + "-fx-background-color: " + (isDarkTheme ? "#27ae60" : "#2ecc71") + ";");
        clearButton.setStyle(baseStyle + "-fx-background-color: " + (isDarkTheme ? "#e74c3c" : "#e74c3c") + ";");
        undoButton.setStyle(baseStyle + "-fx-background-color: " + (isDarkTheme ? "#3498db" : "#3498db") + ";");
        redoButton.setStyle(baseStyle + "-fx-background-color: " + (isDarkTheme ? "#3498db" : "#3498db") + ";");
        saveButton.setStyle(baseStyle + "-fx-background-color: " + (isDarkTheme ? "#27ae60" : "#2ecc71") + ";");
        loadButton.setStyle(baseStyle + "-fx-background-color: " + (isDarkTheme ? "#27ae60" : "#2ecc71") + ";");
        findPathButton.setStyle(baseStyle + "-fx-background-color: " + (isDarkTheme ? "#27ae60" : "#2ecc71") + ";");
        addEdgeButton.setStyle(baseStyle + "-fx-background-color: " + (isDarkTheme ? "#27ae60" : "#2ecc71") + ";");
        viewLogsButton.setStyle(baseStyle + "-fx-background-color: " + (isDarkTheme ? "#27ae60" : "#2ecc71") + ";");
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

        Button closeButton = createStyledButton("Fermer", "#e74c3c", "❌");
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
        canvasObservable.notifyObservers("CLEAR", null);

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
}
