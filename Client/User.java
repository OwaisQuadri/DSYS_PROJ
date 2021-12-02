
import java.rmi.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class User {
    // init constants
    final static String[] USERS = { "john", "emily", "ali", "justin", "user" };
    final static String[] PASSWORDS = { "john", "emily", "ali", "justin", "user" };
    final static String[] USERS_V = { "owais", "taha", "austin", "mitchell", "creator" };
    final static String[] PASSWORDS_V = { "owais", "taha", "austin", "mitchell", "creator" };
    // create scanner
    static Scanner sc = new Scanner(System.in);

    public static void main(String argv[]) {
        // ensure proper usage (no args)
        if (argv.length != 0) {
            System.out.println("Usage: java Student");
            System.exit(0);
        }
        // ask user for username
        System.out.print("Username: ");
        String username = sc.nextLine();
        // and password
        System.out.print("Password: ");
        String password = sc.nextLine();
        // credential validation
        boolean login = false;
        boolean vLogin = false;
        for (int i = 0; i < USERS_V.length; i++) {
            // check corresponding passwords
            if (username.equals(USERS_V[i])) {
                if (password.equals(PASSWORDS_V[i])) {
                    vLogin = true;
                    break;
                }
            }
        }
        if (!vLogin) {
            for (int i = 0; i < USERS.length; i++) {
                // check corresponding passwords
                if (username.equals(USERS[i])) {
                    if (password.equals(PASSWORDS[i])) {
                        login = true;
                        break;
                    }
                }
            }
        }

        // quit if login credentials incorrect
        if (!login && !vLogin) {
            System.out.println("Incorrect username or password");
            System.out.println("Usage: java Student");
            System.exit(0);
        }
        // connect to supervisor server
        DistributionInterface di = null;
        try {
            String name = "//127.0.0.1/FinanSea";
            // configure RMI
            di = (DistributionInterface) Naming.lookup(name);
            // client stub ready
            // does student just want to see their own statistics or take a test
            System.out.println("Welcome to FinanSea: " + username);
            displayMenu(di, username, vLogin);

        } catch (Exception e) {
            // Creator forgot to accept submissions
            System.out.println(
                    "FinanSea server failed to connect via rmiregistry, try again later.");
            sc.close();
            System.exit(0);
        }

        // close scanner
        sc.close();

    }
    private static void displayMenu(DistributionInterface di, String username, boolean verified) {
        System.out.println("\nMain Menu");
        if (verified) {
            String exitMsg = "Exiting Verified Creator Portal ... ";
            // command line menu
            System.out.println("1. Create Post");
            System.out.println("2. View Insights");
            System.out.println("3. Exit");
            System.out.println("please enter a number from the above selection");
            String input = sc.nextLine();
            // switch statement on input
            switch (input) {
                case "1":
                    // create post
                    System.out.println("Create Post");
                    createPost(di, username);
                    break;
                case "2":
                    // view insights
                    System.out.println("View Insights");
                    viewInsights(di, username);
                    break;
                case "3":
                    System.out.println(exitMsg);
                    System.exit(0);
                    break;
                default:
                    // invalid entry
                    System.out.println("Invalid entry : please enter a number from the available selection\n");
                    break;

            }
            displayMenu(di, username, verified);
        } else {
            String exitMsg = "Exiting User Portal ... ";
            // command line menu
            System.out.println("1. Add Funds");
            System.out.println("2. Catalog");
            System.out.println("3. Read");
            System.out.println("4. Exit");
            System.out.println("please enter a number from the above selection");
            String input = sc.nextLine();
            // switch statement on input
            switch (input) {
                case "1":
                    // run server to allow downloads
                    fundsMenu(di, username);
                    break;
                case "2":
                    // Print Usage Statistics (Downloads + Funds Transacted)
                    catalogMenu(di, username);
                    break;
                case "3":
                    // read one of the articles

                    readArticlesMenu(di, username);
                    break;
                case "4":
                    System.out.println(exitMsg);
                    System.exit(0);
                    break;
                default:
                    // invalid entry
                    System.out.println("Invalid entry : please enter a number from the available selection\n");
                    break;

            }
            displayMenu(di, username, verified);
        }

    }

    private static void viewInsights(DistributionInterface di, String username) {
        try {
            System.out.println(di.generateInsights(username));
        } catch (RemoteException re) {
            System.out.println("FinanSea Server is Down");
        }
    }

    private static void readArticlesMenu(DistributionInterface di, String username) {
        // command line menu
        System.out.println("\nBought Articles:");
        try {
            System.out.println(di.purchasedArticleList(username));
        } catch (RemoteException e) {
            System.out.println("FinanSea Server is Down");
        }
        System.out.println("Please enter an article from the above selection (or '_' to go back)");
        String input = sc.nextLine();
        if (input.equals("_")) {
            System.out.println("Returning to Main Menu ...");
            displayMenu(di, username, false);
        } else {
            try {
                System.out.println(di.readArticle(input, username));
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        readArticlesMenu(di, username);
    }

    private static void catalogMenu(DistributionInterface di, String username) {
        try {
            System.out.println(di.articleList(username));
        } catch (RemoteException e) {
            System.out.println("FinanSea Server is Down");
        }

        // command line menu
        System.out.println("1. Buy an Article");
        System.out.println("2. Back");
        System.out.println("please enter a number from the above selection");
        String input = sc.nextLine();
        // switch statement on input
        switch (input) {
            case "1":
                // download an article
                System.out.println("Please identify the article in the following format:\n'Author_Title'");
                try {
                    System.out.println(di.downloadArticle(sc.nextLine(), username));
                } catch (RemoteException e) {
                    System.out.println("FinanSea Server is Down");
                }

                break;
            case "2":
                System.out.println("Returning to Main Menu ...");
                displayMenu(di, username, false);
                break;
            default:
                // invalid entry
                System.out.println("Invalid entry : please enter a number from the available selection\n");
                break;

        }
        catalogMenu(di, username);
    }

    private static void fundsMenu(DistributionInterface di, String username) {
        // command line menu

        System.out.print("\nHow much would you like to add? (CAD)");
        double input = Double.parseDouble(sc.nextLine());
        System.out.print("Card Num: ");
        String ccn = sc.nextLine();
        System.out.print("Expiry Date: ");
        String exp = sc.nextLine();
        System.out.print("Security Code: ");
        String sCode = sc.nextLine();

        try {
            System.out.println(di.addFunds(input, username));
        } catch (RemoteException e) {
            System.out.println("FinanSea Server is Down");
        }

    }

    private static void createPost(DistributionInterface di, String username) {
        // ask what the test should be named
        String author = username;
        System.out.print("\nTitle (cannot include '_'): ");
        String title = sc.nextLine();
        // cannot have _ in the name
        if (title.contains("_")) {
            System.out.println("Please do not use '_' in the Title\nReturning to Main Menu ... ");
            return;
        }

        String body = "";
        String eof = "-" + author;
        System.out.println("Body (enter '" + eof + "' when done):");
        while (true) {
            String line = sc.nextLine();
            if (line.equals(eof))
                break;
            body += line + "\n";
        }
        // price
        System.out.print("Please enter the price to download this article: ");
        double price = sc.nextDouble();
        try {
            System.out.println(di.postArticle(price, author, title, body));
        } catch (RemoteException e) {
            System.out.println("FinanSea Server is Down");
        }

    }

    public static double startTest(String testName, ArrayList<ArrayList<String>> questions) {
        // tell them the name of quiz
        System.out.println("Test " + testName + " is starting now:");
        int total = 0;
        int correct = 0;
        boolean isTest = false;
        for (ArrayList<String> q : questions) {
            System.out.println(q.get(0));// display q's
            for (int i = 1; i < q.size(); i++) {// display answers
                String send = q.get(i);
                if (send.charAt(0) == '!') {// remove ! from correct answers
                    send = send.substring(1);
                    isTest = true;
                }
                send = i + ". " + send;
                System.out.println(send);// display question and answers after parsing and serializing
            }
            if (!isTest) {// if no correct answers specified (!)
                correct++;
            }
            // accept answer
            int ansIndex = Integer.parseInt(sc.nextLine());
            // check for correct
            if (q.get(ansIndex).charAt(0) == '!') {
                correct++;
                total++;
            } else {
                total++;
            }

        }
        // send result to user
        double c = (double) correct;
        double t = (double) total;
        double result = c / t;
        DecimalFormat percent = new DecimalFormat("#0.00 %");// format output to user
        System.out.println("Your score was : " + percent.format(result) + "\n");
        return result;

    }
}