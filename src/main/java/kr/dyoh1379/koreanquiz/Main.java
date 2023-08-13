package kr.dyoh1379.koreanquiz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public final class Main extends JavaPlugin {

    private static Main instance;

    static boolean gameProcess;
    static int gameTicks;

    static List<String> koreanList = Arrays.asList("고슬고슬", "늘봄", "도담", "모아", "솔찬", "진이" , "키움", "토리", "한울", "다원", "윤슬", "함박눈", "안다미로", "아그데아그데", "초롱", "라움", "마루", "미르", "새론", "허우룩", "달보드레", "맵짜다", "아리아", "그린나래", "꼬리별", "다은", "다흰", "다원", "큰꽃", "나래", "풋내", "한별", "해늘", "슬옹", "바름", "보미", "새나", "새롬", "새얀", "수피아", "세이", "승아", "그린내", "꼬두람이", "까미", "다올", "또바기", "개맹이", "개힘");
    static List<String> loanList = Arrays.asList("하꼬", "짬뽕", "우동", "와사비", "오야붕", "오뎅", "쇼부", "세세세", "삐까삐까", "모찌", "다시다", "다대기", "노가다", "냄비", "나가리", "구두", "기스", "겐세이", "가마니", "이루꾸", "사시미", "찌찌", "맘마", "와꾸", "이빠이", "만땅", "뗑깡", "찌라시", "스시", "사라", "다마", "다꽝", "쯔끼다시", "유도리", "겜뻬이", "쿠사리", "기라성", "앗싸리", "자바리", "싹쓰리", "뎃기리", "센바이", "단도리", "엥꼬", "시보리", "히야시", "돈까츠", "곤조", "몸뻬", "나시", "비닐", "해먹", "시소", "댐", "가방", "고구마", "바자회", "담배", "빵", "고무", "조끼", "망토", "지로", "무대포");

    static List<Location> locationList = Arrays.asList(
            new Location(Bukkit.getWorld("world"), -16.5, -59, 105.5),
            new Location(Bukkit.getWorld("world"), -20.5, -59, 105.5),
            new Location(Bukkit.getWorld("world"), -24.5, -59, 105.5),
            new Location(Bukkit.getWorld("world"), -28.5, -59, 105.5),
            new Location(Bukkit.getWorld("world"), -32.5, -59, 105.5),
            new Location(Bukkit.getWorld("world"), -36.5, -59, 105.5),
            new Location(Bukkit.getWorld("world"), -40.5, -59, 105.5),
            new Location(Bukkit.getWorld("world"), -44.5, -59, 105.5),
            new Location(Bukkit.getWorld("world"), -44.5, -59, 101.5),
            new Location(Bukkit.getWorld("world"), -44.5, -59, 97.5),
            new Location(Bukkit.getWorld("world"), -44.5, -59, 93.5),
            new Location(Bukkit.getWorld("world"), -44.5, -59, 89.5),
            new Location(Bukkit.getWorld("world"), -44.5, -59, 85.5),
            new Location(Bukkit.getWorld("world"), -40.5, -59, 85.5),
            new Location(Bukkit.getWorld("world"), -36.5, -59, 85.5),
            new Location(Bukkit.getWorld("world"), -32.5, -59, 85.5),
            new Location(Bukkit.getWorld("world"), -28.5, -59, 85.5),
            new Location(Bukkit.getWorld("world"), -24.5, -59, 85.5),
            new Location(Bukkit.getWorld("world"), -20.5, -59, 85.5),
            new Location(Bukkit.getWorld("world"), -16.5, -59, 85.5),
            new Location(Bukkit.getWorld("world"), -16.5, -59, 89.5),
            new Location(Bukkit.getWorld("world"), -16.5, -59, 93.5),
            new Location(Bukkit.getWorld("world"), -16.5, -59, 97.5),
            new Location(Bukkit.getWorld("world"), -16.5, -59, 101.5),
            new Location(Bukkit.getWorld("world"), -16.5, -59, 105.5)
    );


    static HashMap<UUID, String> correct = new HashMap<>();
    static HashMap<UUID, List<String>> playerWordList = new HashMap<>();
    static HashMap<UUID, Boolean> isCorrect = new HashMap<>();
    static List<String> winner = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        instance = this;

        gameProcess = false;

        Bukkit.getPluginCommand("광복절게임").setExecutor(new Game());
        Bukkit.getPluginCommand("광복절게임").setTabCompleter(new Game());
        Bukkit.getPluginManager().registerEvents(new Game(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        if (gameProcess) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            scoreboard.getObjective("Progress").unregister();
            scoreboard.getObjective("Answered").unregister();
        }
    }

    public static Main getInstance() {
        return instance;
    }
}
