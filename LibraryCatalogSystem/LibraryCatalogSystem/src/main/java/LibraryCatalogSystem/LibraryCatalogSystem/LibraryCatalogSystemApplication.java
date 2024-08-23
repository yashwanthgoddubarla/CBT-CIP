package LibraryCatalogSystem.LibraryCatalogSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import LibraryCatalogSystem.LibraryCatalogSystem.service.BookService;
import LibraryCatalogSystem.LibraryCatalogSystem.Entity.Book;
import org.springframework.boot.CommandLineRunner;

import java.util.Scanner;

@SpringBootApplication
@ComponentScan(basePackages = "LibraryCatalogSystem.LibraryCatalogSystem")
@EntityScan("LibraryCatalogSystem.LibraryCatalogSystem.Entity")
@EnableJpaRepositories("LibraryCatalogSystem.LibraryCatalogSystem.repository")
public class LibraryCatalogSystemApplication implements CommandLineRunner {

	 @Autowired
	 private BookService bookService;
	 
	 @Override
	    public void run(String... args) throws Exception {
	        Scanner scanner = new Scanner(System.in);
	        while (true) {
	            System.out.println("Library Catalog System");
	            System.out.println("1. Add Book");
	            System.out.println("2. Search by Title");
	            System.out.println("3. Search by Author");
	            System.out.println("4. List All Books");
	            System.out.println("5. Exit");
	            System.out.print("Enter choice: ");
	            int choice = scanner.nextInt();
	            scanner.nextLine();   

	            switch (choice) {
	                case 1:
	                    System.out.print("Enter title: ");
	                    String title = scanner.nextLine();
	                    System.out.print("Enter author: ");
	                    String author = scanner.nextLine();
	                    Book book = new Book();
	                    book.setTitle(title);
	                    book.setAuthor(author);
	                    bookService.addBook(book);
	                    System.out.println("Book added successfully.");
	                    break;
	                case 2:
	                    System.out.print("Enter title: ");
	                    String searchTitle = scanner.nextLine();
	                    bookService.searchByTitle(searchTitle).forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthor()));
	                    break;
	                case 3:
	                    System.out.print("Enter author: ");
	                    String searchAuthor = scanner.nextLine();
	                    bookService.searchByAuthor(searchAuthor).forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthor()));
	                    break;
	                case 4:
	                    bookService.listAllBooks().forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthor()));
	                    break;
	                case 5:
	                    System.exit(0);
	                default:
	                    System.out.println("Invalid choice.");
	            }
	        }
	    }
	   
	public static void main(String[] args) {
		SpringApplication.run(LibraryCatalogSystemApplication.class, args);
		 
	}
	
}
