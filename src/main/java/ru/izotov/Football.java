package ru.izotov;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;


public class Football {

    static final int CELL = 50;
    static final int FIELD_HEIGHT = CELL * 15;
    static final int FIELD_WIDTH = CELL * 10;
    static final int BALL_SIZE = 20;
    static final int BALL_RADIUS = BALL_SIZE / 2;
    static final int FIELD_CENTER_X = FIELD_WIDTH / 2;
    static final int FIELD_CENTER_Y = FIELD_HEIGHT / 2 - CELL / 2;
    // Клавиши управления
    static final int LEFT = 37;
    static final int UP = 38;
    static final int RIGHT = 39;
    static final int DOWN = 40;
    static final int SHOT = 32;          // space
    // Горячие клавиши
    static final int MOVE_BACK = 8;      // backspace
    static final int BOT_MOVE = 10;      // enter
    static final int RESTART_GAME = 113; // F2
    static final int CLEAR_FIELD = 114;  // F3
    static final int RESET_SCORE = 115;  // F4
    static final int SHOW_GRID = 118;    // F7
    static final int HIDE_GRID = 119;    // F8
    static final int GET_LINES = 121;    // F10
    static final int GET_POINTS = 122;    // F11
    static final int GET_XY = 123;        // F12 - координаты мяча

    static final Color DEFAULT_COLOR = Color.black;
    static final Color COLOR_FIELD = new Color(0, 220, 0);
    static final Color COLOR_P_1 = new Color(0, 0, 220);
    static final Color COLOR_P_2 = new Color(220, 0, 0);

    int fieldLinesCount;
    int key = 0;  // нажатая клавиша
    int countColor = 2;  // счетчик смены цвета
    int changeColor = 0; // счетчик перехода хода
    int redPlayerGoals = 0;
    int bluePlayerGoals = 0;
    boolean showGrid = false; // индикатор отображения сетки

    JFrame frame;
    JMenuBar menuBar;
    Canvas canvas;
    Ball ball;
    Line line;
    Point point;
    FootballField footballField;
    Bot bot;

    String score = "   синий  " + redPlayerGoals + " : " + bluePlayerGoals + "  красный";
    JLabel scoreLabel = new JLabel(score, SwingConstants.CENTER);
    JPanel panel = new JPanel();

    Color currentColor = COLOR_P_1; // цвет мяча и линий (меняется при переходе хода, начинает синий игрок)

    // Линии ворот
    Point gateRed1 = new Point(CELL * 4, CELL * 1);
    Point gateRed2 = new Point(CELL * 5, CELL * 1);
    Point gateRed3 = new Point(CELL * 6, CELL * 1);
    Point gateBlue1 = new Point(CELL * 4, CELL * 13);
    Point gateBlue2 = new Point(CELL * 5, CELL * 13);
    Point gateBlue3 = new Point(CELL * 6, CELL * 13);

    List<Line> lines = new ArrayList<>();   // Линии поля
    List<Point> points = new ArrayList<>(); // Точки поля

    void start() {
        frame = new JFrame("Football");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH + 19, FIELD_HEIGHT);
        frame.setLocation(2100, 200);
        frame.setResizable(false);
        footballField = new FootballField();
        footballField.addPointToList();
        canvas = new Canvas();
        canvas.setBackground(COLOR_FIELD);
        frame.getContentPane().add(BorderLayout.CENTER, canvas);
        scoreLabel.setFont(new Font("Arial", Font.TRUETYPE_FONT, 22));
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        panel.add(scoreLabel);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                key = e.getKeyCode();
//                System.out.println(key);
                ball.move();
                if (key == BOT_MOVE) {
                    bot.doShot();
                }
            }
        });
        frame.setVisible(true);
        startGame();
    }

    void startGame() {
        bot = new Bot();
        ball = new Ball(FIELD_CENTER_X, FIELD_CENTER_Y);
        line = new Line();
        point = new Point();
        line.setLine();   // добавление линий поля в массив
        point.setPoint(); // добавление точек поля в массив
    }

    // Новая игра
    void restartGame() {
        lines.clear();
        points.clear();
        resetScore();
        startGame();
        canvas.repaint();
    }

    // Очистка поля
    void clearField() {
        lines.clear();
        points.clear();
        startGame();
        canvas.repaint();
    }

    // Сброс счета
    void resetScore() {
        redPlayerGoals = 0;
        bluePlayerGoals = 0;
        score = "   синий  " + bluePlayerGoals + " : " + redPlayerGoals + "  красный";
        scoreLabel.setText(score);
    }

    void onScreenPoints() {
        for (int i = 100; i < points.size(); i++) {
            System.out.print(points.get(i).toString() + " | ");
            if (i % 15 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    private void onScreenLines() {
        for (int i = 50; i < lines.size(); i++) {
            System.out.print(lines.get(i).toString() + " | ");
            if (i % 8 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    private void onScreenLines(List<Line> l) {
        for (int i = 0; i < l.size(); i++) {
            System.out.print(l.get(i).toString() + " | ");
            if (i % 8 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    // Проверка наличия линий на поле
    public boolean checkLine(int x1, int x2, int y1, int y2) {
        boolean checkLine = false;
        for (Line l : lines) {
            if ((l.getX1() == x1 && l.getY1() == y1 && l.getX2() == x2 && l.getY2() == y2)
                    || (l.getX1() == x2 && l.getY1() == y2 && l.getX2() == x1 && l.getY2() == y1)) {
                checkLine = true;
            }
        }
        return checkLine;
    }

    /**
     * Футбольное поле
     */
    class FootballField {
        // Отрисовка сетки поля
        void addGrid(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.gray);
            // Сетка поля
            // Горизонтальные линии
            g2.drawLine(CELL * 1, CELL * 2, CELL * 9, CELL * 2);
            g2.drawLine(CELL * 1, CELL * 3, CELL * 9, CELL * 3);
            g2.drawLine(CELL * 1, CELL * 4, CELL * 9, CELL * 4);
            g2.drawLine(CELL * 1, CELL * 5, CELL * 9, CELL * 5);
            g2.drawLine(CELL * 1, CELL * 6, CELL * 9, CELL * 6);
            g2.drawLine(CELL * 1, CELL * 8, CELL * 9, CELL * 8);
            g2.drawLine(CELL * 1, CELL * 9, CELL * 9, CELL * 9);
            g2.drawLine(CELL * 1, CELL * 10, CELL * 9, CELL * 10);
            g2.drawLine(CELL * 1, CELL * 11, CELL * 9, CELL * 11);
            g2.drawLine(CELL * 1, CELL * 12, CELL * 9, CELL * 12);
            // Вертикальные линии
            g2.drawLine(CELL * 2, CELL * 2, CELL * 2, CELL * 12);
            g2.drawLine(CELL * 3, CELL * 2, CELL * 3, CELL * 12);
            g2.drawLine(CELL * 4, CELL * 2, CELL * 4, CELL * 12);
            g2.drawLine(CELL * 5, CELL * 2, CELL * 5, CELL * 12);
            g2.drawLine(CELL * 6, CELL * 2, CELL * 6, CELL * 12);
            g2.drawLine(CELL * 7, CELL * 2, CELL * 7, CELL * 12);
            g2.drawLine(CELL * 8, CELL * 2, CELL * 8, CELL * 12);
        }

        // Добавление границ поля и центральной линии в lines.
        void addLineToList() {
            lines.add(new Line(CELL * 1, CELL * 2, CELL * 2, CELL * 2, DEFAULT_COLOR));
            lines.add(new Line(CELL * 2, CELL * 2, CELL * 3, CELL * 2, DEFAULT_COLOR));
            lines.add(new Line(CELL * 3, CELL * 2, CELL * 4, CELL * 2, DEFAULT_COLOR));
            // Верхние ворота - красный игрок
            lines.add(new Line(CELL * 4, CELL * 2, CELL * 4, CELL * 1, COLOR_P_2));
            lines.add(new Line(CELL * 4, CELL * 1, CELL * 5, CELL * 1, COLOR_P_2));
            lines.add(new Line(CELL * 5, CELL * 1, CELL * 6, CELL * 1, COLOR_P_2));
            lines.add(new Line(CELL * 6, CELL * 1, CELL * 6, CELL * 2, COLOR_P_2));
            // Кромка поля
            lines.add(new Line(CELL * 6, CELL * 2, CELL * 7, CELL * 2, DEFAULT_COLOR));
            lines.add(new Line(CELL * 7, CELL * 2, CELL * 8, CELL * 2, DEFAULT_COLOR));
            lines.add(new Line(CELL * 8, CELL * 2, CELL * 9, CELL * 2, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 2, CELL * 9, CELL * 3, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 3, CELL * 9, CELL * 4, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 4, CELL * 9, CELL * 5, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 5, CELL * 9, CELL * 6, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 6, CELL * 9, CELL * 7, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 7, CELL * 9, CELL * 8, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 8, CELL * 9, CELL * 9, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 9, CELL * 9, CELL * 10, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 10, CELL * 9, CELL * 11, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 11, CELL * 9, CELL * 12, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 11, CELL * 9, CELL * 12, DEFAULT_COLOR));
            lines.add(new Line(CELL * 9, CELL * 12, CELL * 8, CELL * 12, DEFAULT_COLOR));
            lines.add(new Line(CELL * 8, CELL * 12, CELL * 7, CELL * 12, DEFAULT_COLOR));
            lines.add(new Line(CELL * 7, CELL * 12, CELL * 6, CELL * 12, DEFAULT_COLOR));
            // Нижние ворота - синий игркок
            lines.add(new Line(CELL * 6, CELL * 12, CELL * 6, CELL * 13, COLOR_P_1));
            lines.add(new Line(CELL * 6, CELL * 13, CELL * 5, CELL * 13, COLOR_P_1));
            lines.add(new Line(CELL * 5, CELL * 13, CELL * 4, CELL * 13, COLOR_P_1));
            lines.add(new Line(CELL * 4, CELL * 13, CELL * 4, CELL * 12, COLOR_P_1));
            // Кромка поля
            lines.add(new Line(CELL * 4, CELL * 12, CELL * 3, CELL * 12, DEFAULT_COLOR));
            lines.add(new Line(CELL * 3, CELL * 12, CELL * 2, CELL * 12, DEFAULT_COLOR));
            lines.add(new Line(CELL * 2, CELL * 12, CELL * 1, CELL * 12, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 12, CELL * 1, CELL * 11, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 11, CELL * 1, CELL * 10, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 10, CELL * 1, CELL * 9, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 9, CELL * 1, CELL * 8, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 8, CELL * 1, CELL * 7, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 7, CELL * 1, CELL * 6, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 6, CELL * 1, CELL * 5, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 5, CELL * 1, CELL * 4, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 4, CELL * 1, CELL * 3, DEFAULT_COLOR));
            lines.add(new Line(CELL * 1, CELL * 3, CELL * 1, CELL * 2, DEFAULT_COLOR));
            // Средняя линия
            lines.add(new Line(CELL * 1, CELL * 7, CELL * 2, CELL * 7, DEFAULT_COLOR));
            lines.add(new Line(CELL * 2, CELL * 7, CELL * 3, CELL * 7, DEFAULT_COLOR));
            lines.add(new Line(CELL * 3, CELL * 7, CELL * 4, CELL * 7, DEFAULT_COLOR));
            lines.add(new Line(CELL * 4, CELL * 7, CELL * 5, CELL * 7, DEFAULT_COLOR));
            lines.add(new Line(CELL * 5, CELL * 7, CELL * 6, CELL * 7, DEFAULT_COLOR));
            lines.add(new Line(CELL * 6, CELL * 7, CELL * 7, CELL * 7, DEFAULT_COLOR));
            lines.add(new Line(CELL * 7, CELL * 7, CELL * 8, CELL * 7, DEFAULT_COLOR));
            lines.add(new Line(CELL * 8, CELL * 7, CELL * 9, CELL * 7, DEFAULT_COLOR));
            // Начальная линия нулевой длины (центр поля)
            lines.add(new Line((FIELD_CENTER_X), (FIELD_CENTER_Y),
                    (FIELD_CENTER_X), (FIELD_CENTER_Y), DEFAULT_COLOR));
            fieldLinesCount = lines.size(); // отсчека точек на поле
        }

        void addPointToList() {
            // Граничные точки
            points.add(new Point(CELL * 1, CELL * 2));
            points.add(new Point(CELL * 2, CELL * 2));
            points.add(new Point(CELL * 3, CELL * 2));
            points.add(new Point(CELL * 4, CELL * 2));
            // Верхние ворота (красные) - - - - - - - - - - - - - -
            points.add(gateRed1);
            points.add(gateRed2);
            points.add(gateRed3);
            //  - - - - - - - - - - - - - - - - - - - - - - - - - -
            points.add(new Point(CELL * 6, CELL * 2));
            points.add(new Point(CELL * 7, CELL * 2));
            points.add(new Point(CELL * 8, CELL * 2));
            points.add(new Point(CELL * 9, CELL * 2));
            points.add(new Point(CELL * 9, CELL * 3));
            points.add(new Point(CELL * 9, CELL * 4));
            points.add(new Point(CELL * 9, CELL * 5));
            points.add(new Point(CELL * 9, CELL * 6));
            points.add(new Point(CELL * 9, CELL * 7));
            points.add(new Point(CELL * 9, CELL * 8));
            points.add(new Point(CELL * 9, CELL * 9));
            points.add(new Point(CELL * 9, CELL * 10));
            points.add(new Point(CELL * 9, CELL * 11));
            points.add(new Point(CELL * 9, CELL * 11));
            points.add(new Point(CELL * 9, CELL * 12));
            points.add(new Point(CELL * 8, CELL * 12));
            points.add(new Point(CELL * 7, CELL * 12));
            points.add(new Point(CELL * 6, CELL * 12));
            // Нижние ворота (синие) - - - - - - - - - - - - - - - -
            points.add(gateBlue1);
            points.add(gateBlue2);
            points.add(gateBlue3);
            // - - - - - - - - - - - - - - - - - - - - - - - - - - -
            points.add(new Point(CELL * 4, CELL * 12));
            points.add(new Point(CELL * 3, CELL * 12));
            points.add(new Point(CELL * 2, CELL * 12));
            points.add(new Point(CELL * 1, CELL * 12));
            points.add(new Point(CELL * 1, CELL * 11));
            points.add(new Point(CELL * 1, CELL * 10));
            points.add(new Point(CELL * 1, CELL * 9));
            points.add(new Point(CELL * 1, CELL * 8));
            points.add(new Point(CELL * 1, CELL * 7));
            points.add(new Point(CELL * 1, CELL * 6));
            points.add(new Point(CELL * 1, CELL * 5));
            points.add(new Point(CELL * 1, CELL * 4));
            points.add(new Point(CELL * 1, CELL * 3));
            // Средняя линия
            points.add(new Point(CELL * 1, CELL * 7));
            points.add(new Point(CELL * 2, CELL * 7));
            points.add(new Point(CELL * 3, CELL * 7));
            points.add(new Point(CELL * 4, CELL * 7));
            points.add(new Point(CELL * 5, CELL * 7));
            points.add(new Point(CELL * 6, CELL * 7));
            points.add(new Point(CELL * 7, CELL * 7));
            points.add(new Point(CELL * 8, CELL * 7));
            // Стартовая точка мяча (центр поля)
            points.add(new Point(FIELD_CENTER_X, FIELD_CENTER_Y));
        }

    }


    /**
     * Мяч
     */
    class Ball {
        private static final int LEFT_BORDER = CELL;
        private static final int RIGHT_BORDER = CELL * 9;
        private static final int TOP_BORDER = CELL * 2;
        private static final int BOTTOM_BORDER = CELL * 12;
        private static final int LEFT_BORDER_GATE = CELL * 4;
        private static final int RIGHT_BORDER_GATE = CELL * 6;
        private static final int TOP_BORDER_GATE = CELL;
        private static final int BOTTOM_BORDER_GATE = CELL * 13;
        private int x;
        private int y;

        Ball(int x, int y) {
            this.setXY(x, y);
        }

        void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        void drawBall(Graphics g, Color color) {
            g.setColor(color);
            g.fillOval(x - BALL_RADIUS, y - BALL_RADIUS, BALL_SIZE, BALL_SIZE);
        }

        void move() {
            boolean xPoint = (x == CELL * 4 && y == CELL)
                    || (x == CELL * 6 && y == CELL)
                    || (x == CELL * 4 && y == CELL * 13)
                    || (x == CELL * 6 && y == CELL * 13);
//            boolean rightGateBorder = ;
//            boolean leftGateBorder;
            boolean borderPoint = (x == CELL * 4 && y == CELL * 2)
                    || (x == CELL * 5 && y == CELL * 2)
                    || (x == CELL * 6 && y == CELL * 2)
                    || (x == CELL * 4 && y == CELL * 12)
                    || (x == CELL * 5 && y == CELL * 12)
                    || (x == CELL * 6 && y == CELL * 12);
            if (key == LEFT && (x > LEFT_BORDER && !xPoint)) {
                x = x - CELL;
            }
            if (key == RIGHT && (x < RIGHT_BORDER && !xPoint)) {
                x = x + CELL;
            }
            if (key == UP && (y > TOP_BORDER || borderPoint)) {
                y = y - CELL;
            }
            if (key == DOWN && (y < BOTTOM_BORDER || borderPoint)) {
                y = y + CELL;
            }
            canvas.repaint();
        }

        // Гол забит
        void goal(int i) {
            // В чьи ворота забили?
            if (i == 1) {
//                JOptionPane.showMessageDialog(frame, "ГОЛ в ворота красного игрока", "GOAL", 1);
                JOptionPane.showMessageDialog(frame, "", "GOAL", INFORMATION_MESSAGE);
                currentColor = COLOR_P_2; // Красный игрок разводит мяч
                bluePlayerGoals++;
                score = "   синий  " + bluePlayerGoals + " : " + redPlayerGoals + "  красный";
            }
            if (i == 2) {
//                JOptionPane.showMessageDialog(frame, "ГОЛ в ворота синего игрока");
                JOptionPane.showMessageDialog(frame, "", "GOAL", INFORMATION_MESSAGE);
                currentColor = COLOR_P_1; //Синий огрок разводит мяч
                redPlayerGoals++;
                score = "   синий  " + bluePlayerGoals + " : " + redPlayerGoals + "  красный";
            }
            clearField(); // перезапуск игры
        }

        void changeColor() {
            changeColor++;
            if (changeColor % 2 == 0) currentColor = COLOR_P_1;
            if (changeColor % 2 == 1) currentColor = COLOR_P_2;
        }

    }

    /**
     * Класс для создания точек, в узлах сетки при ударе мяча
     */
    class Point {
        private int x;
        private int y;

        Point() {
        }

        Point(int x, int y) {
            this.setXY(x, y);
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void setPoint() {
            footballField.addPointToList();
        }

        void addNewPoint(int x2, int y2) {
            points.add(new Point(x2, y2));
        }

        void drawPoints(Graphics g) {
            g.fillOval(x - BALL_RADIUS / 2, y - BALL_RADIUS / 2, BALL_RADIUS, BALL_RADIUS);
        }

        void drawPoints(Graphics g, Color color) {
            g.setColor(color);
            g.fillOval(x - BALL_RADIUS / 2, y - BALL_RADIUS / 2, BALL_RADIUS, BALL_RADIUS);
        }

        @Override
        public String toString() {
            return "x=" + x + " y=" + y;
        }
    }

    /**
     * Класс для создания линий
     */
    class Line {
        private int x1;
        private int x2;
        private int y1;
        private int y2;
        private Color colorLine;

        Line() {
        }

        Line(int x1, int y1, int x2, int y2, Color colorLine) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.colorLine = colorLine;
        }

        int getX1() {
            return x1;
        }

        int getY1() {
            return y1;
        }

        int getX2() {
            return x2;
        }

        int getY2() {
            return y2;
        }

        Color getColor() {
            return colorLine;
        }

        // Добавление линий в массив с начальными и конечными координатами, и цветом
        void addNewLine(int x1, int y1, int x2, int y2, Color colorLine) {
            lines.add(new Line(x1, y1, x2, y2, colorLine));
        }

        void setLine() {
            // Убираем тупикове ходы
            lines.add(new Line(2 * CELL, 3 * CELL, 1 * CELL, 2 * CELL, COLOR_FIELD));
            lines.add(new Line(2 * CELL, 11 * CELL, 1 * CELL, 12 * CELL, COLOR_FIELD));
            lines.add(new Line(8 * CELL, 3 * CELL, 9 * CELL, 2 * CELL, COLOR_FIELD));
            lines.add(new Line(8 * CELL, 11 * CELL, 9 * CELL, 12 * CELL, COLOR_FIELD));
            // Добавлем линии поля
            footballField.addLineToList();
        }

        /*        boolean isNotAngle(int x2, int y2) {
                    final int LT_X = CELL;
                    final int LT_Y = 2 * CELL;
                    final int RT_X = 9 * CELL;
                    final int RT_Y = 2 * CELL;
                    final int RB_X = 9 * CELL;
                    final int RB_Y = 12 * CELL;
                    final int LB_X = CELL;
                    final int LB_Y = 12 * CELL;
                    if ((x2 == LT_X && y2 == LT_Y)
                            || (x2 == LB_X && y2 == LB_Y)
                            || (x2 == RB_X && y2 == RB_Y)
                            || (x2 == RT_X && y2 == RT_Y)) {
                        return false;
                    } else {
                        return true;
                    }
                }*/

        // Отрисовка линий на поле.
        void drawLines(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            int i = 0;
            for (Line l : lines) {
                i++;
                g2.setColor(l.getColor());
                g2.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
                // Не рисуем точки на линиях поля
                if (i >= fieldLinesCount) {
                    new Point(l.getX2(), l.getY2()).drawPoints(g);
                }
            }
            // Текущее место положения мяча на поле
            new Point(lines.get(lines.size() - 1).getX2(),
                    lines.get(lines.size() - 1).getY2()).
                    drawPoints(g, new Color(255, 255, 255));
        }

        @Override
        public String toString() {
            return "x=1" + x1 + " x2=" + x2 + " y1=" + y1 + " y2=" + y2;
        }
    }

    /**
     * Bot
     */
    class Bot {
        void doShot() {
            List<Line> possibleShots = new ArrayList<>();
            int x = ball.getX();
            int y = ball.getY();
            int x2 = x - CELL;
            int x3 = x + CELL;
            int y2 = y - CELL;
            int y3 = y + CELL;
            // Возможные удары
            if (x2 >= CELL && y3 <= 12 * CELL)
                possibleShots.add(new Line(x, y, x2, y3, COLOR_P_1));
            if (x3 <= 9 * CELL && y3 <= 12 * CELL)
                possibleShots.add(new Line(x, y, x3, y3, COLOR_P_1));
            if (x3 <= 9 * CELL && y2 >= 2 * CELL)
                possibleShots.add(new Line(x, y, x3, y2, COLOR_P_1));
            if (x2 >= CELL && y2 >= 2 * CELL)
                possibleShots.add(new Line(x, y, x2, y2, COLOR_P_1));
            if (x2 >= CELL)
                possibleShots.add(new Line(x, y, x2, y, COLOR_P_1));
            if (x3 <= 9 * CELL)
                possibleShots.add(new Line(x, y, x3, y, COLOR_P_1));
            if (y2 >= 2 * CELL)
                possibleShots.add(new Line(x, y, x, y2, COLOR_P_1));
            if (y3 <= 12 * CELL)
                possibleShots.add(new Line(x, y, x, y3, COLOR_P_1));

            System.out.println("possibleShots: " + possibleShots.size());
//            onScreenLines(possibleShots);
//            System.out.println("lines: " + lines.size());
//            onScreenLines(lines);
            // Допустимые удары
            List<Line> allowedShots = new ArrayList<>();
            for (Line l : possibleShots) {
                if (!checkLine(l.getX1(), l.getX2(), l.getY1(), l.getY2())) {
                    allowedShots.add(l);
                }
            }
            System.out.println("allowedShots: " + allowedShots.size());
//            onScreenLines(allowedShots);
            // Выбор опитмального удара
            int minDX = Integer.MAX_VALUE;
            int minDY = Integer.MAX_VALUE;
            int dx, dy;
/*            for (int i = 0; i < allowedShots.size(); i++) {
//                Math.abs()
            }*/
            Line shot = allowedShots.get(new Random().nextInt(allowedShots.size()));
//            Line shot = allowedShots.get(0);
            lines.add(shot);
            ball.setXY(shot.getX2(), shot.getY2());
            canvas.repaint();
        }

/*        boolean isNotAngle(int x2, int x3, int y2, int y3) {
            final int LT_X = CELL;
            final int LT_Y = 2 * CELL;
            final int RT_X = 9 * CELL;
            final int RT_Y = 2 * CELL;
            final int RB_X = 9 * CELL;
            final int RB_Y = 12 * CELL;
            final int LB_X = CELL;
            final int LB_Y = 12 * CELL;

            if (x2 == LT_X || x2 == LB_X || x2) {
                return true;
            }
        }*/
    }


    /**
     * Реализация графики и логики игры
     */
    public class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int x1, x2, y1, y2;
            countColor = 0; // счетчик для смены цвета при переходе хода.
            ball.drawBall(g, currentColor);
            scoreLabel.setText(score);
            line.drawLines(g);
            // Управление горячими клавишами
            if (key == HIDE_GRID) showGrid = false; // скрыть
            if (key == SHOW_GRID) showGrid = true;  // показать
            if (showGrid) footballField.addGrid(g); // сетку
            if (key == RESTART_GAME) restartGame(); // перезапуск игры
            if (key == CLEAR_FIELD) clearField();   // очистка поля
            if (key == RESET_SCORE) resetScore();   // сброс счета
            if (key == GET_XY) System.out.println(ball.getX()/CELL + "*CELL " + ball.getY()/CELL + "*CELL");
            if (key == GET_LINES) onScreenLines(); // показать существующие точки (кроме поля)
            if (key == GET_POINTS) onScreenPoints(); // показать существующие точки (кроме поля)
            // Действия при выполнении удара (нажатии "space")
            if (key == SHOT) {
                x1 = lines.get(lines.size() - 1).getX2();
                y1 = lines.get(lines.size() - 1).getY2();
                x2 = ball.getX();
                y2 = ball.getY();
                // Проверка точек в массиве. Если точка существует на поле, можно и нужно сделать ещё удар.
                for (Point p : points) {
                    if (((ball.getX()) == p.getX()) && ((ball.getY()) == p.getY())) {
                        countColor++; // счетчик для перехода хода со сменой цвета
                    }
                }
                if ((Math.abs(x1 - x2) == CELL && Math.abs(y1 - y2) == CELL)
                        || (Math.abs(x1 - x2) == 50 && (Math.abs(y1 - y2) == 0))
                        || (Math.abs(x1 - x2) == 0 && (Math.abs(y1 - y2) == CELL))) {
                    if (!checkLine(x1, x2, y1, y2)) {
                        point.addNewPoint(x2, y2); // в случае возможности удара, доавляем новую точку
                        line.addNewLine(x1, y1, x2, y2, currentColor); // добавляем новую линию
                        if (countColor == 0) {
                            ball.changeColor(); // меняем цвет при переходе хода
                            ball.drawBall(g, currentColor); // рисуем мяч нового цвета
                        }
                    } else {
                        System.out.println("Повторный ход недопустим");
                    }
                } else {
                    System.out.println("Удар такой длины недопустим");
                }
                line.drawLines(g); // рисуем линии ходов.
                // Проверка: забит ли ГОЛ!? (при достижении мячом точек ворот - гол.
                if ((lines.get(lines.size() - 1).getX2() == gateRed1.getX()
                        || lines.get(lines.size() - 1).getX2() == gateRed2.getX()
                        || lines.get(lines.size() - 1).getX2() == gateRed3.getX())
                        && (lines.get(lines.size() - 1).getY2() == gateRed1.getY())) {
                    ball.goal(1); // Гол в ворота "красного игрока"
                    canvas.repaint();
                }
                if ((lines.get(lines.size() - 1).getX2() == gateBlue1.getX()
                        || lines.get(lines.size() - 1).getX2() == gateBlue2.getX()
                        || lines.get(lines.size() - 1).getX2() == gateBlue3.getX())
                        && (lines.get(lines.size() - 1).getY2() == gateBlue1.getY())) {
                    ball.goal(2); // Гол в ворота "синего игрока"
                    canvas.repaint();
                }
                /*new Point(lines.get(lines.size()-1).getX2(),
                          lines.get(lines.size()-1).getY2()).
                          drawPoints(g, new Color(200, 200, 0));*/
            }
        }
    }
}