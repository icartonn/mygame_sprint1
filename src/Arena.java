import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Arena {

    // константы
    private static final int SIZE = 20;
    private static final int TOWER_X = 9; // ну почти по центру
    private static final int TOWER_Y = 17;
    private static final int TOTAL_ENEMIES = 10;

    private Player player;
    private List<Enemy> enemies;
    private int towerHp;
    private int spawnedCount;
    private boolean towerAlive;
    private String lastMessage = "";
    private Random rand = new Random();


    // конструктор арены принимающий игрока
    public Arena(Player player) {
        this.player = player;
        this.enemies = new ArrayList<>();
        this.towerHp = 500;
        this.spawnedCount = 0;
        this.towerAlive = true;
        player.setX(1);
        player.setY(1);
        player.heal();
        spawnEnemy();
    }

    private void spawnEnemy() {
        if (spawnedCount < TOTAL_ENEMIES) {
            int ex = clamp(TOWER_X + rand.nextInt(5) - 2, 0, SIZE - 1);
            int ey = clamp(TOWER_Y + rand.nextInt(5) - 2, 0, SIZE - 1);
            if (ex == TOWER_X && ey == TOWER_Y) ex = TOWER_X - 1;
            enemies.add(new Enemy(ex, ey));
            spawnedCount++;
        }
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val)); // max и min - две функции для ограничения (нашел на stack overflow)
    }

    private boolean isNear(int x1, int y1, int x2, int y2) {
        boolean answer = false;
        if (Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1) { // abs - модуль числа (тоже stack overflow). Тут мы просто берем первые и вторые координаты, и модуль разности и является расстоянием
            answer = true;
        }
        return answer;
    }

    private Enemy getNearEnemy() {
        for (Enemy e : enemies) {
            if (e.isAlive() && isNear(player.getX(), player.getY(), e.getX(), e.getY())) {
                return e;
            }
        }
        return null;
    }

    private boolean isNearTower() {
        boolean answer = false;
        if (isNear(player.getX(), player.getY(), TOWER_X, TOWER_Y)) {
            answer = true;
        }
        return answer;
    }

    public static void clearScreen() { // обьчный аски клир у меня не работает так что сооружаем костыль
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }


    private void printMap() {
        Enemy target = getNearEnemy();
        boolean nearTower = isNearTower();

        clearScreen();

        System.out.printf("Игрок | HP: %d/%d  Урон: %d  Защита: %d%n",
                player.getHp(), player.getMaxHp(), player.getDamage(), player.getDefense());

        if (target != null) {
            System.out.printf("Цель | HP монстра: %d/%d  Урон монстра: %d%n",
                    target.getHp(), target.getMaxHp(), target.getDamage());
            System.out.println("[Enter] = атака");
        } else if (nearTower) {
            System.out.printf("Цель | Башня HP: %d/500%n", towerHp);
            System.out.println("[Enter] = атака башни");
        } else {
            System.out.println("Управление: W-вверх  S-вниз  A-влево  D-вправо");
        }

        if (lastMessage != "") {
            System.out.println(lastMessage);
            lastMessage = ""; // на случай если сообщения не будет, чтобы не писалось предыдущее
        }

        boolean[][] enemyPos = new boolean[SIZE][SIZE];
        for (Enemy e : enemies) {
            if (e.isAlive()) enemyPos[e.getY()][e.getX()] = true;
        }

        StringBuilder border = new StringBuilder("+"); //
        for (int i = 0; i < SIZE; i++) {
            border.append("-");
        }
        border.append("+");
        System.out.println(border);

        for (int y = 0; y < SIZE; y++) {
            StringBuilder row = new StringBuilder("|");
            for (int x = 0; x < SIZE; x++) {
                if (x == player.getX() && y == player.getY()) {
                    row.append("P"); // player
                } else if (towerAlive && x == TOWER_X && y == TOWER_Y) {
                    row.append("B"); // base
                } else if (enemyPos[y][x]) {
                    row.append("M"); // monster
                } else {
                    row.append(" "); // ничего нету
                }
            }
            row.append("|");
            System.out.println(row);
        }
        System.out.println(border);

        long aliveCount = enemies.stream().filter(Enemy::isAlive).count();
        System.out.printf("Монстров на карте: %d/%d  |  Башня: %s%n", aliveCount, TOTAL_ENEMIES, towerAlive ? "жива (" + towerHp + " HP)" : "не жива");
    }

    private void moveEnemies() {
        boolean[][] blocked = new boolean[SIZE][SIZE];
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) blocked[enemy.getY()][enemy.getX()] = true;
        }
        blocked[player.getY()][player.getX()] = true;
        if (towerAlive) {
            blocked[TOWER_Y][TOWER_X] = true;
        }

        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) { // фикс вылета
                blocked[enemy.getY()][enemy.getX()] = false;
                enemy.moveToward(player.getX(), player.getY(), blocked);
                blocked[enemy.getY()][enemy.getX()] = true;
            }
        }
    }

    public boolean run(Scanner scanner) { // беру объект сканнера чтобы переватить ввод и не создавать тут его
        System.out.println("\n=== Цель: уничтожить башню (Она помечена тут как B) ===");

        while (player.isAlive()) {
            printMap();

            Enemy target = getNearEnemy();
            boolean nearTower = isNearTower();

            String input = scanner.nextLine().trim().toUpperCase();

            if (target != null) { // отвечает за управление, конкретно этот за атаку
                target.takeDamage(player.getDamage());
                if (!target.isAlive()) {
                    lastMessage = "+5$ вам на счет";
                    player.addCurrency(5);
                    enemies.remove(target);
                    if (spawnedCount < TOTAL_ENEMIES) spawnEnemy();
                } else {
                    player.takeDamage(target.getDamage());
                    lastMessage = String.format("Вы нанесли %d урона, а монстр ударил вас на %d.",
                            player.getDamage(), target.getDamage());
                }
            } else if (nearTower) { // атака башни
                towerHp -= player.getDamage();
                if (towerHp <= 0) {
                    towerHp = 0;
                    towerAlive = false;
                    printMap();
                    System.out.println("\n>>> Вы выйграли! +50$ <<<");
                    System.out.println("Нажмите Enter...");
                    scanner.nextLine();
                    player.addCurrency(50);
                    return true;
                }
                lastMessage = String.format("Удар по башне: %d урона. Осталось HP: %d", player.getDamage(), towerHp);
            } else { // передвижение игрока
                int nx = player.getX();
                int ny = player.getY();
                switch (input) {
                    case "W": ny--; break;
                    case "S": ny++; break;
                    case "A": nx--; break;
                    case "D": nx++; break;
                }
                boolean blocked = false;
                if (towerAlive && nx == TOWER_X && ny == TOWER_Y) blocked = true;
                for (Enemy e : enemies) {
                    if (e.isAlive() && e.getX() == nx && e.getY() == ny) { blocked = true; break; }
                }
                if (!blocked && nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE) {
                    player.setX(nx);
                    player.setY(ny);
                }
            }

            moveEnemies();

            if (!player.isAlive()) {
                printMap();
                System.out.println("\n>>> Вы погибли! <<<");
                System.out.println("Нажмите Enter чтобы вернуться в меню...");
                scanner.nextLine();
                return false;
            }

            long alive = enemies.stream().filter(Enemy::isAlive).count();
            if (!towerAlive && alive == 0) {
                System.out.println("\n>>> Вы победили! <<<");
                System.out.println("Нажмите Enter...");
                scanner.nextLine();
                player.addCurrency(50);
                return true;
            }
        }
        return false;
    }
}