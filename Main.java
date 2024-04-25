package quiz1;
import quiz2.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Timer;
import java.util.List;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
interface q1{
    void run();
}
abstract class q2{
    public abstract void run();
}
class OptionsPanel extends JPanel {
    private JLabel questionLabel;
    private ButtonGroup buttonGroup;
    private List<JRadioButton> optionButtons;
    private Question question;
    private JavaQuiz javaQuiz;

    public OptionsPanel(Question question, JavaQuiz javaQuiz) {
        this.question = question;
        this.javaQuiz = javaQuiz;
        createOptionsPanel();
        setOpaque(false); // Set the panel to be non-opaque
    }

    private void createOptionsPanel() {
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());

        JLabel questionLabel = new JLabel(question.getQuestion());
        questionLabel.setFont(questionLabel.getFont().deriveFont(Font.PLAIN, 20));
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        contentPanel.add(questionLabel, gbc);

        gbc.gridy++;
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 0, 0));
        optionsPanel.setOpaque(false);
        optionButtons = new ArrayList<>();
        buttonGroup = new ButtonGroup();
        for (String option : question.getOptions()) {
            JRadioButton optionButton = new JRadioButton(option);
            optionButton.setFont(optionButton.getFont().deriveFont(Font.PLAIN, 20));
            optionButton.setOpaque(false);
            optionButton.setHorizontalAlignment(SwingConstants.CENTER);
            optionsPanel.add(optionButton);
            optionButtons.add(optionButton);
            buttonGroup.add(optionButton);
        }
        contentPanel.add(optionsPanel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        JButton previousButton = new JButton("Previous");
        previousButton.addActionListener(e -> javaQuiz.previousQuestion());
        buttonPanel.add(previousButton);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            String userAnswer = getSelectedOption();
            if (userAnswer != null) {
                if (userAnswer.equals(question.getAnswer())) {
                    javaQuiz.updateScore(true);
                }
                javaQuiz.nextQuestion();
            } else {
                JOptionPane.showMessageDialog(null, "Please select an answer.");
            }
        });
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton("Skip");
        cancelButton.addActionListener(e -> javaQuiz.nextQuestion());
        buttonPanel.add(cancelButton);

        contentPanel.add(buttonPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }
    public String getSelectedOption() {
        for (JRadioButton button : optionButtons) {
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }
}

class TimerPanel extends JPanel {
    private JLabel timerLabel;
    private JavaQuiz javaQuiz;

    public TimerPanel(JavaQuiz javaQuiz) {
        this.javaQuiz = javaQuiz;
        createTimerPanel();
        setOpaque(false); // Set the panel to be non-opaque
    }

    private void createTimerPanel() {
        setLayout(new BorderLayout());
        timerLabel = new JLabel();
        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.BOLD, 16));
        timerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 5), // Add thin black border
                BorderFactory.createEmptyBorder(0, 0, 0, 30))); // Add padding inside the border
        add(timerLabel, BorderLayout.WEST);
    }

    public void updateTimer(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        timerLabel.setText("Time Left: " + String.format("%d:%02d", minutes, seconds));
    }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
class BackgroundImageLoadException extends Exception {
    public BackgroundImageLoadException(String message) {
        super(message);
    }
}
class JavaQuiz extends q2 implements Runnable,q1 {
    private List<Question> questions;
    private JFrame frame;
    private Timer timer;
    private int score = 0;
    private int currentQuestionIndex = 0;
    private JPanel questionPanel;
    private int totalSeconds = 120; // 10 minutes in seconds
    private List<Boolean> answeredQuestions; // List to keep track of answered questions

    public JavaQuiz(List<Question> questions) {
        this.questions = questions;
        this.timer = new Timer();
        this.answeredQuestions = new ArrayList<>(Collections.nCopies(questions.size(), false));
    }
    @Override
    public void run() {
        JOptionPane.showMessageDialog(null, "Welcome to Java Quiz!");

        frame = new JFrame("Java Quiz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create background panel
        BackgroundPanel backgroundPanel;
        try {
            BufferedImage image = ImageIO.read(getClass().getResource("background_image.jpg"));
            if (image == null) {
                throw new BackgroundImageLoadException("Failed to load background image");
            }
            backgroundPanel = new BackgroundPanel(image);
            backgroundPanel.setLayout(new BorderLayout());
            frame.setContentPane(backgroundPanel);
        } catch (IOException | BackgroundImageLoadException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return;
        }

        // Create question panel
        questionPanel = new JPanel(new BorderLayout());
        questionPanel.setOpaque(false);
        backgroundPanel.add(questionPanel, BorderLayout.CENTER);
        nextQuestion();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void nextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishQuiz();
            return;
        }

        Question question = questions.get(currentQuestionIndex++);
        showQuestion(question);
        updateTimer();
    }

    public void previousQuestion() {
        if (currentQuestionIndex > 1) {
            currentQuestionIndex -= 2;
            Question question = questions.get(currentQuestionIndex++);
            showQuestion(question);
            updateTimer();
        }
    }
    private void showQuestion(Question question) {
        questionPanel.removeAll();
        OptionsPanel optionsPanel = new OptionsPanel(question, this);
        TimerPanel timerPanel = new TimerPanel(this);
        questionPanel.add(optionsPanel, BorderLayout.CENTER);
        questionPanel.add(timerPanel, BorderLayout.NORTH); // or BorderLayout.SOUTH, BorderLayout.EAST, BorderLayout.WEST

        questionPanel.revalidate();
        questionPanel.repaint();
    }
    public void finishQuiz() {
        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new GridBagLayout());

        JLabel scoreLabel = new JLabel("Quiz finished! Your final score: " + score + "/" + (questions.size() * 2));
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 24)); // Increase font size and set bold
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the label text

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Center the label horizontally and vertically
        gbc.insets = new Insets(10, 10, 10, 10);

        frame.getContentPane().add(scoreLabel, gbc);
        frame.revalidate();
        frame.repaint();
    }


    public void updateScore(boolean correct) {
        int currentIndex = currentQuestionIndex - 1;
        if (!answeredQuestions.get(currentIndex)) {
            if (correct) {
                score += 2;
            }
            answeredQuestions.set(currentIndex, true);
        }
    }

    private void updateTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (totalSeconds > 0) {
                    totalSeconds--;
                    ((TimerPanel) questionPanel.getComponent(1)).updateTimer(totalSeconds);
                } else {
                    timer.cancel();
                    finishQuiz();
                }
            }
        }, 0, 1600); // Update timer every 5 seconds (slow motion)
    }
}

public class Main {
    public static void main(String[] args) throws IOException{
        List<Question> javaQuestions = new ArrayList<>();
        javaQuestions.add(new Question("What is the purpose of the static keyword in Java?", Arrays.asList("It indicates that a variable or method belongs to the class rather than instances of the class", " It denotes a variable that can be modified at runtime", " It specifies that a class cannot be instantiated","It indicates that a method cannot be overridden"), "It indicates that a variable or method belongs to the class rather than instances of the class"));
        javaQuestions.add(new Question("In Java, which keyword is used to create a new instance of a class?", Arrays.asList("new", "class", "this", "extends"), "new"));
        javaQuestions.add(new Question("Which of the following statements is true about Java's garbage collection mechanism?", Arrays.asList(" Garbage collection can be explicitly invoked using the System.gc() method", "Garbage collection guarantees that an object will be garbage collected as soon as it becomes unreachable", "Garbage collection eliminates the need for manual memory management in Java programs", "Garbage collection can only reclaim memory occupied by unreachable objects"), "Garbage collection eliminates the need for manual memory management in Java programs"));
        javaQuestions.add(new Question("Which of the following is NOT a valid access modifier in Java?", Arrays.asList("public", "protected", "global", "private"), "global"));
        javaQuestions.add(new Question("What are the limitations of generics in Java?", Arrays.asList("Generics cannot be used with primitive data types", "Generics cannot guarantee type safety at runtime", "Generics cannot be used with arrays", "All of the above"), "All of the above"));

        JavaQuiz javaQuiz = new JavaQuiz(javaQuestions);
        javaQuiz.run();
    }
}