
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DistributionInterface extends Remote {

    public String postArticle(double price, String author, String title, String body) throws RemoteException;

    public String generateInsights(String username) throws RemoteException;

    public String articleList(String username) throws RemoteException;

    public String downloadArticle(String author_title, String username) throws RemoteException;

    public String addFunds(double amount, String username) throws RemoteException;

    public String purchasedArticleList(String username) throws RemoteException;

    public String readArticle(String input, String username) throws RemoteException;

}