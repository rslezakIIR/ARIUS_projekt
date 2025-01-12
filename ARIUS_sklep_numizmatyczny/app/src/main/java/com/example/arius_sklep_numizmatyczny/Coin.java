package com.example.arius_sklep_numizmatyczny;

/**
 *  Moneta
 *  Ma identyfikator, nazwę, opis, cenę, rok wydania, państwo wykonania,
 *  dostępną liczbę sztuk, nazwę pliku obrazu, średnią ocenę i liczbę ocen
 */
public class Coin {
    /**
     * Identyfikator monety
     */
    public int id;

    /**
     * Nazwa monety
     */
    public String name;

    /**
     * Opis monety
     */
    public String description;

    /**
     * Cena monety
     */
    public float price;

    /**
     * Rok wydania monety
     */
    public int yearIssued;

    /**
     * Państwo, które wykonało monetę
     */
    public String country;

    /**
     * Liczba dostępnych sztuk monety
     */
    public int quantityAvailable;

    /**
     * Nazwa pliku obrazu monety
     */
    public String imageURL;

    /**
     * Średnia ocena monety
     */
    public float averageRating;

    /**
     * Liczba ocen monety
     */
    public int reviewCount;

    /**
     * Konstruktor monety
     * @param id Identyfikator
     * @param name Nazwa
     * @param description Opis
     * @param price Cena
     * @param yearIssued Rok wydania
     * @param country Państwo
     * @param quantityAvailable Dostępna liczba sztuk
     * @param imageURL Nazwa pliku obrazu
     */
    public Coin(int id, String name, String description, float price, int yearIssued,
                String country, int quantityAvailable, String imageURL){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.yearIssued = yearIssued;
        this.country = country;
        this.quantityAvailable = quantityAvailable;
        this.imageURL = imageURL;

        // Przypisz puste wartości niepodanym polom monety
        this.averageRating = 0.0F;
        this.reviewCount = 0;
    }

    /**
     * Konstruktor monety na podstawie istniejącej monety
     * @param coin Moneta
     * @param averageRating Średnia ocena
     * @param reviewCount Liczba ocen
     */
    public Coin(Coin coin, float averageRating, int reviewCount){
        // Przepisz wartości pól z istniejącej monety
        this.id = coin.id;
        this.name = coin.name;
        this.description = coin.description;
        this.price = coin.price;
        this.yearIssued = coin.yearIssued;
        this.country = coin.country;
        this.quantityAvailable = coin.quantityAvailable;
        this.imageURL = coin.imageURL;
        // Przypisz podane wartości pól
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    /**
     * Konstruktor pełnej monety
     * @param id Identyfikator
     * @param name Nazwa
     * @param description Opis
     * @param price Cena
     * @param yearIssued Rok wydania
     * @param country Państwo
     * @param quantityAvailable Dostępna liczba sztuk
     * @param imageURL Nazwa pliku obrazu
     * @param averageRating Średnia ocena
     * @param reviewCount Liczba ocen
     */
    public Coin(int id, String name, String description, float price, int yearIssued,
                String country, int quantityAvailable, String imageURL,
                float averageRating, int reviewCount){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.yearIssued = yearIssued;
        this.country = country;
        this.quantityAvailable = quantityAvailable;
        this.imageURL = imageURL;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }
}
