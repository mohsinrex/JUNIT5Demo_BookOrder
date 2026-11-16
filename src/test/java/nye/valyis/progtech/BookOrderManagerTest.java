package nye.valyis.progtech;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookOrderManagerTest {

    private BookOrderManager orderManager;
    private Book sampleBook;

    @BeforeEach
    void setup() {
        orderManager = new BookOrderManager();
        sampleBook = new Book("Harry Potter", 20.0, 10);
    }

    @Test
    void testAddBook_Success() {
        orderManager.addBookToInventory(sampleBook);
    }

    @Test
    void testAddBook_Null_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderManager.addBookToInventory(null);
        });
        assertEquals("Book cannot be null.", ex.getMessage());
    }

    @Test
    void testSetDiscount_Valid() {
        orderManager.setDiscountRate(0.5);
    }

    @Test
    void testSetDiscount_Negative_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderManager.setDiscountRate(-0.1);
        });
        assertEquals("Discount rate must be between 0.0 and 1.0.", ex.getMessage());
    }

    @Test
    void testSetDiscount_OverOne_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderManager.setDiscountRate(1.1);
        });
        assertEquals("Discount rate must be between 0.0 and 1.0.", ex.getMessage());
    }

    @Test
    void testCalculateTotal_NoDiscount() {
        orderManager.addBookToInventory(sampleBook);
        double total = orderManager.calculateOrderTotal("Harry Potter", 2);
        assertEquals(40.0, total);
    }

    @Test
    void testCalculateTotal_WithDiscount() {
        orderManager.addBookToInventory(sampleBook);
        orderManager.setDiscountRate(0.1);
        double total = orderManager.calculateOrderTotal("Harry Potter", 2);
        assertEquals(36.0, total);
    }

    @Test
    void testCalculateTotal_ZeroDiscount() {
        orderManager.addBookToInventory(sampleBook);
        double total = orderManager.calculateOrderTotal("Harry Potter", 1);
        assertEquals(20.0, total);
    }

    @Test
    void testCalculateTotal_NullTitle_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderManager.calculateOrderTotal(null, 1);
        });
        assertEquals("Book title cannot be null or empty.", ex.getMessage());
    }

    @Test
    void testCalculateTotal_EmptyTitle_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderManager.calculateOrderTotal("   ", 1);
        });
        assertEquals("Book title cannot be null or empty.", ex.getMessage());
    }

    @Test
    void testCalculateTotal_ZeroQuantity_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderManager.calculateOrderTotal("Harry Potter", 0);
        });
        assertEquals("Quantity must be greater than zero.", ex.getMessage());
    }

    @Test
    void testCalculateTotal_BookNotFound_ThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            orderManager.calculateOrderTotal("Unknown Book", 1);
        });
        assertEquals("Book not found in inventory.", ex.getMessage());
    }

    @Test
    void testCalculateTotal_NotEnoughStock_ThrowsException() {
        orderManager.addBookToInventory(sampleBook);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            orderManager.calculateOrderTotal("Harry Potter", 11);
        });
        assertEquals("Insufficient stock available.", ex.getMessage());
    }

    @Test
    void testCheckout_Success() {
        orderManager.addBookToInventory(sampleBook);
        boolean result = orderManager.processCheckout("Harry Potter", 2, true);
        assertTrue(result);
        assertEquals(8, sampleBook.getStock());
    }

    @Test
    void testCheckout_MultipleBooks() {
        Book secondBook = new Book("Lord of the Rings", 30.0, 5);
        orderManager.addBookToInventory(sampleBook);
        orderManager.addBookToInventory(secondBook);

        boolean result = orderManager.processCheckout("Lord of the Rings", 1, true);
        assertTrue(result);
        assertEquals(4, secondBook.getStock());
    }

    @Test
    void testCheckout_NullTitle_ReturnsFalse() {
        boolean result = orderManager.processCheckout(null, 1, false);
        assertFalse(result);
    }

    @Test
    void testCheckout_NegativeQuantity_ReturnsFalse() {
        boolean result = orderManager.processCheckout("Harry Potter", -1, false);
        assertFalse(result);
    }

    @Test
    void testCheckout_UnknownBook_ThrowsException() {
        orderManager.addBookToInventory(sampleBook);
        assertThrows(IllegalStateException.class, () -> {
            orderManager.processCheckout("Unknown Book", 1, true);
        });
    }

    @Test
    void testCheckout_NotEnoughStock_ThrowsException() {
        orderManager.addBookToInventory(sampleBook);
        assertThrows(IllegalStateException.class, () -> {
            orderManager.processCheckout("Harry Potter", 15, true);
        });
    }

    @Test
    void testCheckout_EmptyInventory_ThrowsException() {
        assertThrows(IllegalStateException.class, () -> {
            orderManager.processCheckout("Harry Potter", 1, true);
        });
    }
}