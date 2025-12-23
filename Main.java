import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

// --- قوانین بازی ---
interface GameRules {
    int baseScore();
    int bonusPerSecondRemaining();
    int penaltyPerWrongGuess();
    int maxWrongGuesses();
    int roundTimeSeconds();
}

class StandardRules implements GameRules {
    public int baseScore() { return 50; }
    public int bonusPerSecondRemaining() { return 2; }
    public int penaltyPerWrongGuess() { return 5; }
    public int maxWrongGuesses() { return 5; }
    public int roundTimeSeconds() { return 30; }
}

// --- بانک کلمات ---
class WordBank {
    private final List<String> words;
    private final Random random = new Random();

    public WordBank(List<String> words) {
        this.words = new ArrayList<>(words);
    }

    public String nextWord() {
        return words.get(random.nextInt(words.size())).toLowerCase();
    }
}

// --- یک دور بازی ---
class Round {
    private final String word;
    private final char[] revealed;
    private int wrongGuesses = 0;
    private boolean finished = false;
    private boolean won = false;

    public Round(String word) {
        this.word = word;
        this.revealed = new char[word.length()];
        for (int i = 0; i < revealed.length; i++) revealed[i] = '_';
    }

    public boolean guessChar(char c) {
        if (finished) return false;
        boolean hit = false;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == c && revealed[i] == '_') {
                revealed[i] = c;
                hit = true;
            }
        }
        if (!hit) wrongGuesses++;
        if (String.valueOf(revealed).equals(word)) {
            won = true; finished = true;
        }
        return hit;
    }

    public void timeUp() { finished = true; }

    public boolean isFinished() { return finished; }
    public boolean isWon() { return won; }
    public int getWrongGuesses() { return wrongGuesses; }
    public String getRevealed() { return String.valueOf(revealed); }
    public String getWord() { return word; }
}

// --- امتیاز ---
class Scoreboard {
    private int totalScore = 0;
    public void add(int score) { totalScore += score; }
    public int getTotalScore() { return totalScore; }
}

// --- تایمر ---
class TimerService {
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;

    public void start(int seconds, Consumer<Integer> onTick, Runnable onFinish) {
        stop();
        final int[] remaining = { seconds };
        future = scheduler.scheduleAtFixedRate(() -> {
            remaining[0]--;
            onTick.accept(Math.max(remaining[0], 0));
            if (remaining[0] <= 0) {
                onFinish.run();
                stop();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        if (future != null && !future.isCancelled()) future.cancel(true);
    }

    public void shutdown() { scheduler.shutdownNow(); }
}

// --- موتور بازی ---
class GameEngine {
    private final GameRules rules;
    private final WordBank bank;
    private final Scoreboard scoreboard;
    private final TimerService timer;

    public GameEngine(GameRules rules, WordBank bank) {
        this.rules = rules;
        this.bank = bank;
        this.scoreboard = new Scoreboard();
        this.timer = new TimerService();
    }

    public void playRounds(int count) {
        Scanner sc = new Scanner(System.in);
        for (int r = 1; r <= count; r++) {
            String word = bank.nextWord();
            Round round = new Round(word);
            System.out.println("\n--- Round " + r + " ---");
            System.out.println("Word: " + round.getRevealed());

            final int[] secondsLeft = { rules.roundTimeSeconds() };
            timer.start(rules.roundTimeSeconds(),
                sec -> secondsLeft[0] = sec,
                () -> {
                    round.timeUp();
                    System.out.println("\nTime's up!");
                }
            );while (!round.isFinished()
                   && round.getWrongGuesses() < rules.maxWrongGuesses()) {
                System.out.print("Enter a letter: ");
                String input = sc.nextLine().trim().toLowerCase();
                if (input.isEmpty()) continue;
                char c = input.charAt(0);
                boolean hit = round.guessChar(c);
                System.out.println(hit ? "Hit!" : "Miss!");
                System.out.println("Word: " + round.getRevealed()
                                   + " | Wrong: " + round.getWrongGuesses()
                                   + " | Time left: " + secondsLeft[0] + "s");
            }

            timer.stop();

            int scoreThisRound = 0;
            if (round.isWon()) {
                scoreThisRound = rules.baseScore()
                                + rules.bonusPerSecondRemaining() * secondsLeft[0]
                                - rules.penaltyPerWrongGuess() * round.getWrongGuesses();
                System.out.println("You won! +" + scoreThisRound + " pts");
            } else {
                System.out.println("You lost. The word was: " + round.getWord());
            }
            scoreboard.add(Math.max(0, scoreThisRound));
            System.out.println("Total score: " + scoreboard.getTotalScore());
        }
        timer.shutdown();
        System.out.println("\nGame finished. Final score: " + scoreboard.getTotalScore());
    }
}

// --- نقطه شروع ---
public class Main {
    public static void main(String[] args) {
        GameRules rules = new StandardRules();
        WordBank bank = new WordBank(Arrays.asList(
            "AKBAR", "ABGHER", "ALI", "REZA", "HASAN", "SAJAD", "AMIRHOSSEIN"
        ));
        GameEngine engine = new GameEngine(rules, bank);
        engine.playRounds(3);
    }
}