import java.util.*;

public class chatBot {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String user1 , user2 ;
        String user1msg = "";
        String user2msg = "";

        System.out.print("Enter the user1 name: ");
        user1 = sc.nextLine();
        System.out.print("Enter the user2 name: ");
        user2 = sc.nextLine();

        System.out.println("Chat Started (Type exit to stop the chat)");
        while(!user1msg.equalsIgnoreCase("exit") && !user2msg.equalsIgnoreCase("exit")){
            System.out.println(user1 + ": ");
            user1msg = sc.nextLine();
            if (user1msg.equalsIgnoreCase("exit")) break;
            
            System.out.println( user1 + " says=> " + user1msg);

            System.out.println( user2 + ": ");
            user2msg = sc.nextLine();
            if (user2msg.equalsIgnoreCase("exit"))break;

            System.out.println(user2 + " says=> " + user2msg);
        }
        System.out.println("Chat Ended");
        System.out.println("Thank You :)");
        sc.close();
    }
}
