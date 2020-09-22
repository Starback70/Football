package ru.izotov;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;


public class Football {

    static final int CELL = 50;
    static final int FIELD_HEIGHT = CELL * 15;
    static final int FIELD_WIDTH = CELL * 10;
    static final int BALL = 16;
    static final int BALL_RADIUS = BALL / 2;
    static final int FIELD_CENTER_X = FIELD_WIDTH / 2 - BALL_RADIUS;
    static final int FIELD_CENTER_Y = FIELD_HEIGHT / 2 - CELL / 2 - BALL_RADIUS;
    // Клавиши управления
    static final int LEFT = 37;
    static final int UP = 38;
    static final int RIGHT = 39;
    static final int DOWN = 40;
    static final int SHOT = 32;       // space
    // Горячие клавиши
    static final int RESTART_GAME = 113; // F2
    static final int CLEAR_FIELD = 114;  // F3
    static final int RESET_SCORE = 115;  // F4
    static final int SHOW_GRID = 118;    // F7
    static final int HIDE_GRID = 119;    // F8
    static final int getXY = 123;        // F12 - координаты мяча

    static final Color DEFAULT_COLOR = Color.black;

    boolean showGrid = false; // индикатор отображения сетки
    int key = 0;  // нажатая клавиша
    int countColor = 2;  // счетчик смены цвета
    int changeColor = 0; // счетчик перехода хода
    int redPlayerGoals = 0;
    int bluePlayerGoals = 0;

    JFrame frame;
    Canvas canvas;
    Ball ball;
    Line line;
    Point point;
    FootballField footballField;

    String score = "   синий  " + redPlayerGoals + " : " + bluePlayerGoals + "  красный";
    JLabel scoreLabel = new JLabel(score, SwingConstants.CENTER);
    JPanel panel = new JPanel();

    Color color = Color.blue; // цвет мяча и линий (меняется при переходе хода, начинает синий игрок)

    List<Point> points = new ArrayList<>(); // Точки поля
    List<Line> lines = new ArrayList<>();   // Линии поля
    // Линии ворот
    Point gateRed1 = new Point(CELL * 4, CELL * 1);
    Point gateRed2 = new Point(CELL * 5, CELL * 1);
    Point gateRed3 = new Point(CELL * 6, CELL * 1);
    Point gateBlue1 = new Point(CELL * 4, CELL * 13);
    Point gateBlue2 = new Point(CELL * 5, CELL * 13);
    Point gateBlue3 = new Point(CELL * 6, CELL * 13);

    void start() {
        frame = new JFrame("Football");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH + 19, FIELD_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        footballField = new FootballField();
        footballField.addPointToList();
        canvas = new Canvas();
        canvas.setBackground(new Color(0, 220, 0));
        frame.getContentPane().add(BorderLayout.CENTER, canvas);
        scoreLabel.setFont(new Font("Arial", Font.TRUETYPE_FONT, 22));
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        panel.add(scoreLabel);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                key = e.getKeyCode();
                //System.out.println(key);
                ball.move();
            }
        });
        frame.setVisible(true);
        startGame();
    }

    void startGame() {
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
            lines.add(new Line(CELL * 4, CELL * 2, CELL * 4, CELL * 1, Color.red));
            lines.add(new Line(CELL * 4, CELL * 1, CELL * 5, CELL * 1, Color.red));
            lines.add(new Line(CELL * 5, CELL * 1, CELL * 6, CELL * 1, Color.red));
            lines.add(new Line(CELL * 6, CELL * 1, CELL * 6, CELL * 2, Color.red));
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
            lines.add(new Line(CELL * 6, CELL * 12, CELL * 6, CELL * 13, Color.blue));
            lines.add(new Line(CELL * 6, CELL * 13, CELL * 5, CELL * 13, Color.blue));
            lines.add(new Line(CELL * 5, CELL * 13, CELL * 4, CELL * 13, Color.blue));
            lines.add(new Line(CELL * 4, CELL * 13, CELL * 4, CELL * 12, Color.blue));
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
            lines.add(new Line((FIELD_CENTER_X + BALL_RADIUS), (FIELD_CENTER_Y + BALL_RADIUS),
                    (FIELD_CENTER_X + BALL_RADIUS), (FIELD_CENTER_Y + BALL_RADIUS), DEFAULT_COLOR));
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
            points.add(new Point(FIELD_CENTER_X + BALL_RADIUS, FIELD_CENTER_Y + BALL_RADIUS));
        }
    }


    /**
     * Мяч
     */
    class Ball {

        private static final int LEFT_BORDER = CELL - BALL_RADIUS;
        private static final int RIGHT_BORDER = CELL * 9 - BALL_RADIUS;
        private static final int TOP_BORDER = CELL * 2 - BALL_RADIUS;
        private static final int BOTTOM_BORDER = CELL * 12 - BALL_RADIUS;

        private static final int LEFT_BORDER_GATE = CELL * 4 - BALL_RADIUS;
        private static final int RIGHT_BORDER_GATE = CELL * 6 - BALL_RADIUS;
        private static final int TOP_BORDER_GATE = CELL - BALL_RADIUS;
        private static final int BOTTOM_BORDER_GATE = CELL * 13 - BALL_RADIUS;

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
            g.fillOval(x, y, BALL, BALL);
        }


        void move() {
            boolean xPoint = (x == CELL * 4 - BALL_RADIUS && y == CELL - BALL_RADIUS)
                    || (x == CELL * 6 - BALL_RADIUS && y == CELL - BALL_RADIUS)
                    || (x == CELL * 4 - BALL_RADIUS && y == CELL * 13 - BALL_RADIUS)
                    || (x == CELL * 6 - BALL_RADIUS && y == CELL * 13 - BALL_RADIUS);
//            boolean rightGateBorder = ;
//            boolean leftGateBorder;
            boolean borderPoint = (x == CELL * 4 - BALL_RADIUS && y == CELL * 2 - BALL_RADIUS)
                    || (x == CELL * 5 - BALL_RADIUS && y == CELL * 2 - BALL_RADIUS)
                    || (x == CELL * 6 - BALL_RADIUS && y == CELL * 2 - BALL_RADIUS)
                    || (x == CELL * 4 - BALL_RADIUS && y == CELL * 12 - BALL_RADIUS)
                    || (x == CELL * 5 - BALL_RADIUS && y == CELL * 12 - BALL_RADIUS)
                    || (x == CELL * 6 - BALL_RADIUS && y == CELL * 12 - BALL_RADIUS);
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
                color = Color.red; // Красный игрок разводит мяч
                bluePlayerGoals++;
                score = "   синий  " + bluePlayerGoals + " : " + redPlayerGoals + "  красный";
            }
            if (i == 2) {
//                JOptionPane.showMessageDialog(frame, "ГОЛ в ворота синего игрока");
                JOptionPane.showMessageDialog(frame, "", "GOAL", INFORMATION_MESSAGE);
                color = Color.blue; //Синий огрок разводит мяч
                redPlayerGoals++;
                score = "   синий  " + bluePlayerGoals + " : " + redPlayerGoals + "  красный";
            }
            clearField(); // перезапуск игры
        }

        void changeColor() {
            changeColor++;
            if (changeColor % 2 == 0) color = Color.blue;
            if (changeColor % 2 == 1) color = Color.red;
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
            footballField.addLineToList();
        }

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
                if (i > 49) {
                    new Point(l.getX2(), l.getY2()).drawPoints(g);
                }
            }
            // Текущее место положения мяча на поле
            new Point(lines.get(lines.size() - 1).getX2(),
                    lines.get(lines.size() - 1).getY2()).
                    drawPoints(g, new Color(255, 255, 255));
        }
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
            ball.drawBall(g, color);
            scoreLabel.setText(score);
            line.drawLines(g);
            // Управление горячими клавишами
            if (key == HIDE_GRID) showGrid = false;         // скрыть
            if (key == SHOW_GRID) showGrid = true;          // показать
            if (showGrid == true) footballField.addGrid(g); // сетку
            if (key == RESTART_GAME) restartGame(); // перезапуск игры
            if (key == CLEAR_FIELD) clearField();   // очистка поля
            if (key == RESET_SCORE) resetScore();   // сброс счета
            if (key == getXY) System.out.println("x= " + ball.getX() + " y= " + ball.getY());
            // Отрисовка линий, имеющихся в массиве, при движении мяча.
            if ((key == UP) || (key == DOWN) || (key == LEFT) || (key == RIGHT)) {
                line.drawLines(g);
            }
            // Действия при выполнении удара (нажатии "space")
            if (key == SHOT) {
                x1 = lines.get(lines.size() - 1).getX2();
                y1 = lines.get(lines.size() - 1).getY2();
                x2 = ball.getX() + BALL_RADIUS;
                y2 = ball.getY() + BALL_RADIUS;
                // Проверка точек в массиве. Если точка существует на поле, можно и нужно сделать ещё удар.
                for (Point p : points) {
                    if (((ball.getX() + BALL_RADIUS) == p.getX()) && ((ball.getY() + BALL_RADIUS) == p.getY())) {
                        countColor++; // счетчик для перехода хода со сменой цвета
                    }
                }
                // Проверка совпедения линий. Если лини существует, то по ней нельзя делать повторный удар.
                boolean checkLine = false;
                for (Line l : lines) {
                    // линии сверяются по координатам в оба направления
                    checkLine = (l.getX1() == x1 && l.getY1() == y1 && l.getX2() == x2 && l.getY2() == y2)  //туда
                            || (l.getX1() == x2 && l.getY1() == y2 && l.getX2() == x1 && l.getY2() == y1); //сюда
                }
                // Правило выполнения удара (на 1 клетку в любом направление, если точка существет, бьём ещё)
                if ((Math.abs(x1 - x2) == 50 && Math.abs(y1 - y2) == 50)
                        || (Math.abs(x1 - x2) == 50 && (Math.abs(y1 - y2) == 0))
                        || (Math.abs(x1 - x2) == 0 && (Math.abs(y1 - y2) == 50))) {
                    if (!checkLine) {
                        point.addNewPoint(x2, y2); // в случае возможности удара, доавляем новую точку
                        line.addNewLine(x1, y1, x2, y2, color); // добавляем новую линию
                        if (countColor == 0) {
                            ball.changeColor(); // меняем цвет при переходе хода
                            ball.drawBall(g, color); // рисуем мяч нового цвета
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