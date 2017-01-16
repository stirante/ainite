package com.stirante.ainite.ui;

import com.sun.javafx.tk.Toolkit;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

/**
 * Created by stirante
 */
public class ProcessingRenderer {

    private static final Color BG = Color.BLACK;
    private static final Color FG = Color.WHITE;
    private static final Color GOOD = Color.GREEN;
    private static final Color BAD = Color.RED;
    private static final double NODE_SIZE = 10D;
    private Phase phase = Phase.CALCULATE;
    private double value = 0d;
    private double x = 0d;
    private double y = 250;
    private double zoom = 1.5d;


    /**
     * @param t the current time (or position) of the tween. This can be seconds or frames, steps, seconds, ms, whatever – as long as the unit is the same as is used for the total time
     * @param b the beginning value of the property
     * @param c the change between the beginning and destination value of the property
     * @param d the total time of the tween
     */
    private static double easeOut(double t, double b, double c, double d) {
        return (t == d) ? b + c : c * (-(float) Math.pow(2, -10 * t / d) + 1) + b;
    }

    public void update(Canvas c, ProcessingData data) {
        Edge compare = new Edge("", "");
        int level = 1;
        double startX = c.getWidth() / 8D;
        double stepX = (c.getWidth() - (2D * startX)) / ((double) data.inputs.size());
        double stepY = 150D;
        for (int i = 0; i < data.inputs.size(); i++) {
            ProcessingNode node = data.inputs.get(i);
            node.x = i * stepX + startX + (stepX / 2D);
            node.y = level * stepY;
        }
        level++;
        stepX = (c.getWidth() - (2D * startX)) / ((double) data.calculates.size());
        for (int i = 0; i < data.calculates.size(); i++) {
            ProcessingNode node = data.calculates.get(i);
            node.x = i * stepX + startX + (stepX / 2D);
            node.y = level * stepY;
            compare.to = node.name;
            for (ProcessingNode input : data.inputs) {
                compare.from = input.name;
                if (data.fromInput.contains(compare)) {
                    node.from.add(input);
                }
            }
        }
        level++;
        stepX = (c.getWidth() - (2D * startX)) / ((double) data.models.size());
        for (int i = 0; i < data.models.size(); i++) {
            ProcessingNode node = data.models.get(i);
            node.x = i * stepX + startX + (stepX / 2D);
            node.y = level * stepY;
            compare.to = node.name;
            for (ProcessingNode input : data.inputs) {
                compare.from = input.name;
                if (data.fromInput.contains(compare)) {
                    node.from.add(input);
                }
            }
            for (ProcessingNode calculate : data.calculates) {
                compare.from = calculate.name;
                if (data.fromCalculate.contains(compare)) {
                    node.from.add(calculate);
                }
            }
        }
        level++;
        stepX = (c.getWidth() - (2D * startX)) / ((double) data.rules.size());
        for (int i = 0; i < data.rules.size(); i++) {
            ProcessingNode node = data.rules.get(i);
            node.x = i * stepX + startX + (stepX / 2D);
            node.y = level * stepY;
            compare.to = node.name;
            compare.toId = node.id;
            for (ProcessingNode model : data.models) {
                compare.from = model.name;
                if (data.fromModel.contains(compare)) {
                    node.from.add(model);
                }
            }
            for (ProcessingNode calculate : data.calculates) {
                compare.from = calculate.name;
                if (data.fromCalculate.contains(compare)) {
                    node.from.add(calculate);
                }
            }
            for (ProcessingNode input : data.inputs) {
                compare.from = input.name;
                if (data.fromInput.contains(compare)) {
                    node.from.add(input);
                }
            }
        }
    }

    public void render(Canvas c, ProcessingData data) {
        value += 1;
        update(c, data);
        GraphicsContext g = c.getGraphicsContext2D();
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setFill(BG);
        g.setStroke(BG);
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        g.setFill(FG);
        g.setStroke(FG);
        for (ProcessingNode node : data.inputs) {
            renderNode(g, node, null);
        }
        if (phase == Phase.CALCULATE)
            y = easeOut(value, 250, -100, 100D);
        if (phase == Phase.MODEL)
            y = easeOut(value, 150, -100, 100D);
        if (phase == Phase.RULE)
            y = easeOut(value, 50, -100, 100D);
        if (phase == Phase.ZOOM_OUT) {
            y = easeOut(value, -50, 50, 100D);
            zoom = easeOut(value, 1.5, -0.5, 100D);
            if (value >= 100) {
                value = 0;
                phase = Phase.END;
            }
        }
        if (phase.ordinal() >= Phase.CALCULATE.ordinal()) {
            for (ProcessingNode node : data.calculates) {
                renderNode(g, node, Phase.CALCULATE);
                if (value >= 100 || phase != Phase.CALCULATE) {
                    if (phase == Phase.CALCULATE) {
                        phase = Phase.MODEL;
                        value = 0;
                    }
                }
            }
        }
        if (phase.ordinal() >= Phase.MODEL.ordinal()) {
            for (ProcessingNode node : data.models) {
                renderNode(g, node, Phase.MODEL);
                if (value >= 100 || phase != Phase.MODEL) {
                    if (phase == Phase.MODEL) {
                        phase = Phase.RULE;
                        value = 0;
                    }
                }
            }
        }
        if (phase.ordinal() >= Phase.RULE.ordinal()) {
            for (ProcessingNode node : data.rules) {
                renderNode(g, node, Phase.RULE);
                if (value >= 100 || phase != Phase.RULE) {
                    if (phase == Phase.RULE) {
                        phase = Phase.ZOOM_OUT;
                        value = 0;
                    }
                }
            }
        }
        if (phase == Phase.END) value = 100;
    }

    private void renderNode(GraphicsContext g, ProcessingNode node, Phase notPhase) {
        if (value >= 100 || phase != notPhase) {
            for (ProcessingNode from : node.from) {
                if (from.boolType) {
                    if (from.state) g.setStroke(GOOD);
                    else g.setStroke(BAD);
                }
                g.strokeLine((from.x + x) * zoom + (g.getCanvas().getWidth() / 2) * (1 - zoom), (from.y + y) * zoom + (g.getCanvas().getHeight() / 2) * (1 - zoom), (node.x + x) * zoom + (g.getCanvas().getWidth() / 2) * (1 - zoom), (node.y + y) * zoom + (g.getCanvas().getHeight() / 2) * (1 - zoom));
                g.setStroke(FG);
            }
            if (node.boolType) {
                if (node.state) g.setFill(GOOD);
                else g.setFill(BAD);
            }
            String str = node.name + " = " + (node.boolType ? node.state : node.value);
            float width = Toolkit.getToolkit().getFontLoader().computeStringWidth(str, g.getFont()) + 10;
            float height = Toolkit.getToolkit().getFontLoader().getFontMetrics(g.getFont()).getLineHeight() + 10;
            g.fillOval((node.x - (NODE_SIZE / 2) + x) * zoom + (g.getCanvas().getWidth() / 2) * (1 - zoom), (node.y - (NODE_SIZE / 2) + y) * zoom + (g.getCanvas().getHeight() / 2) * (1 - zoom), NODE_SIZE * zoom, NODE_SIZE * zoom);
            g.setFill(FG);
            g.fillRect((node.x + x) * zoom + (g.getCanvas().getWidth() / 2) * (1 - zoom) - (width / 2), (node.y - (NODE_SIZE * 2) + y) * zoom + (g.getCanvas().getHeight() / 2) * (1 - zoom) - (height / 2), width, height);
            g.setFill(BG);
            g.fillText(str, (node.x + x) * zoom + (g.getCanvas().getWidth() / 2) * (1 - zoom), (node.y - (NODE_SIZE * 2) + y) * zoom + (g.getCanvas().getHeight() / 2) * (1 - zoom));
            g.setFill(FG);
        } else {
            for (ProcessingNode from : node.from) {
                if (from.boolType) {
                    if (from.state) g.setStroke(GOOD);
                    else g.setStroke(BAD);
                }
                double w = easeOut(value, from.x, node.x - from.x, 100D);
                double h = easeOut(value, from.y, node.y - from.y, 100D);
                g.strokeLine((from.x + x) * zoom + (g.getCanvas().getWidth() / 2) * (1 - zoom), (from.y + y) * zoom + (g.getCanvas().getHeight() / 2) * (1 - zoom), (w + x) * zoom + (g.getCanvas().getWidth() / 2) * (1 - zoom), (h + y) * zoom + (g.getCanvas().getHeight() / 2) * (1 - zoom));
                g.setStroke(FG);
            }
        }
    }

    private enum Phase {
        CALCULATE, MODEL, RULE, ZOOM_OUT, END
    }

    public static class ProcessingData {
        public ArrayList<ProcessingNode> inputs = new ArrayList<>();
        public ArrayList<ProcessingNode> calculates = new ArrayList<>();
        public ArrayList<ProcessingNode> models = new ArrayList<>();
        public ArrayList<ProcessingNode> rules = new ArrayList<>();

        public ArrayList<Edge> fromInput = new ArrayList<>();
        public ArrayList<Edge> fromCalculate = new ArrayList<>();
        public ArrayList<Edge> fromModel = new ArrayList<>();

    }

    public static class ProcessingNode {
        public int id = -1;
        public String name;
        public float value;
        public boolean state;
        public boolean boolType;
        double x;
        double y;
        ArrayList<ProcessingNode> from = new ArrayList<>();

        public ProcessingNode(String name, float value) {
            this.name = name;
            this.value = value;
            boolType = false;
        }

        public ProcessingNode(String name, boolean state) {
            this.name = name;
            this.state = state;
            boolType = true;
        }
    }

    public static class Edge {
        public int toId = -1;
        public String from;
        public String to;
        public boolean hasValue;
        public boolean value;

        public Edge(String from, String to, boolean value) {
            this.from = from;
            this.to = to;
            this.value = value;
            hasValue = true;
        }

        public Edge(String from, String to) {
            this.from = from;
            this.to = to;
            hasValue = false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge edge = (Edge) o;

            if (toId != edge.toId) return false;
            if (from != null ? !from.equals(edge.from) : edge.from != null) return false;
            return to != null ? to.equals(edge.to) : edge.to == null;
        }

        @Override
        public int hashCode() {
            int result = toId;
            result = 31 * result + (from != null ? from.hashCode() : 0);
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }

}
