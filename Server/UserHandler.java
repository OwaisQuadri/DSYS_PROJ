
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class UserHandler extends UnicastRemoteObject implements DistributionInterface {

    // init vairables
    final NumberFormat CAD = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

    // Constructor
    public UserHandler(String s) throws RemoteException {
        super();
    }

    @Override
    public String postArticle(double price, String author, String title, String body) {
        // add to a file
        String path = "./Content/" + author + "_" + title + ".txt";
        try {
            File file = new File(path);
            if (file.createNewFile()) {
                // write to file
                FileWriter wr = new FileWriter(path);
                // downloads and price
                wr.append("0 " + price + "\n");
                // body
                wr.append(body);
                // close file
                wr.close();
                return "Article Created : " + title + "\n";
            } else

            {
                return "This Article already exists.\n";
            }
        } catch (IOException e) {
            return "An error occurred.";
        }
    }

    @Override
    public String generateInsights(String username) {
        String output = "Per Article Insights:\n\n";
        // read current test stat file and take note of average, low and high marks
        // list available content
        File availFileFolder = new File("./Content");
        File[] listOfFiles = availFileFolder.listFiles();
        // get transactions accross all content and add up all the revenue generated
        int downloads = 0;
        double revenue = 0;
        for (File file : listOfFiles) {
            String fileName = file.getName();
            String[] authorAndTitle = fileName.substring(0, fileName.length() - 4).split("_");
            String author = authorAndTitle[0];
            String title = authorAndTitle[1];
            if (author.equals(username)) {
                try {
                    // open file
                    Scanner fileReader = new Scanner(file);
                    // read num of downloads and append
                    int thisDownloads = fileReader.nextInt();
                    double thisPrice = fileReader.nextDouble();
                    downloads += thisDownloads;
                    revenue += thisPrice * thisDownloads;
                    fileReader.close();
                    // add to output
                    output += "Title: " + title + "\nAuthor: " + author + "\nDownloads: " + thisDownloads
                            + ", Revenue:" + CAD.format(thisPrice * thisDownloads) + "\n\n";
                } catch (FileNotFoundException e) {
                    System.out.println("File not found somehow");
                }
            }

        }
        output += "\n" + username + " has Served " + downloads + " Verified Financial Articles and generated "
                + CAD.format(revenue) + " in revenues\n";

        return output;
    }

    @Override
    public String addFunds(double amount, String username) throws RemoteException {
        String path = "./UserFunds/" + username + ".txt";
        File funds = new File(path);
        double money = amount;
        try {
            // open file
            Scanner fileReader = new Scanner(funds);
            // read num of downloads and append
            money += fileReader.nextDouble();
            fileReader.close();
            try (FileWriter fw = new FileWriter(path, false);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                out.println(money);
                return "\nAdded " + CAD.format(amount) + " Successfully!\nNew Balance: " + CAD.format(money);
            } catch (IOException e) {
                return "IOException";
            }
        } catch (Exception e) {
            try (FileWriter fw = new FileWriter(path, false);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                out.println(money);
                return "\nAdded " + CAD.format(amount) + " Successfully!\nNew Balance: " + CAD.format(money);
            } catch (IOException f) {
                return "\nIOException\n";
            }
        }
    }

    @Override
    public String articleList(String username) throws RemoteException {
        File userFunds = new File("./UserFunds/" + username + ".txt");
        double funds = 0;
        try {
            // open file
            Scanner fileReader = new Scanner(userFunds);
            // read num of downloads and append
            funds = fileReader.nextDouble();
            fileReader.close();
        } catch (Exception e) {
        }
        String output = "\nCatalog:\nCurrent Balance: " + CAD.format(funds) + "\n\n";
        // read current test stat file and take note of average, low and high marks

        // list available content
        File availFileFolder = new File("./Content");
        File[] listOfFiles = availFileFolder.listFiles();
        // get transactions accross all content and add up all the revenue generated
        for (File file : listOfFiles) {
            String fileName = file.getName();
            String[] authorAndTitle = fileName.substring(0, fileName.length() - 4).split("_");
            String author = authorAndTitle[0];
            String title = authorAndTitle[1];
            try {
                // open file
                Scanner fileReader = new Scanner(file);
                // read num of downloads and append
                int thisDownloads = fileReader.nextInt();
                double thisPrice = fileReader.nextDouble();
                fileReader.close();
                // add to output
                output += "Title: " + title + "\nAuthor: " + author + "\nDownloads: " + thisDownloads
                        + "\nPrice: " + CAD.format(thisPrice) + "\n\n";
            } catch (FileNotFoundException e) {
                System.out.println("File not found somehow");
            }
        }
        return output;
    }

    @Override
    public String downloadArticle(String author_title, String username) throws RemoteException {
        // check ./UserFunds/username.txt for the fund amount
        File userFunds = new File("./UserFunds/" + username + ".txt");
        double funds = 0;
        try {
            // open file
            Scanner fileReader = new Scanner(userFunds);
            // read num of downloads and append
            funds = fileReader.nextDouble();
            fileReader.close();
        } catch (Exception e) {
            return "\nInsufficient Funds.\n";
        }

        // get downloads on line 0 and price on line 1 from ./Content/author_title.txt
        File article = new File("./Content/" + author_title + ".txt");
        int downloads = 0;
        double price = 0;
        String body = "";
        try {
            // open file
            Scanner fileReader = new Scanner(article);
            // read num of downloads and append
            downloads = fileReader.nextInt();
            price = fileReader.nextDouble();
            while (fileReader.hasNext()) {
                body += fileReader.nextLine() + "\n";
            }
            fileReader.close();
        } catch (Exception e) {
            return "\nNo Such Article Exists\n";
        }
        // if funds>price, append author_title to ./UserPurchases/username.txt
        if (funds >= price) {
            // append author_title to ./UserPurchases/username.txt
            try (FileWriter fw = new FileWriter("./UserPurchases/" + username + ".txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                out.println(author_title);
            } catch (IOException e) {
                // exception handling left as an exercise for the reader
            }
            // rewrite funds with funds=funds-price
            try (FileWriter fw = new FileWriter("./UserFunds/" + username + ".txt", false);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                out.println((funds - price));
            } catch (IOException e) {
                // exception handling left as an exercise for the reader
            }
            // rewrite author_title with downloads+1 and revenue
            try (FileWriter fw = new FileWriter("./Content/" + author_title + ".txt", false);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                out.print((downloads + 1) + " " + (price));
                out.print(body);
            } catch (IOException e) {
                // exception handling left as an exercise for the reader
            }
            return "\nSuccessfully acquired " + author_title + "\n";

        } else {// else return "insufficient funds"
            return "\nInsufficient Funds.\n";
        }
    }

    @Override
    public String purchasedArticleList(String username) throws RemoteException {
        String output = "";
        Map<String, Integer> D = new HashMap<String, Integer>();
        File purchased = new File("./UserPurchases/" + username + ".txt");
        try (Scanner fr = new Scanner(purchased);) {
            while (fr.hasNext()) {
                D.put(fr.nextLine(), 0);
            }
        } catch (Exception e) {
            return "No Articles Purchased";
        }
        for (String article : D.keySet()) {
            output += article + "\n";
        }
        return output;
    }

    @Override
    public String readArticle(String input, String username) throws RemoteException {
        boolean owns = false;
        String[] purchased = purchasedArticleList(username).split("\n");
        for (String aName : purchased) {
            if (aName.equals(input)) {
                owns = true;
            }
        }
        if (owns) {
            File f = new File("./Content/" + input + ".txt");
            String output = "\nArticle Contents:\n\n";
            try {
                Scanner fr = new Scanner(f);
                fr.nextLine();
                while (fr.hasNext()) {
                    output += fr.nextLine() + "\n";
                }
                fr.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
            return output;
        } else {
            return "You do not have access to this file";
        }
    }
}