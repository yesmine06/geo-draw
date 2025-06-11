package masi.s2.observer;

public class CanvasEvent {
    private final EventType type;
    private final Object data;

    public CanvasEvent(EventType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public EventType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public enum EventType {
        DRAW,
        CLEAR,
        THEME_CHANGED,
        SHAPE_SELECTED,
        COLOR_CHANGED,
        STROKE_WIDTH_CHANGED,
        ROTATION_CHANGED,
        FILL_CHANGED
    }
} 