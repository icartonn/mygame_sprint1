import java.util.Random;

public class Enemy {
    private int x;
    private int y;
    private int hp;
    private int maxHp;
    private int damage;
    private boolean alive;
    private static Random rand = new Random();

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 30 + rand.nextInt(21);
        this.maxHp = this.hp;
        this.damage = 1 + rand.nextInt(10);
        this.alive = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }

    public void moveToward(int px, int py, boolean[][] blocked) {
        int dx = Integer.compare(px, x); // фикс Overflow ошибки (обычное вычитание не работет почему то)
        int dy = Integer.compare(py, y);
        int nx = x + dx;
        int ny = y + dy;
        if (nx >= 0 && nx < 64 && ny >= 0 && ny < 64 && !blocked[ny][nx]) {
            x = nx;
            y = ny;
        } else {
            int ax = x + dx;
            int ay = y;
            int bx = x;
            int by = y + dy;
            if (ax >= 0 && ax < 64 && ay >= 0 && ay < 64 && !blocked[ay][ax]) {
                x = ax;
            } else if (bx >= 0 && bx < 64 && by >= 0 && by < 64 && !blocked[by][bx]) {
                y = by;
            }
        }
    }
}