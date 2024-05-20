package com.example.heqoa;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class HelloApplication extends Application {

    private int currentQuestionIndex = 0;
    private int score = 0;
    private int correctAnswers = 0; // Track the number of correct answers
    private int totalQuestions = 0; // Track the total number of questions attempted
    private Timer questionTimer;
    private int elapsedTime = 0; // Elapsed time in seconds

    private Label questionLabel;
    private ImageView imageView;
    private RadioButton[] options;
    private Button submitButton;
    private Label feedbackLabel;
    private Label scoreLabel;
    private Label progressLabel; // Label to display progress
    private Button fiftyFiftyButton;
    private Button hintButton;
    private Label timerLabel;
    private Button nextButton;

    private TriviaQuestion[] triviaQuestions = {
            new TriviaQuestion(
                    "Who is the founder of Basotho Nation?",
                    new Image(getClass().getResourceAsStream("/Moshoeshoe.jpg")),
                    new String[]{"Letsie", "King Moshoeshoe I", "Lerotholi", "Moshoeshoe II"},
                    "King Moshoeshoe I"
            ),
            new TriviaQuestion(
                    "What is the official language of Lesotho?",
                    new Image(getClass().getResourceAsStream("/pere.jpg")),
                    new String[]{"English", "Chinese", "Sesotho", "Zulu"},
                    "Sesotho"
            ),
            new TriviaQuestion(
                    "What is the name of the traditional Basotho blanket?",
                    new Image(getClass().getResourceAsStream("/seanamarena.jpg")),
                    new String[]{"Mink", "Delela", "Seanamarena", "Bed"},
                    "Seanamarena"
            ),
            new TriviaQuestion(
                    "Which waterfall in Lesotho is one of the highest single-drop waterfalls in Africa?",
                    new Image(getClass().getResourceAsStream("/Maletsunyane.jpg")),
                    new String[]{"Katse Dam", "Mohale Dam", "Maletsunyane Falls", "Muela Dam"},
                    "Maletsunyane Falls"
            ),
            new TriviaQuestion(
                    "What is the highest point in Lesotho?",
                    new Image(getClass().getResourceAsStream("/Thabana-Ntlenyana.jpg")),
                    new String[]{"Qiloane", "Thabana Ntlenyana", "Mount Moorosi", "Tsikooane"},
                    "Thabana Ntlenyana"
            ),
            new TriviaQuestion(
                    "What is the traditional attire for Basotho men called?",
                    new Image(getClass().getResourceAsStream("/mokorotlo.jpg")),
                    new String[]{"Seshoeshoe", "Mokorotlo", "Jacket", "Tuku"},
                    "Mokorotlo"
            ),
            new TriviaQuestion(
                    "What is the Capital town of Lesotho?",
                    new Image(getClass().getResourceAsStream("/Maseru.jpg")),
                    new String[]{"Leribe", "Mokhotlong", "Maseru", "Quthing"},
                    "Maseru"
            ),

    };

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #2ECC71;"); // Set background color to green
        Scene scene = new Scene(root, 800, 600);

        questionLabel = new Label();
        questionLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #000000; -fx-font-weight: bold;");
        questionLabel.setFont(Font.font("Arial", 20));

        imageView = new ImageView();
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);

        options = new RadioButton[4];
        ToggleGroup toggleGroup = new ToggleGroup();
        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(0, 0, 20, 0)); // Add padding to bottom
        for (int i = 0; i < 4; i++) {
            options[i] = new RadioButton();
            options[i].setToggleGroup(toggleGroup);
            options[i].setStyle("-fx-font-size: 14px;");
            optionsBox.getChildren().add(options[i]);
        }

        submitButton = new Button("Turn in");
        submitButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        submitButton.setOnAction(e -> checkAnswer());

        feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF0000;");

        scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        progressLabel = new Label("Question: 0 / " + triviaQuestions.length);
        progressLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        fiftyFiftyButton = new Button("50/50");
        fiftyFiftyButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        fiftyFiftyButton.setOnAction(e -> useFiftyFifty());

        hintButton = new Button("Hint");
        hintButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        hintButton.setOnAction(e -> showHint());

        timerLabel = new Label("Time: 00:00"); // Initial time
        timerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        nextButton = new Button("Next Question");
        nextButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        nextButton.setOnAction(e -> showQuestion());
        nextButton.setDisable(true); // Disable next button initially

        HBox lifelineBox = new HBox(20);
        lifelineBox.setAlignment(Pos.CENTER);
        lifelineBox.setPadding(new Insets(10));
        lifelineBox.getChildren().addAll(fiftyFiftyButton, hintButton, timerLabel);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        centerBox.getChildren().addAll(questionLabel, imageView, optionsBox, submitButton, nextButton, feedbackLabel);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(scoreLabel, progressLabel);

        root.setTop(lifelineBox);
        root.setCenter(centerBox);
        root.setBottom(buttonBox);

        showQuestion();
        startQuestionTimer();
        animateFlag(); // Adding animation for flag

        primaryStage.setScene(scene);
        primaryStage.setTitle("Lesotho Trivia Game");
        primaryStage.show();
    }

    private void startQuestionTimer() {
        questionTimer = new Timer();
        questionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    elapsedTime++;
                    updateTimerLabel();
                    if (elapsedTime >= 120) {
                        questionTimer.cancel();
                        showTimeUpMessage();
                    }
                });
            }
        }, 0, 1000); // Update every second
    }

    private void showTimeUpMessage() {
        // Implement logic to handle time up scenario
    }

    private void updateTimerLabel() {
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String timeString = String.format("Time: %02d:%02d", minutes, seconds);
        timerLabel.setText(timeString);
    }

    private void useFiftyFifty() {
        // Implement logic to eliminate two incorrect options
        TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];
        String[] optionsText = currentQuestion.getOptions();
        int correctOptionIndex = -1;
        for (int i = 0; i < optionsText.length; i++) {
            if (optionsText[i].equals(currentQuestion.getCorrectAnswer())) {
                correctOptionIndex = i;
                break;
            }
        }
        int optionToKeep = correctOptionIndex;
        while (optionToKeep == correctOptionIndex) {
            optionToKeep = (int) (Math.random() * 4);
        }
        for (int i = 0; i < options.length; i++) {
            if (i != correctOptionIndex && i != optionToKeep) {
                options[i].setVisible(false);
            }
        }
        fiftyFiftyButton.setDisable(true);
    }

    private void showHint() {
        // Implement logic to show a hint for the current question
        TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];
        String correctAnswer = currentQuestion.getCorrectAnswer();
        String hint = "The correct answer starts with: " + correctAnswer.charAt(0);
        feedbackLabel.setText(hint);
        hintButton.setDisable(true);
    }

    private void showQuestion() {
        if (currentQuestionIndex < triviaQuestions.length) {
            TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];
            int questionNumber = currentQuestionIndex + 1; // Calculate question number
            int totalQuestions = triviaQuestions.length; // Total number of questions
            questionLabel.setText("Question " + questionNumber + " of " + totalQuestions + ": " + currentQuestion.getQuestion()); // Display question number and total
            imageView.setImage(currentQuestion.getImage());
            String[] optionsText = currentQuestion.getOptions();
            for (int i = 0; i < optionsText.length; i++) {
                options[i].setText(optionsText[i]);
                options[i].setSelected(false);
                options[i].setVisible(true);
                options[i].setDisable(false); // Re-enable options in case they were disabled from previous questions
            }

            feedbackLabel.setText("");
            submitButton.setDisable(false);
            nextButton.setDisable(true); // Disable next button initially
            fiftyFiftyButton.setDisable(false);
            hintButton.setDisable(false);
            elapsedTime = 0; // Reset the timer
            updateTimerLabel();
            totalQuestions++; // Increment total questions attempted

            // Update progress label
            progressLabel.setText("Question " + questionNumber + " over " + totalQuestions + " questions");
        } else {
            endGame();
        }
    }


    private void checkAnswer() {
        TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];
        RadioButton selectedOption = null;

        // Find the selected option
        for (RadioButton option : options) {
            if (option.isSelected()) {
                selectedOption = option;
                break;
            }
        }

        // If no option is selected, display an error message
        if (selectedOption == null) {
            feedbackLabel.setText("Please select an answer.");
            return;
        }

        // Check if the selected option is correct
        if (selectedOption.getText().equals(currentQuestion.getCorrectAnswer())) {
            score++; // Increment score for correct answer
            correctAnswers++; // Increment correct answers count
            feedbackLabel.setText("Correct! The correct answer is: " + currentQuestion.getCorrectAnswer());
        } else {
            feedbackLabel.setText("Incorrect. The correct answer is: " + currentQuestion.getCorrectAnswer());
        }

        // Move to the next question
        currentQuestionIndex++;
        scoreLabel.setText("Score: " + score);
        submitButton.setDisable(true);
        nextButton.setDisable(false); // Enable next button

        // Disable all options after the user has answered
        for (RadioButton option : options) {
            option.setDisable(true);
        }

        // Update progress label
        progressLabel.setText("Question: " + (currentQuestionIndex + 1) + " / " + triviaQuestions.length);
    }

    private void endGame() {
        double percentage = (double) correctAnswers / totalQuestions * 100; // Calculate percentage

        // Final score summary
        String finalScoreSummary = String.format("Final Score: %d correct out of %d questions.\n", correctAnswers, triviaQuestions.length);

        // User's progress
        int questionsAnswered = currentQuestionIndex + 1; // Add 1 to currentQuestionIndex to get the number of questions answered
        String progressSummary = String.format("You got %d out of %d questions.\n", questionsAnswered, triviaQuestions.length);

        // Percentage
        String percentageSummary = String.format("Percentage: %.2f%%\n", percentage);

        // Concatenate all summaries
        StringBuilder summary = new StringBuilder();
        summary.append(finalScoreSummary)
                .append(progressSummary)
                .append(percentageSummary);

        feedbackLabel.setText(summary.toString());

        // Cancel the question timer
        questionTimer.cancel();

        // Add restart button
        Button restartButton = new Button("Play Again");
        restartButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        restartButton.setOnAction(e -> restartGame());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(scoreLabel, restartButton);

        root.setBottom(buttonBox);
    }



    private void restartGame() {
    }

    private void animateFlag() {
        Image flagImage = new Image(getClass().getResourceAsStream("/flag.png"));
        ImageView flagView = new ImageView(flagImage);
        flagView.setFitWidth(150);
        flagView.setFitHeight(150);

        // Translate transition
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), flagView);
        translateTransition.setFromX(-200); // Start from left side
        translateTransition.setToX(200); // Move to right side
        translateTransition.setAutoReverse(true); // Move back to left
        translateTransition.setCycleCount(TranslateTransition.INDEFINITE); // Repeat indefinitely

        // Scale transition
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), flagView);
        scaleTransition.setToX(1.5); // Scale horizontally by 1.5
        scaleTransition.setToY(1.5); // Scale vertically by 1.5
        scaleTransition.setAutoReverse(true); // Reverse the scaling
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE); // Repeat indefinitely

        // Fade transition
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), flagView);
        fadeTransition.setFromValue(1.0); // Start with full opacity
        fadeTransition.setToValue(0.3); // Fade to 30% opacity
        fadeTransition.setAutoReverse(true); // Reverse the fading
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE); // Repeat indefinitely

        // Parallel transition
        ParallelTransition parallelTransition = new ParallelTransition(flagView,
                translateTransition, scaleTransition, fadeTransition);
        parallelTransition.play();

        // Add the flag to the top-right corner of the screen
        BorderPane.setAlignment(flagView, Pos.TOP_RIGHT);
        BorderPane.setMargin(flagView, new Insets(10));
        root.getChildren().add(flagView);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class TriviaQuestion {
        private String question;
        private Image image;
        private String[] options;
        private String correctAnswer;

        public TriviaQuestion(String question, Image image, String[] options, String correctAnswer) {
            this.question = question;
            this.image = image;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestion() {
            return question;
        }

        public Image getImage() {
            return image;
        }

        public String[] getOptions() {
            return options;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }
    }

    private String getExplanation(TriviaQuestion question) {
        // You can add explanations for each question if you want
        return "";
    }
}
