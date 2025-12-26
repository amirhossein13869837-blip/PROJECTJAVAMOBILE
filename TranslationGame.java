import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TranslationGame extends JFrame {

    String[] englishWords = {
        "computer",
        "keyboard",
        "mouse",
        "internet",
        "programming",
        "java",
        "monitor",
        "application"
    };

    String[] persianWords = {
        "کامپیوتر",
        "کیبورد",
        "ماوس",
        "اینترنت",
        "برنامه نویسی",
        "جاوا",
        "مانیتور",
        "برنامه"
    };

    int index;
    int score = 0;

    JLabel titleLabel;
    JLabel wordLabel;
    JTextField inputField;
    JLabel resultLabel;
    JLabel scoreLabel;
    JButton checkButton;

    Font titleFont = new Font("Tahoma", Font.BOLD, 22);
    Font normalFont = new Font("Tahoma", Font.PLAIN, 16);

    Color bgColor = new Color(30, 30, 30);
    Color panelColor = new Color(45, 45, 45);
    Color accentColor = new Color(0, 170, 255);

    Random random = new Random();

    public TranslationGame() {
        setTitle("Translation Game");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(bgColor);

        JPanel card = new JPanel();
        card.setBackground(panelColor);
        card.setPreferredSize(new Dimension(400, 280));
        card.setLayout(new GridLayout(6, 1, 10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        titleLabel = new JLabel("🎮 بازی ترجمه", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(accentColor);

        wordLabel = new JLabel("", SwingConstants.CENTER);
        wordLabel.setFont(normalFont);
        wordLabel.setForeground(Color.WHITE);

        inputField = new JTextField();
        inputField.setFont(normalFont);
        inputField.setHorizontalAlignment(JTextField.RIGHT);

        checkButton = new JButton("بررسی");
        checkButton.setFont(normalFont);
        checkButton.setBackground(accentColor);
        checkButton.setForeground(Color.WHITE);
        checkButton.setFocusPainted(false);

        resultLabel = new JLabel(" ", SwingConstants.CENTER);
        resultLabel.setFont(normalFont);
        resultLabel.setForeground(Color.LIGHT_GRAY);

        scoreLabel = new JLabel("امتیاز: 0", SwingConstants.CENTER);
        scoreLabel.setFont(normalFont);
        scoreLabel.setForeground(Color.WHITE);

        card.add(titleLabel);
        card.add(wordLabel);
        card.add(inputField);
        card.add(checkButton);
        card.add(resultLabel);
        card.add(scoreLabel);

        add(card);

        nextWord();

        checkButton.addActionListener(e -> checkTranslation());
    }

    void nextWord() {
        index = random.nextInt(englishWords.length);
        wordLabel.setText("ترجمه کن:  " + englishWords[index]);
        inputField.setText("");
    }

    void checkTranslation() {
        String userInput = inputField.getText().trim();

        if (userInput.equals(persianWords[index])) {
            resultLabel.setText("✅ درست");
            score++;
        } else {
            resultLabel.setText("❌ غلط | جواب: " + persianWords[index]);
        }

        scoreLabel.setText("امتیاز: " + score);
        nextWord();
    }

    public static void main(String[] args) {
        new TranslationGame().setVisible(true);
    }
}