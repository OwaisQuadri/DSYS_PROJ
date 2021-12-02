
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.text.NumberFormat;
import java.util.*;

import org.w3c.dom.UserDataHandler;

public class FinanSea {

    // initialize
    // constant
    final static String[] USERS = { "admin" };
    final static String[] PASSWORDS = { "admin" };
    final static Scanner sc = new Scanner(System.in);
    // variables

    public static void main(String argv[]) {
        // make sure no arguements are passed
        if (argv.length != 0) {
            System.out.println("Usage: java -Djava.security.policy=policy.txt FinanSea");
            System.exit(0);
        }
        // prompt user for username
        System.out.print("Username: ");
        String username = sc.nextLine();
        // prompt user for password
        System.out.print("Password: ");
        String password = sc.nextLine();
        // verify login
        boolean login = false;
        // check the constant arrays of usernames and corresponding passwords for match
        for (int i = 0; i < USERS.length; i++) {
            if (username.equalsIgnoreCase(USERS[i])) {// ignore case on username only
                if (password.equals(PASSWORDS[i])) {
                    login = true;// verified
                }
            }
        }
        // if login not verified, exit
        if (!login) {
            System.out.println("Incorrect username or password, try again later");
            System.exit(0);
        }
        // welcome the verified user
        System.out.println("\nWelcome to FinanSea Admin : " + username);
        // show menu
        displayMenu();
    }

    /*
     * Method name: displayMenu ; accepts: no parameters ; returns: void ;
     *
     * purpose: show user possible actions and guide user towards those actions when
     * selected. Then send user to perform the selected action.
     */
    private static void displayMenu() {
        // command line menu
        System.out.println("1. Run Server for Users");
        System.out.println("2. Statistics");
        System.out.println("3. Exit");
        System.out.println("please enter a number from the above selection");
        String input = sc.nextLine();
        // switch statement on input
        switch (input) {
            case "1":
                // run server to allow downloads
                runServer();

                break;
            case "2":
                // Print Usage Statistics (Downloads + Funds Transacted)
                printStats();
                break;
            case "3":
                System.out.println("Exiting Admin Portal ... ");
                System.exit(0);
                break;
            default:
                // invalid entry
                System.out.println("Invalid entry : please enter a number from the available selection\n");
                break;

        }
        displayMenu();
    }

    /*
     * Method name: runServer ; accepts: no parameters ; returns: void ;
     *
     * purpose:
     * 
     */
    private static void runServer() {

        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }
            DistributionInterface di = (DistributionInterface) new UserHandler("FinanSea");
            Naming.rebind("//127.0.0.1/FinanSea", di);
            // waiting for students
            System.out.println(
                    "Download requests are now being accepted\n"
                            + "Type \"exit\" to stop the server.");
            while (sc.nextLine().equalsIgnoreCase("exit")) {
                System.out.println("Exiting Admin Portal ... ");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println(
                    "Exiting invalid security or RMI unavailable\nUsage: java -Djava.security.policy=policy.txt FinanSea");
        }

    }

    /*
     * Method name: printStats ; accepts: no parameters ; returns: void ;
     *
     * purpose:
     */
    private static void printStats() {
        // read current test stat file and take note of average, low and high marks
        final NumberFormat CAD = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        // list available content
        File availFileFolder = new File("./Content");
        File[] listOfFiles = availFileFolder.listFiles();
        // get transactions accross all content and add up all the revenue generated
        int downloads = 0;
        double revenue = 0;
        for (File file : listOfFiles) {
            try {
                Scanner fileReader = new Scanner(file);
                int d = fileReader.nextInt();
                double p = fileReader.nextDouble();
                downloads += d;
                revenue += p * d;
                fileReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found somehow");
            }

        }
        System.out.println("\nFinanSea has Served " + downloads + " Verified Financial Articles and generated "
                + CAD.format(revenue) + " in revenues for its creators!\n");
    }

}