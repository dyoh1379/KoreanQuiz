package kr.dyoh1379.koreanquiz;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

import static kr.dyoh1379.koreanquiz.Main.*;

public class Game implements CommandExecutor, TabCompleter, Listener {

    Main plugin = Main.getInstance();
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if ("시작".equals(args[0])) {
            if (gameProcess) {
                sender.sendMessage(ChatColor.of("#FF0000") + "게임이 이미 시작되었습니다!");
                return true;
            }

            gameProcess = true;
            sender.sendMessage(ChatColor.GREEN + "게임 시작!");
            winner.clear();
            Objective progress = scoreboard.registerNewObjective("Progress", String.valueOf(Criteria.DUMMY));
            Objective answered = scoreboard.registerNewObjective("Answered", String.valueOf(Criteria.DUMMY));

            gameTicks = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {

                player.teleport(locationList.get(0));

                player.sendTitle(ChatColor.GOLD + "광복절 퀴즈", ChatColor.GRAY + "순우리말을 찾아라!");
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.5F);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            if (!gameProcess) {
                                cancel();
                                return;
                            }

                            if (gameTicks == 15 * 20) {
                                gameTicks = 0;
                            }

                            if (gameTicks == 0) {

                                for (Player player : Bukkit.getOnlinePlayers()) {

                                    List<String> wordList = new ArrayList<>();

                                    Random random = new Random();
                                    int randomKorean = random.nextInt(koreanList.size());
                                    String wordKorean = koreanList.get(randomKorean);

                                    correct.put(player.getUniqueId(), wordKorean);

                                    wordList.add(wordKorean);

                                    int endShuffle = 0;
                                    List<Integer> tempRandom = new ArrayList<>();

                                    while (endShuffle < 3) {

                                        int randomloan = random.nextInt(loanList.size());

                                        if (!tempRandom.contains(randomloan)) {
                                            tempRandom.add(randomloan);
                                            endShuffle += 1;
                                        }

                                    }

                                    for (Integer index : tempRandom) {
                                        wordList.add(loanList.get(index));
                                    }

                                    Collections.shuffle(wordList);
                                    playerWordList.put(player.getUniqueId(), wordList);
                                }


                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendMessage(ChatColor.DARK_GRAY + "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
                                    player.sendMessage("제시어: " + ChatColor.AQUA + String.valueOf(playerWordList.get(player.getUniqueId())).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", " " + ChatColor.YELLOW + "|" + ChatColor.AQUA));
                                    player.sendMessage(ChatColor.GRAY + "10초 내에 순우리말을 찾아 채팅으로 입력하세요!");
                                    player.sendMessage(ChatColor.DARK_GRAY + "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 0.75F);
                                }
                            }

                            if (gameTicks <= 200) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("시간: " + ChatColor.GREEN + ( gameTicks / 20) + "초"));

                                    if (player.getScoreboard().getObjective("Answered").getScore(player).getScore() == 0) {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 0, false, false, false));
                                    }
                                }
                            }

                            ++gameTicks;

                            if (gameTicks == 10 * 20 || isAnsweredAllPlayers()) {

                                gameTicks = 200;

                                for (Player player : Bukkit.getOnlinePlayers()) {

                                    if (answered.getScore(player).getScore() == 0) {

                                        player.sendMessage(ChatColor.RED + "답을 입력하지 않았습니다!");
                                        player.sendMessage(ChatColor.GRAY + "1칸 뒤로 이동합니다..");
                                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                                        subtractPoints(player, 1);

                                        player.teleport(locationList.get(getPoints(player)));
                                    }

                                    if (answered.getScore(player).getScore() == 1) {

                                        player.getScoreboard().getObjective("Answered").getScore(player).setScore(0);

                                        if (isCorrect.get(player.getUniqueId())) {
                                            player.sendMessage(ChatColor.GREEN + "정답!");
                                            player.sendMessage(ChatColor.GRAY + "2칸 앞으로 이동합니다!");
                                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0F, 1.5F);
                                            addPoints(player, 2);

                                            if (progress.getScore(player).getScore() > 24) {
                                                player.teleport(locationList.get(24));
                                            } else {
                                                player.teleport(locationList.get(getPoints(player)));
                                            }

                                            for (Player players : Bukkit.getOnlinePlayers()) {

                                                if (progress.getScore(players).getScore() >= 24) {

                                                    winner.add(players.getName());
                                                    removeDuplicates(winner);
                                                }

                                                if (progress.getScore(players).getScore() >= 24) {
                                                    Firework firework = (Firework) players.getWorld().spawnEntity(locationList.get(0), EntityType.FIREWORK);
                                                    FireworkMeta fireworkMeta = firework.getFireworkMeta();

                                                    FireworkEffect effect = FireworkEffect.builder()
                                                            .flicker(true)
                                                            .trail(true)
                                                            .withColor(Color.RED)
                                                            .withFade(Color.ORANGE)
                                                            .with(FireworkEffect.Type.BURST)
                                                            .build();

                                                    fireworkMeta.addEffect(effect);
                                                    fireworkMeta.setPower(1);
                                                    firework.setFireworkMeta(fireworkMeta);

                                                    Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 10);
                                                }


                                                for (Player allPlayer : Bukkit.getOnlinePlayers()) {
                                                    if (getPoints(allPlayer) >= 24) {

                                                        for (Player endPlayer : Bukkit.getOnlinePlayers()) {
                                                            endPlayer.sendTitle(ChatColor.of("#FF0000") + "게임 종료!", "우승자: " + ChatColor.GOLD + String.valueOf(winner).replaceAll("\\[", "").replaceAll("\\]", ""));
                                                        }
                                                    }
                                                }


                                                if (gameProcess) {
                                                    if (progress.getScore(players).getScore() >= 24) {
                                                        new BukkitRunnable() {
                                                            @Override
                                                            public void run() {

                                                                List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
                                                                sortPlayersByScore(playerList);

                                                                new BukkitRunnable() {
                                                                    @Override
                                                                    public void run() {

                                                                        if (gameProcess) {

                                                                            Bukkit.broadcastMessage(ChatColor.RED + "-----[ " + ChatColor.GOLD + "점수" + ChatColor.RED + " ]-----");

                                                                            for (int i = 0; i < playerList.size(); i++) {
                                                                                Player player = playerList.get(i);
                                                                                Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(i + 1) + ". " + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " - " + ChatColor.GREEN + progress.getScore(player).getScore());
                                                                            }

                                                                            for (Player player : Bukkit.getOnlinePlayers()) {
                                                                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                                                                            }
                                                                        }

                                                                        progress.unregister();
                                                                        answered.unregister();
                                                                        gameProcess = false;
                                                                    }
                                                                }.runTaskLater(plugin, 20);
                                                            }
                                                        }.runTaskLater(plugin, 20);
                                                    }
                                                }
                                            }
                                        }

                                        if (!isCorrect.get(player.getUniqueId())) {
                                            player.sendMessage(ChatColor.RED + "오답!");
                                            player.sendMessage(ChatColor.GRAY + "1칸 뒤로 이동합니다..");
                                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                                            subtractPoints(player, 1);

                                            player.teleport(locationList.get(getPoints(player)));
                                        }
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 0,  1);
                }
            }.runTaskLater(plugin, 3 * 20);

            return true;
        }

        if ("종료".equals(args[0])) {
            if (!gameProcess) {
                sender.sendMessage(ChatColor.of("#FF0000") + "게임이 이미 종료되었습니다!");
                return true;
            }

            gameProcess = false;
            sender.sendMessage(ChatColor.GREEN + "게임이 종료되었습니다.");

            scoreboard.getObjective("Progress").unregister();
            scoreboard.getObjective("Answered").unregister();
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> list = Arrays.asList("시작", "종료");

        List<String> completions = null;

        if (args.length == 1) {
            for (String s : list) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    if (completions == null) {
                        completions = new ArrayList();
                    }
                    completions.add(s);
                }
            }

            if (completions != null) {
                Collections.sort((completions));
            }
        }
        return completions;
    }

    @EventHandler
    public void chat(PlayerChatEvent e) {
        Player player = e.getPlayer();

        if (gameProcess) {
            if (gameTicks < 10 * 20) {

                scoreboard.getObjective("Answered").getScore(player).setScore(1);

                e.setCancelled(true);
                player.sendMessage(ChatColor.BOLD + "답이 입력되었습니다!");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F);

                if (e.getMessage().equals(correct.get(player.getUniqueId()))) {
                    isCorrect.put(player.getUniqueId(), true);
                    return;
                }

                isCorrect.put(player.getUniqueId(), false);


            }

        }
    }

    public void sortPlayersByScore(List<Player> players) {
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                Objective progress = scoreboard.getObjective("Progress");

                int score1 = progress.getScore(player1).getScore();
                int score2 = progress.getScore(player2).getScore();
                return Integer.compare(score2, score1);
            }
        });
    }

    public int getPoints(Player player) {
        return scoreboard.getObjective("Progress").getScore(player).getScore();
    }

    public void addPoints(Player player, int points) {
        scoreboard.getObjective("Progress").getScore(player).setScore(getPoints(player) + points);
    }

    public void subtractPoints(Player player, int points) {

        if (getPoints(player) - points < 0) {
            scoreboard.getObjective("Progress").getScore(player).setScore(0);
            return;
        }

        scoreboard.getObjective("Progress").getScore(player).setScore(getPoints(player) - points);
    }

    public void removeDuplicates(List<String> inputList) {
        List<String> uniqueList = new ArrayList<>();
        for (String str : inputList) {
            if (!uniqueList.contains(str)) {
                uniqueList.add(str);
            }
        }
        inputList.clear();
        inputList.addAll(uniqueList);
    }

    public boolean isAnsweredAllPlayers() {

        List<Integer> answeredList = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            answeredList.add(scoreboard.getObjective("Answered").getScore(player).getScore());
        }

        if (!answeredList.contains(0)) {
            return true;
        }

        return false;
    }
}
