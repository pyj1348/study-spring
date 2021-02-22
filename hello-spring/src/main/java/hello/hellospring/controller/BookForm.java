package hello.hellospring.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    private Long id;
    private String name;
    private int stockQuantity;
    private int price;
    private String author;
    private String isbn;
}
