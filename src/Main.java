import java.util.Scanner;

public class Main {
    private static Player player = new Player(4, 4);
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("------------------");
        System.out.println("Click royale");
        System.out.println("------------------");

        while (true) {
            showMainMenu();
        }
    }

    private static void showMainMenu() {
        System.out.println("\n====== Меню =========");
        System.out.printf("Валюта: %d  |  Урон: %d  |  Защита: %d%n", // вывожу для удобства через printf
                player.getCurrency(), player.getDamage(), player.getDefense());
        System.out.println("1 - Магазин улучшений");
        System.out.println("0 - Начать игру");
        System.out.print("> ");
        String input = sc.nextLine().trim();

        switch (input) {
            case "0":
                startGame();
                break;
            case "1":
                showShop();
                break;
            default:
                System.out.println("Вы ввели что-то неверное. Опции для взлома игры тут нету!");
        }
    }

    private static void startGame() {
        Arena arena = new Arena(player);
        arena.run(sc);
    }

    private static void showShop() {
        while (true) {
            System.out.println("\n========= Магазин ========");
            System.out.printf("У вас: %d$%n", player.getCurrency());
            System.out.println("----------------------------");
            System.out.printf("1 - Улучшить урон    (ур. %d) | Следующий уровень: +5 урона | Цена: %d%n",
                    player.getDamageLvl(), player.getDamageUpgradeCost());
            System.out.printf("2 - Улучшить защиту  (ур. %d) | Следующий уровень: +2 защиты | Цена: %d%n",
                    player.getDefenseLvl(), player.getDefenseUpgradeCost());
            System.out.println("3 - Режим кликера (зарабатывай доллары)");
            System.out.println("0 - Назад");
            System.out.printf("Текущий урон: %d | Текущая защита: %d%n",
                    player.getDamage(), player.getDefense());
            System.out.print("> ");
            String input = sc.nextLine().trim();

            switch (input) {
                case "1":
                    if (player.upgradeDamage()) {
                        System.out.printf("Урон улучшен! Теперь: %d%n", player.getDamage());
                    } else {
                        System.out.printf("Недостаточно долларов! Нужно: %d%n", player.getDamageUpgradeCost());
                    }
                    break;
                case "2":
                    if (player.upgradeDefense()) {
                        System.out.printf("Защита улучшена! Теперь: %d%n", player.getDefense());
                    } else {
                        System.out.printf("Недостаточно долларов! Нужно: %d%n", player.getDefenseUpgradeCost());
                    }
                    break;
                case "3":
                    clickerMode();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("и тут тоже нету опции взлома");
            }
        }
    }

    private static void clickerMode() {
        System.out.println("\n==== Кликер ====");
        System.out.println("Нажимай Enter чтобы зарабатывать доллары!");
        System.out.println("Введи 'exit' чтобы выйти.");
        int clicks = 0;
        while (true) {
            System.out.printf("%d$ | Всего кликов: %d%n", player.getCurrency(), clicks);
            System.out.print("> ");
            String input = sc.nextLine().trim();
            if (input == "exit") {
                System.out.printf("Вы заработали: %d$%n", clicks);
                break;
            }
            player.addCurrency(1);
            clicks++;
            System.out.println("+1$");
        }
    }
}