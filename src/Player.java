public class Player {
    private int x;
    private int y;
    private int hp;
    private int maxHp; // вообще можно сделать и константой так как есть defense, но боюсь
    private int damage;
    private int defense;
    private int currency;
    private int damageLvl;
    private int defenseLvl;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = 100;
        this.maxHp = 100;
        this.damage = 10;
        this.defense = 0;
        this.currency = 0;
        this.damageLvl = 0;
        this.defenseLvl = 0;
    }

    // геттеры

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

    public int getDefense() {
        return defense;
    }

    public int getCurrency() {
        return currency;
    }

    public int getDamageLvl() {
        return damageLvl;
    }

    public int getDefenseLvl() {
        return defenseLvl;
    }

    // перемещение
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // добавление валюты
    public void addCurrency(int amount) {
        this.currency += amount;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int dmg) {
        int actual = Math.max(1, dmg - defense);
        hp -= actual;
        if (hp < 0) hp = 0;
    }

    public boolean upgradeDamage() {
        int cost = (damageLvl + 1) * 10;
        if (currency >= cost) {
            currency -= cost;
            damage += 5;
            damageLvl++;
            return true;
        }
        return false;
    }

    public boolean upgradeDefense() {
        int cost = (defenseLvl + 1) * 10;
        if (currency >= cost) {
            currency -= cost;
            defense += 2;
            defenseLvl++;
            return true;
        }
        return false;
    }

    public void heal() {
        hp = maxHp;
    }

    public int getDamageUpgradeCost() {
        return (damageLvl + 1) * 10;
    }
    public int getDefenseUpgradeCost() {
        return (defenseLvl + 1) * 10;
    }
}