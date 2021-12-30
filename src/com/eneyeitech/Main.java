package com.eneyeitech;

import java.io.*;
import java.util.*;

public class Main {

    private static Scanner scanner;
    private static int nn = 100;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        CardManager manager = new CardManager();

        //createCards(manager);
        //testUser(manager);
        menu(manager);

    }

    public static void menu(CardManager manager) {
        String selection;
        boolean stop = false;
        do {
            System.out.println("Input the action (add, remove, import, export, ask, exit):");
            selection = scanner.nextLine();
            switch (selection.toLowerCase()){
                case "add":
                    addCard(manager);
                    break;
                case "remove":
                    removeCard(manager);
                    break;
                case "import":
                    importCards(manager);
                    break;
                case "export":
                    exportCards(manager);
                    break;
                case "ask":
                    askUser(manager);
                    break;
                case "exit":
                    System.out.println("Bye bye!");
                    stop = true;
                    break;
                default:

            }
        } while (!stop);
    }

    public static void removeCard(CardManager manager){
        System.out.println("Which card?");
        String term = scanner.nextLine();
        Map<String, Card> cardMap = manager.getFlashCards();
        if(doesTermExist(cardMap, term)) {
            manager.removeCard(term);
            System.out.println("The card has been removed.");
        } else {
            System.out.printf("Can't remove \"%s\": there is no such card.\n", term);
        }
    }

    public static void importCards(CardManager manager){
        int count = 0;
        String fileName = "C:\\Users\\eneye\\Downloads\\flashcards.txt";
        System.out.println("File name:");
        fileName = scanner.nextLine();
        File file = new File(fileName);
        Map<String, Card> cardMap;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return;
        }
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] lines = line.split(":");
                cardMap = manager.getFlashCards();
                Card card = new Card(++nn, new Term(lines[0]), new Definition(lines[1]));
                if (doesTermExist(cardMap, lines[0])) {
                    manager.removeCardFromList(lines[0]);
                    manager.addCard(card);
                } else {
                    manager.addCard(card);
                }
                manager.addFlashCard(card);
                count++;
            }
        } catch (IOException e){
            System.out.println("File not found");
        }
        System.out.printf("%s cards have been loaded.\n", count);

    }

    public static void exportCards(CardManager manager){
        System.out.println("File name:");
        String fileName = scanner.next();
        String separator = System.getProperty("line.separator");
        List<Card> list = manager.getCards();
        File file = new File(fileName);
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (Card c : list) {
            builder.append(c.getTerm()+":"+c.getDefinition()+separator);
            count++;
        }
        try{
            Writer writer = new BufferedWriter(new FileWriter(file));
            writer.write(builder.toString());
            writer.close();

        }catch (IOException e){

        }
        System.out.printf("%s cards have been saved.\n", count);
    }

    public static void createCards(CardManager manager) {
        System.out.println("Input the number of cards:");
        int noOfCards = Integer.parseInt(scanner.nextLine());
        int n = 0;
        Map<String, Card> cards;
        boolean success = false;

        while (noOfCards != n) {

            n++;

            cards = manager.getFlashCards();
            System.out.printf("Card #%s\n", n);
            String term = scanner.nextLine();
            while (doesTermExist(cards, term)) {
                System.out.printf("The term \"%s\" already exists. Try again:\n",term);
                term = scanner.nextLine();
            }
            System.out.printf("The definition for card #%s\n", n);
            String definition = scanner.nextLine();
            while (doesDefinitionExist(cards, definition)) {
                System.out.printf("The definition \"%s\" already exists. Try again:\n",definition);
                definition = scanner.nextLine();
            }

            Card c = manager.createCard(n, term, definition);
            //manager.addCard(c);
            manager.addCard(c);
            manager.addFlashCard(c);




        }
    }

    public static void addCard(CardManager manager) {
            Map<String, Card> cards;
            nn++;
            cards = manager.getFlashCards();
            System.out.printf("The Card:\n", nn);
            String term = scanner.nextLine();
            String definition = "";
            if (doesTermExist(cards, term)) {
                System.out.printf("The card \"%s\" already exists.\n",term);
                return;
            } else {
                System.out.printf("The definition of the card:\n", nn);
                definition = scanner.nextLine();
                if (doesDefinitionExist(cards, definition)) {
                    System.out.printf("The definition \"%s\" already exists.\n", definition);
                    //definition = scanner.nextLine();
                    return;
                }
            }
            Card c = manager.createCard(nn, term, definition);
            //manager.addCard(c);
            manager.addCard(c);
            manager.addFlashCard(c);
            System.out.printf("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
    }

    public static boolean doesTermExist(Map<String, Card> cardMap, String term){
        return cardMap.containsKey(term);
    }

    public static boolean doesDefinitionExist(Map<String, Card> cardMap, String definition){

        for(Map.Entry<String, Card> entry : cardMap.entrySet()){
            if(entry.getValue().getDefinition().equals(definition)){
                return true;
            }
        }
        return false;
    }

    public static Card getRightCard(Map<String, Card> cardMap, String definition){
        for(Map.Entry<String, Card> entry : cardMap.entrySet()){
            if(entry.getValue().getDefinition().equals(definition)){
                return entry.getValue();
            }
        }
        return null;
    }

    public static void testUser(CardManager manager) {
        List<Card> cards = manager.getCards();
        Map<String, Card> cardMap = manager.getFlashCards();

        if (cards.size() > 0) {
            for (Card c : cards) {
                System.out.printf("Print the definition of \"%s\"\n", c.getTerm());
                String answer = scanner.nextLine();

                boolean isCorrect = manager.checkAnswer(c, answer);

                if (isCorrect) {
                    System.out.println("Correct!");
                } else {
                    if (doesDefinitionExist(cardMap, answer)) {
                        Card cc = cardMap.get(answer);
                        System.out.printf("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\"\n", c.getDefinition(), cc.getTerm());

                    } else {
                        System.out.printf("Wrong. The right answer is \"%s\"\n", c.getDefinition());
                    }
                }
            }
        }
    }

    public static void askUser(CardManager manager) {
        List<Card> cards = manager.getCards();
        Map<String, Card> cardMap = manager.getFlashCards();
        System.out.println("How many times to ask?");
        int no = Integer.parseInt(scanner.nextLine());
        if (cards.size() > 0) {
            for(int i = 0; i < no; i++){
            for (Card c : cards) {
                System.out.printf("Print the definition of \"%s\"\n", c.getTerm());
                String answer = scanner.nextLine();

                boolean isCorrect = manager.checkAnswer(c, answer);

                if (isCorrect) {
                    System.out.println("Correct!");
                } else {
                    if (doesDefinitionExist(cardMap, answer)) {
                        Card cc = getRightCard(cardMap, answer);
                        System.out.printf("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\"\n", c.getDefinition(), cc.getTerm());

                    } else {
                        System.out.printf("Wrong. The right answer is \"%s\"\n", c.getDefinition());
                    }
                }
            }
        }
        }
    }
}

abstract class Text {
    private String text;

    public Text(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

class Term extends Text{

    public Term(String text) {
        super(text);
    }
}

class Definition extends Text{

    public Definition(String text) {
        super(text);
    }
}

class Card {
    private int cardNumber;
    private Text front;
    private Text back;

    public Card(int cardNumber, Term front, Definition back) {
        this.front = front;
        this.back = back;
        this.cardNumber = cardNumber;
    }

    public int getCardNumber(){
        return cardNumber;
    }

    public String getTerm() {
        return front.getText();
    }

    public String getDefinition() {
        return back.getText();
    }

    @Override
    public String toString() {
        return String.format("Card:\n%s<--->Definition:\n%s\n", getTerm(), getDefinition());
    }
}

class CardManager {

    private List<Card> cards = new ArrayList<>();
    private Map<String, Card> flashCards = new HashMap<>();

    public Card createCard(int cardNumber, String term, String definition){
        return new Card(cardNumber, new Term(term), new Definition(definition));
    }

    public boolean addCard(Card card) {
        if(cards.size()>1){
            cards.add(0, card);
            return true;
        }else{
            return cards.add(card);
        }
    }

    public void addFlashCard(Card card) {
        flashCards.put(card.getTerm(), card);
    }

    public Map<String, Card> getFlashCards(){
        return new HashMap<>(flashCards);
    }

    public Card findFlashCard(String definition){
        return flashCards.get(definition);
    }

    public Card findFlashCardbyId(int no){
        for(Map.Entry<String, Card> entry : flashCards.entrySet()){
            if(entry.getValue().getCardNumber() == no){
                return entry.getValue();
            }
        }
        return null;
    }

    public Card findCard(int no){
        for (Card c : cards) {
            if (c.getCardNumber() == no) {
                return c;
            }
        }
        return null;
    }

    public boolean removeCard(String term) {
        flashCards.remove(term);
        return true;
    }

    public void removeCardFromList(String term){
        for(int i = 0; i < cards.size(); i++){
            if(cards.get(i).getTerm().equals(term)) {
                cards.remove(i);
            }
        }
    }

    public List<Card> getCards(){
        return new ArrayList<>(cards);
    }

    public boolean checkAnswer(Card c, String answer) {
        return answer.equals(c.getDefinition());
    }

    public void print(){
        for (Card c : cards){
            System.out.println(c);
        }
    }

    public void printR(){
        for(Map.Entry<String, Card> entry : flashCards.entrySet()){
            System.out.printf("%s --> %s\n", entry.getKey(), entry.getValue().getDefinition());
        }
    }
}