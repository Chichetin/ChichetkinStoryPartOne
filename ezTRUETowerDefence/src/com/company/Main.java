package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JFrame implements KeyListener, MouseListener {
    static final int w = 1600;
    static final int h = 900;
    static final int cellsX = (w - 200) / 20;//количество ячеек по оси х и у
    static final int cellsY = (h - 200) / 20;
    static final int[][] grid = new int[cellsX][cellsY];
    static int botTX = cellsX / 2;//положение цели на сетке
    static int botTY = cellsY / 2;
    static ArrayList<Integer> botX = new ArrayList(List.of());//
    static ArrayList<Integer> botY = new ArrayList(List.of());//местонахождение ботов
    static ArrayList botHP = new ArrayList(List.of());//здоровье каждого бота
    static boolean pause = true;//включенная или отключенная пауза
    static int gold = 210;
    static int rezh = 1; //режим башни // 1 - стенка, 2- башня огня (бьющая рядом с собой), 3 - башня лучника (стреляющая на расстояние до 5 клеток)

    static {//заполняем сетку случайными часламми  от 0 до 2
        Random r = new Random();
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                grid[i][j] = r.nextDouble() > 0.2 ? 0 : -1;
            }
        }
    }


    static final int gridX = 100;//координаты левого верхнего угла сетки
    static final int gridY = 100;
    static final int gridW = w - 200;//Шририна и высота сетки
    static final int gridH = h - 200;
    static final int cellSizeX = gridW / cellsX;// длинна стороны ячейки
    static final int cellSizeY = gridH / cellsY;
    static int frames = 0;
    static int speed = 8;

    public static void draw(Graphics2D g) {

        if (pause == false) {
            frames++;
            System.out.println(speed);
            if (frames == 3000 | frames == 6000) {
                speed--;
            }
            if (frames % speed == 0) {
                moveBot();
            }

            gold =gold+ 10;

        }
        //чтобы нарисовать сетку нужно перебрать все её клетки
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                //и для каждой клетки нарисовать прямоугольник нужного цвета, проще всего это сделать через switch
                if (grid[i][j] == -1) {
                    g.setColor(new Color(0, 9, 29));
                } else if (grid[i][j] == -2) {
                    g.setColor(new Color(80, 9, 5));
                } else if (grid[i][j] == -3) {
                    g.setColor(new Color(0, 78, 0));
                } else g.setColor(new Color(0, 0, 0, 0));
                //неправильное число в сетке
                g.fillRect(gridX + cellSizeX * i, gridY + cellSizeY * j, cellSizeX, cellSizeY);

                if (grid[i][j] == 1) {
                    g.setColor(Color.RED);
                    g.fillOval(gridX + cellSizeX * i, gridY + cellSizeY * (j + 1), 10, 10);
                } else {
                    g.setColor(Color.BLACK);
                    g.drawString(grid[i][j] + "", gridX + cellSizeX * i, gridY + cellSizeY * (j + 1));
                }

            }
        }
        //рисуем ботов поверх сетки
        g.setFont(new Font("", Font.BOLD, cellSizeY));
        g.setColor(Color.RED);
        for (int i = 0; i < botX.size(); i++) {
            g.drawString("O", gridX + cellSizeX * (int) botX.get(i), gridY + cellSizeY * (int) botY.get(i) + cellSizeY * 3 / 4);
        }
        //рисуем кол-во золота и тип ставляемой башни
        g.drawString(String.valueOf("Золота : " + gold), 90, 90);

        switch (rezh) {
            case 1:
                g.drawString(String.valueOf("Стена"), 90, 70);
                break;
            case 2:
                g.drawString(String.valueOf("Башня огня"), 90, 70);
                break;
            case 3:
                g.drawString(String.valueOf("Башня лучника"), 90, 70);
                break;
        }

    }


    //функция добавления бота
    public static void newBot() {

    }

    public static void recalculatePaths(int tx, int ty) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] >= 0) grid[i][j] = 0;
            }//обнуление всего поля
        }
        grid[tx][ty] = 1;
        int cur = 1; //собственно цвет

        boolean overwritten = false;
        do {
            overwritten = false;
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    if (grid[i][j] != 0) continue;
                    if ((i - 1 >= 0 && grid[i - 1][j] == cur) ||
                            (j - 1 >= 0 && grid[i][j - 1] == cur) ||
                            (i + 1 < cellsX && grid[i + 1][j] == cur) ||
                            (j + 1 < cellsY && grid[i][j + 1] == cur)) {
                        grid[i][j] = cur + 1;
                        overwritten = true;
                    }
                }
            }
            cur++;
        } while (overwritten);

    }

    public static void moveBot() {
        for (int i = 0; i < botX.size(); i++) {
            if (grid[botX.get(i)][botY.get(i)] > 0) {
                if ((botX.get(i) - 1 >= 0 && grid[botX.get(i) - 1][botY.get(i)] == grid[botX.get(i)][botY.get(i)] - 1)) {
                    botX.set(i, botX.get(i) - 1);
                } else if (botY.get(i) - 1 >= 0 && grid[botX.get(i)][botY.get(i) - 1] == grid[botX.get(i)][botY.get(i)] - 1) {
                    botY.set(i, botY.get(i) - 1);
                } else if ((botX.get(i) + 1 < cellsX && grid[botX.get(i) + 1][botY.get(i)] == grid[botX.get(i)][botY.get(i)] - 1)) {
                    botX.set(i, botX.get(i) + 1);
                } else if (botY.get(i) + 1 < cellsY && grid[botX.get(i)][botY.get(i) + 1] == grid[botX.get(i)][botY.get(i)] - 1) {
                    botY.set(i, botY.get(i) + 1);
                }
            }
        }

    }

    public void mouseClicked(MouseEvent e) {
        int cx = (e.getX() - gridX) / cellSizeX;
        int cy = (e.getY() - gridY) / cellSizeY;
        if (e.getButton() == MouseEvent.BUTTON1) {
            switch (rezh) {
                case 1:
                    if (gold > 30) {
                        grid[cx][cy] = -1;
                        gold -= 30;
                    }
                case 2:
                    if (gold > 50) {
                        grid[cx][cy] = -2;
                        gold -= 50;
                    }
                case 3:
                    if (gold > 50) {
                        grid[cx][cy] = -3;
                        gold-=50;
                    }
            }

            recalculatePaths(botTX, botTY);//надо будет изменить
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            botTX = cx;
            botTY = cy;
            recalculatePaths(botTX, botTY);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (pause == true) pause = false;
            else pause = true;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q:
                rezh = 1;
            case KeyEvent.VK_W:
                rezh = 2;
            case KeyEvent.VK_E:
                rezh = 3;
        }
    }

    //дальше фигня
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //магический код позволяющий всему работать, лучше не трогать
    public static void main(String[] args) throws InterruptedException {
        Main jf = new Main();
        jf.setSize(w, h);//размер экрана
        jf.setUndecorated(false);//показать заголовок окна
        jf.setTitle("Grid");
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.createBufferStrategy(2);
        jf.addKeyListener(jf);
        jf.addMouseListener(jf);
        //в бесконечном цикле рисуем новый кадр
        while (true) {
            long frameLength = 1000 / 60; //пытаемся работать из рассчета  60 кадров в секунду
            long start = System.currentTimeMillis();
            BufferStrategy bs = jf.getBufferStrategy();
            Graphics2D g = (Graphics2D) bs.getDrawGraphics();
            g.clearRect(0, 0, jf.getWidth(), jf.getHeight());
            draw(g);

            bs.show();
            g.dispose();

            long end = System.currentTimeMillis();
            long len = end - start;
            if (len < frameLength) {
                Thread.sleep(frameLength - len);
            }
        }

    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}