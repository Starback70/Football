package ru.izotov;

//import com.sun.corba.se.impl.orbutil.graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class GameOfFootball {

    JFrame frame;
    Canvas canvas;
    Ball ball;
    FootballField footballField;
    final int CELL_SIZE = 50;
    //final int FIELD_EDGE = CELL_SIZE/2;
    final int FIELD_HEIGHT = CELL_SIZE * 15;
    final int FIELD_WIDTH = CELL_SIZE * 10;
    final int BALL_RADIUS = 20 ;
    final int FIELD_CENTER_X = FIELD_WIDTH/2 - BALL_RADIUS/2;
    final int FIELD_CENTER_Y = FIELD_HEIGHT / 2 - CELL_SIZE/2 - BALL_RADIUS/2;

    final Color DEFAULT_COLOR = Color.black;
    Color color = DEFAULT_COLOR;
    final int FIELD[][] = new int[FIELD_HEIGHT][FIELD_WIDTH];
    final int LEFT = 37;
    final int UP = 38;
    final int RIGHT = 39;
    final int DOWN = 40;
    final int SHOT = 32;
    int direction = 0;
    int countColor = 2;
    ArrayList<Point> points = new ArrayList<Point>();
    Point startPoint = new Point(FIELD_CENTER_X + BALL_RADIUS/2, FIELD_CENTER_Y + BALL_RADIUS/2);

    void go (){
        frame = new JFrame("Football");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH, FIELD_HEIGHT);
        frame.setLocation(300, 500);
        frame.setResizable(false);

        footballField = new FootballField();
        canvas = new Canvas();
        canvas.setBackground(Color.GREEN);

        frame.getContentPane().add(BorderLayout.CENTER, canvas);
        frame.addKeyListener(new KeyAdapter() {
            //@Override
            public void keyPressed (KeyEvent e) {
                direction = e.getKeyCode();
                ball.move();
            }
        });
        frame.setVisible(true);

        ball = new Ball(FIELD_CENTER_X, FIELD_CENTER_Y, 0);
        ball.setStartPoint();

    }

    class FootballField  {
        void paintField (Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.black);
            // границы поля, начало - левый верхний угол  и по часовой.
            g2.drawLine(CELL_SIZE*1,CELL_SIZE*2, CELL_SIZE*4, CELL_SIZE*2);
            g2.drawLine(CELL_SIZE*4,CELL_SIZE*2, CELL_SIZE*4, CELL_SIZE*1);
            g2.drawLine(CELL_SIZE*4,CELL_SIZE*1, CELL_SIZE*6, CELL_SIZE*1);
            g2.drawLine(CELL_SIZE*6,CELL_SIZE*1, CELL_SIZE*6, CELL_SIZE*2);
            g2.drawLine(CELL_SIZE*6,CELL_SIZE*2, CELL_SIZE*9, CELL_SIZE*2);
            g2.drawLine(CELL_SIZE*9,CELL_SIZE*2, CELL_SIZE*9, CELL_SIZE*12);
            g2.drawLine(CELL_SIZE*9,CELL_SIZE*12, CELL_SIZE*6, CELL_SIZE*12);
            g2.drawLine(CELL_SIZE*6,CELL_SIZE*12, CELL_SIZE*6, CELL_SIZE*13);
            g2.drawLine(CELL_SIZE*6,CELL_SIZE*13, CELL_SIZE*4, CELL_SIZE*13);
            g2.drawLine(CELL_SIZE*4,CELL_SIZE*13, CELL_SIZE*4, CELL_SIZE*12);
            g2.drawLine(CELL_SIZE*4,CELL_SIZE*12, CELL_SIZE*1, CELL_SIZE*12);
            g2.drawLine(CELL_SIZE*1,CELL_SIZE*12, CELL_SIZE*1, CELL_SIZE*2);
            // середина поля
            g2.drawLine(CELL_SIZE*1,CELL_SIZE*7, CELL_SIZE*9, CELL_SIZE*7);
        }
    }

    class Ball {

        int x, y;
        int x1, y1, x2, y2, x11, y11, x22, y22;
        int count = 0;

        Ball (int x, int y, int direction) { this.setXY(x, y); }

        void paintBall(Graphics g){
            g.setColor(color);
            g.fillOval(x, y, BALL_RADIUS, BALL_RADIUS);
        }

        void setStartPoint () { points.add(startPoint); }
        void setXY(int x, int y) { this.x = x; this.y = y; }
        int getX() { return x; }
        int getY() { return y; }

        void  paintLine (Graphics g) {
            //при нажатии "стрелок" рисуется тракетория предыдущих ударов
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            g2.setColor(color);
            if (points.size() >= 2) {
                x1 = FIELD_CENTER_X + BALL_RADIUS/2;
                y1 = FIELD_CENTER_Y + BALL_RADIUS/2;
                for (int i = 0 ; i < points.size(); i++) {
                    System.out.println();
                    System.out.println("Шаг прорисовки #" + i);
                    x2 = points.get(i).getX();
                    y2 = points.get(i).getY();
                    System.out.println("x2=" + x2 + "; y2=" + y2);
                    System.out.println("текущий x1=" + x1 + "; текущий y1=" + y1);
                    g2.drawLine(x1, y1, x2, y2);
                    x1 = x2;
                    y1 = y2;
                    System.out.println("новый x1=" + x1 + "; новый y1=" + y1);
                    new Point(x1, y1).paintPoint(g);
                }
            }
        }
        void shot (Graphics g){
            //при нажатии "пробела" рисуется тракетория текущего удара.
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            g2.setColor(color);
            x1 = points.get(points.size()-1).getX();
            y1 = points.get(points.size()-1).getY();
            System.out.println("1) x1: " + x1 + "; y1: " + y1);
            x2 = ball.getX() + BALL_RADIUS/2;
            y2 = ball.getY() + BALL_RADIUS/2;
            System.out.println("2) x2: " + x2 + "; y2: " + y2);
            g2.drawLine(x1, y1, x2, y2);
            points.add(new Point(x2, y2));
            System.out.println("points.SIZE: " + points.size());
            System.out.println("---------------------------------");
            System.out.println();
        }
        void move(){
            //Передвижение мяча по полю.
            if (direction == LEFT && x>40 )  { x = x - CELL_SIZE; }
            if (direction == RIGHT && x<440) { x = x + CELL_SIZE; }
            if (direction == UP && y>40)    { y = y - CELL_SIZE;  }
            if (direction == DOWN && y<640)  { y = y + CELL_SIZE; }
            canvas.repaint();
        }
        public void changeColor(int colorCount) {
            if (colorCount==0) color = Color.blue;
            if (colorCount==1) color = Color.red;
        }
    }

    class Point {
        //класс для создания точек, в узлах сетки при ударе мяча.
        int x, y;
        Point (int x, int y) { this.setXY(x, y);}

        void paintPoint (Graphics g){
            //Отрисовка точек
            g.fillOval(x-BALL_RADIUS/4, y-BALL_RADIUS/4, BALL_RADIUS/2, BALL_RADIUS/2);
        }
        int getX() { return x; }
        int getY() { return y; }
        void setXY(int x, int y) { this.x = x; this.y = y; }
    }

    public class Canvas extends JPanel {
        @Override
        public void paint (Graphics g){
            super.paint(g);
            ball.paintBall(g);
            if ((direction==UP)||(direction==DOWN)||(direction==LEFT)||(direction==RIGHT)){
                ball.paintLine(g);
            }
            if (direction == SHOT) {
                ball.shot(g);
                ball.paintLine(g);
                if (true){

                } else {
                    ball.changeColor(countColor++);
                }
            }
            footballField.paintField(g);

        }
    }

}
