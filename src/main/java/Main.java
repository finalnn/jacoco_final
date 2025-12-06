import java.util.*;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.LocalDate;
import model.*;
import service.*;

public class Main {
	/*
    private static final int BORROW_DAYS = 28;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ======= Default Books =======
        Book b1 = new Book("Java Basics", "James Gosling", "001");
        Book b2 = new Book("Python Intro", "Guido Rossum", "002");
        Book b3 = new Book("C++ Fundamentals", "Bjarne Stroustrup", "003");
        Book b4 = new Book("Data Structures", "Mark Allen", "004");
        Book b5 = new Book("Algorithms", "Robert Sedgewick", "005");
        Book b6 = new Book("Machine Learning", "Tom Mitchell", "006");
        List<Book> books = new ArrayList<>(Arrays.asList(b1,b2,b3,b4,b5,b6));

        // ======= Default CDs =======
        CD cd1 = new CD("Classical Hits", "Beethoven", "CD001");
        CD cd2 = new CD("Rock Classics", "Queen", "CD002");
        CD cd3 = new CD("Jazz Essentials", "Miles Davis", "CD003");
        CD cd4 = new CD("Pop Top", "Taylor Swift", "CD004");
        List<CD> cds = new ArrayList<>(Arrays.asList(cd1, cd2, cd3, cd4));

        // ======= Default Users =======
        List<User> users = new ArrayList<>();
        User u1 = new User("Noor", "halawad257@gmail.com");
        User u2 = new User("Hala", "s12217844@stu.najah.edu");
        User u3 = new User("Hala", "halaawwad455@gmail.com");
        User u4 = new User("Sara", "sara@gmail.com");
        users.addAll(Arrays.asList(u1,u2,u3,u4));

        // ======= Services =======
        FineService fineService = new FineService();
        LoanService loanService = new LoanService();

        // ======= Dotenv and Email Service =======
        Dotenv dotenv = Dotenv.load();
        String emailUser = dotenv.get("EMAIL_USERNAME");
        String emailPass = dotenv.get("EMAIL_PASSWORD");
        String adminUsername = dotenv.get("ADMIN_USERNAME");
        String adminPassword = dotenv.get("ADMIN_PASSWORD");

        EmailService emailService = new EmailService(emailUser, emailPass);
        MediaEmailNotifier notifier = new MediaEmailNotifier(emailService);

        // ======= Admin =======
        Admin admin = new Admin(adminUsername, adminPassword);

        // ======= Borrow Books & CDs Automatically =======
        Loan l1 = loanService.createLoan(b1, u1);
        Loan l2 = loanService.createLoan(cd1, u2);
        Loan l3 = loanService.createLoan(b2, u3);
        Loan l4 = loanService.createLoan(cd2, u3);

        // تعديل المواعيد للقروض لتوليد غرامات
        try {
            var field = Loan.class.getDeclaredField("dueDate");
            field.setAccessible(true);
            field.set(l1, LocalDate.now().minusDays(29));
            field.set(l2, LocalDate.now().minusDays(8));
            field.set(l3, LocalDate.now().minusDays(30));
            field.set(l4, LocalDate.now().minusDays(8));
        } catch(Exception ignored){}

        // إضافة الغرامات تلقائياً
        if(l1.isOverdue()) fineService.addFine(u1, l1.getFineAmount());
        if(l2.isOverdue()) fineService.addFine(u2, l2.getFineAmount());
        if(l3.isOverdue()) fineService.addFine(u3, l3.getFineAmount());
        if(l4.isOverdue()) fineService.addFine(u3, l4.getFineAmount());

        // ======= Main Menu =======
        while(true){
            System.out.println("\n===== Library System =====");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as User");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");
            int choice = sc.nextInt(); sc.nextLine();

            if(choice==1){
                System.out.print("Admin username: "); 
                String name = sc.nextLine();
                System.out.print("Password: "); 
                String pass = sc.nextLine();

                if(name.equals(admin.getUsername()) && admin.checkPassword(pass)){
                    System.out.println("✅ Admin login successful!");
                    adminMenu(sc, books, cds, users, loanService, notifier);
                } else System.out.println("❌ Invalid username or password.");
            }
            else if(choice==2){
                System.out.print("Enter your name: "); String name = sc.nextLine();
                System.out.print("Enter your email: "); String email = sc.nextLine();

                User loggedUser = null;
                for(User u: users){
                    if(u.getName().equalsIgnoreCase(name) && u.getEmail().equalsIgnoreCase(email)){
                        loggedUser = u; break;
                    }
                }

                if(loggedUser!=null){
                    System.out.println("✅ Logged in as user: "+loggedUser.getName());
                    userMenu(sc, loggedUser, books, cds, loanService);
                } else System.out.println("❌ No user found with that info.");
            }
            else if(choice==3){ System.out.println("👋 Exiting system..."); break; }
            else System.out.println("❗ Invalid option.");
        }

        sc.close();
    }

    // ======= Admin Menu =======
    private static void adminMenu(Scanner sc, List<Book> books, List<CD> cds, List<User> users, LoanService loanService, MediaEmailNotifier notifier){
        while(true){
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. View Media");
            System.out.println("2. Add Media");
            System.out.println("3. Delete Media");
            System.out.println("4. Search Media");
            System.out.println("5. View users and fines");
            System.out.println("6. Register a new user");
            System.out.println("7. View number of borrowed Media");
            System.out.println("8. Send reminders");
            System.out.println("9. Unregister a user");
            System.out.println("10. Logout");

            System.out.print("Your choice: "); int c = sc.nextInt(); sc.nextLine();

            switch(c){
                case 1 -> { // View Media
                    System.out.println("View: 1-Books 2-CDs");
                    int type = sc.nextInt(); sc.nextLine();
                    if(type == 1){
                        books.forEach(b -> System.out.println(b + (b.isBorrowed() ? " (Borrowed)" : " (Available)")));
                    } else if(type == 2){
                        cds.forEach(cd -> System.out.println(cd + (cd.isBorrowed() ? " (Borrowed)" : " (Available)")));
                    } else System.out.println("❗ Invalid option.");
                }

                case 2 -> { // Add Media
                    System.out.println("Add: 1-Book 2-CD");
                    int type = sc.nextInt(); sc.nextLine();
                    if(type == 1){
                        System.out.print("Enter title: "); String title = sc.nextLine();
                        System.out.print("Enter author: "); String author = sc.nextLine();
                        System.out.print("Enter ISBN: "); String isbn = sc.nextLine();
                        boolean exists = books.stream().anyMatch(b -> b.getIsbn().equals(isbn));
                        if(exists) System.out.println("❌ ISBN already exists.");
                        else { books.add(new Book(title, author, isbn)); System.out.println("✅ Book added!"); }
                    } else if(type == 2){
                        System.out.print("Enter title: "); String title = sc.nextLine();
                        System.out.print("Enter artist: "); String artist = sc.nextLine();
                        System.out.print("Enter CD ID: "); String cdId = sc.nextLine();
                        boolean exists = cds.stream().anyMatch(cd -> cd.getId().equals(cdId));
                        if(exists) System.out.println("❌ CD ID already exists.");
                        else { cds.add(new CD(title, artist, cdId)); System.out.println("✅ CD added!"); }
                    } else System.out.println("❗ Invalid option.");
                }

                case 3 -> { // Delete Media
                    System.out.println("Delete: 1-Book 2-CD");
                    int type = sc.nextInt(); sc.nextLine();
                    if(type == 1){
                        System.out.print("Enter ISBN to delete: "); String isbn = sc.nextLine();
                        Book del = books.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().orElse(null);
                        if(del != null){ books.remove(del); System.out.println("✅ Book deleted!"); }
                        else System.out.println("❌ Book not found.");
                    } else if(type == 2){
                        System.out.print("Enter CD ID to delete: "); String cdId = sc.nextLine();
                        CD del = cds.stream().filter(cd -> cd.getId().equals(cdId)).findFirst().orElse(null);
                        if(del != null){ cds.remove(del); System.out.println("✅ CD deleted!"); }
                        else System.out.println("❌ CD not found.");
                    } else System.out.println("❗ Invalid option.");
                }

                case 4 -> { // Search
                    System.out.println("Search: 1-Book 2-CD"); 
                    int type = sc.nextInt(); sc.nextLine();

                    if(type == 1){
                        System.out.println("Search by: 1-Title 2-Author 3-ISBN"); 
                        int s = sc.nextInt(); sc.nextLine();

                        System.out.print("Enter query: "); 
                        String query = sc.nextLine();

                        SearchStrategy strategy = switch(s){ 
                            case 1 -> new SearchByTitle(); 
                            case 2 -> new SearchByAuthor(); 
                            case 3 -> new SearchByISBN(); 
                            default -> null; 
                        };
                        if(strategy != null){ 
                            List<Book> res = strategy.search(books, query); 
                            if(res.isEmpty()) System.out.println("❌ No books found."); 
                            else res.forEach(System.out::println);
                        } else System.out.println("❗ Invalid search option.");
                    } else if(type == 2){
                        System.out.print("Enter query: "); 
                        String query = sc.nextLine();

                        List<CD> cdResults = cds.stream()
                                                .filter(cd -> cd.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                                              cd.getArtist().toLowerCase().contains(query.toLowerCase()) ||
                                                              cd.getId().toLowerCase().contains(query.toLowerCase()))
                                                .toList();
                        if(cdResults.isEmpty()) System.out.println("❌ No CDs found."); 
                        else cdResults.forEach(System.out::println);
                    } else System.out.println("❗ Invalid option.");
                }


                case 5 -> users.forEach(u -> System.out.println(u.getName()+" | "+u.getEmail()+" | Fine: $"+u.getFineBalance()));

                case 6 -> { // Register User
                    System.out.print("Enter new user name: "); String uname = sc.nextLine();
                    System.out.print("Enter email: "); String uemail = sc.nextLine();
                    users.add(new User(uname,uemail)); 
                    System.out.println("✅ User registered!"); 
                }

                case 7 -> { // Borrowed count for all media
                    long countBooks = books.stream().filter(Book::isBorrowed).count();
                    long countCDs = cds.stream().filter(CD::isBorrowed).count();
                    long total = countBooks + countCDs;
                    System.out.println("📚 Total borrowed media: " + total + " (Books: " + countBooks + " | CDs: " + countCDs + ")");
                }

                case 8 -> { // Send reminders
                    System.out.println("📩 Sending overdue reminders...");
                    for(User u: users){
                        boolean hasOverdue = loanService.getUserLoans(u)
                                                        .stream()
                                                        .anyMatch(Loan::isOverdue);
                        if(hasOverdue){
                            notifier.update(null, u); 
                        }
                    }
                }

                case 9 -> { // Unregister user
                    System.out.print("Enter email of user to unregister: "); 
                    String email = sc.nextLine();
                    User target = users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
                    if(target == null) System.out.println("❌ No user found with that email.");
                    else if(target.getFineBalance() > 0) System.out.println("❌ Cannot unregister user with unpaid fines.");
                    else if(target.getLoans().stream().anyMatch(l -> !l.isReturned())) System.out.println("❌ Cannot unregister user with active loans.");
                    else { users.remove(target); System.out.println("✅ User unregistered successfully: " + target.getName()); }
                }

                case 10 -> { System.out.println("🔙 Logout"); return; }

                default -> System.out.println("❗ Invalid option.");
            }
        }
    }

    // ======= User Menu =======
    private static void userMenu(Scanner sc, User user, List<Book> books, List<CD> cds, LoanService loanService){
        while(true){
            System.out.println("\n===== User Menu =====");
            System.out.println("1. Show all books");
            System.out.println("2. Show all CDs");
            System.out.println("3. Search book");
            System.out.println("4. Borrow a Media");
            System.out.println("5. Return a Media");
            System.out.println("6. View & pay fine");
            System.out.println("7. Logout");

            System.out.print("Choice: "); int c=sc.nextInt(); sc.nextLine();

            switch(c){
                case 1->books.forEach(b->System.out.println(b+(b.isBorrowed()?" (Borrowed)":" (Available)")));
                case 2->cds.forEach(cd -> System.out.println(cd + (cd.isBorrowed() ? " (Borrowed)" : " (Available)")));
                case 3 -> { 
                    System.out.println("Search: 1-Book 2-CD"); 
                    int type = sc.nextInt(); sc.nextLine();

                    if(type == 1){
                        System.out.println("Search by: 1-Title 2-Author 3-ISBN"); 
                        int s = sc.nextInt(); sc.nextLine();

                        System.out.print("Enter query: "); 
                        String query = sc.nextLine();

                        SearchStrategy strategy = switch(s){ 
                            case 1 -> new SearchByTitle(); 
                            case 2 -> new SearchByAuthor(); 
                            case 3 -> new SearchByISBN(); 
                            default -> null; 
                        };
                        if(strategy != null){ 
                            List<Book> res = strategy.search(books, query); 
                            if(res.isEmpty()) System.out.println("❌ No books found."); 
                            else res.forEach(System.out::println);
                        } else System.out.println("❗ Invalid search option.");
                    } else if(type == 2){
                        System.out.print("Enter query: "); 
                        String query = sc.nextLine();

                        List<CD> cdResults = cds.stream()
                                                .filter(cd -> cd.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                                              cd.getArtist().toLowerCase().contains(query.toLowerCase()) ||
                                                              cd.getId().toLowerCase().contains(query.toLowerCase()))
                                                .toList();
                        if(cdResults.isEmpty()) System.out.println("❌ No CDs found."); 
                        else cdResults.forEach(System.out::println);
                    } else System.out.println("❗ Invalid option.");
                }

                case 4 -> { 
                    List<Loan> userLoans = loanService.getUserLoans(user);

                 
                    boolean hasOverdueOrActive = userLoans.stream()
                                                          .anyMatch(l -> l.isOverdue() || !l.isReturned());
                    if(hasOverdueOrActive){
                        System.out.println("❌ You have overdue or active items. Please return them or pay fines first.");
                        break;
                    }

                    System.out.println("Borrow: 1-Book 2-CD"); 
                    int type = sc.nextInt(); sc.nextLine();

                    if(type == 1){
                        System.out.print("Enter book title: "); 
                        String title = sc.nextLine();

                        Book bookFound = books.stream()
                                              .filter(b -> b.getTitle().equalsIgnoreCase(title) && !b.isBorrowed())
                                              .findFirst().orElse(null);
                        if(bookFound != null){
                            loanService.createLoan(bookFound, user);
                            System.out.println("✅ Borrowed Book! Due: " + bookFound.getDueDate());
                        } else System.out.println("❌ Book not available.");
                    } else if(type == 2){
                        System.out.print("Enter CD title: "); 
                        String title = sc.nextLine();

                        CD cdFound = cds.stream()
                                        .filter(cd -> cd.getTitle().equalsIgnoreCase(title) && !cd.isBorrowed())
                                        .findFirst().orElse(null);
                        if(cdFound != null){
                            loanService.createLoan(cdFound, user);
                            System.out.println("✅ Borrowed CD! Due: " + cdFound.getDueDate());
                        } else System.out.println("❌ CD not available.");
                    } else System.out.println("❗ Invalid option.");
                }

                case 5 -> { 
                    System.out.println("Return: 1-Book 2-CD"); 
                    int type = sc.nextInt(); sc.nextLine();

                    if(type == 1){
                        System.out.print("Enter book title to return: "); 
                        String title = sc.nextLine();

                        Loan loanToReturn = loanService.getUserLoans(user).stream()
                                                  .filter(l -> l.getMedia() instanceof Book && l.getMedia().getTitle().equalsIgnoreCase(title) && !l.isReturned())
                                                  .findFirst().orElse(null);

                        if(loanToReturn != null){
                            loanService.returnLoan(loanToReturn);
                            System.out.println("✅ Book returned successfully!");
                        } else System.out.println("❌ No such borrowed book found.");
                    } else if(type == 2){
                        System.out.print("Enter CD title to return: "); 
                        String title = sc.nextLine();

                        Loan loanToReturn = loanService.getUserLoans(user).stream()
                                                  .filter(l -> l.getMedia() instanceof CD && l.getMedia().getTitle().equalsIgnoreCase(title) && !l.isReturned())
                                                  .findFirst().orElse(null);

                        if(loanToReturn != null){
                            loanService.returnLoan(loanToReturn);
                            System.out.println("✅ CD returned successfully!");
                        } else System.out.println("❌ No such borrowed CD found.");
                    } else System.out.println("❗ Invalid option.");
                }

                case 6->{ 
                    System.out.println("Your fine: $"+user.getFineBalance());
                    if(user.getFineBalance()>0){ 
                        System.out.print("Pay now? (y/n): "); String ans=sc.nextLine();
                        if(ans.equalsIgnoreCase("y")){ 
                            System.out.print("Amount: "); double amt=sc.nextDouble(); sc.nextLine(); 
                            user.payFine(amt); 
                            System.out.println("💰 Remaining: $"+user.getFineBalance()); 
                        } 
                    }
                }
                case 7-> { System.out.println("🔙 Logout"); return; }
                default->System.out.println("❗ Invalid option.");
            }
        }
    }
*/}