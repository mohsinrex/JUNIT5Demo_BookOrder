package nye.valyis.progtech;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookOrderManagerTest {

    private BookOrderManager manager;
    private Book testBook;

    @BeforeEach
    void setUp() {
        manager = new BookOrderManager();
        // Standard test book used across most tests
        testBook = new Book("Harry Potter", 20.0, 10);
    }

    // ==========================================
    // Tests for addBookToInventory()
    // ==========================================

    @Test
    void testAddBookToInventory_Success() {
        manager.addBookToInventory(testBook);
        // If no exception is thrown, the test passes
    }

    @Test
    void testAddBookToInventory_NullBook_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addBookToInventory(null);
        });
        assertEquals("Book cannot be null.", exception.getMessage());
    }

    // ==========================================
    // Tests for setDiscountRate()
    // ==========================================

    @Test
    void testSetDiscountRate_Success() {
        manager.setDiscountRate(0.5);
        // If no exception is thrown, the test passes
    }

    @Test
    void testSetDiscountRate_BelowZero_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.setDiscountRate(-0.1);
        });
        assertEquals("Discount rate must be between 0.0 and 1.0.", exception.getMessage());
    }

    @Test
    void testSetDiscountRate_AboveOne_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.setDiscountRate(1.1);
        });
        assertEquals("Discount rate must be between 0.0 and 1.0.", exception.getMessage());
    }

    // ==========================================
    // Tests for calculateOrderTotal()
    // ==========================================

    @Test
    void testCalculateOrderTotal_SuccessNoDiscount() {
        manager.addBookToInventory(testBook);
        double total = manager.calculateOrderTotal("Harry Potter", 2);
        assertEquals(40.0, total, "Total should be base price * quantity");
    }

    @Test
    void testCalculateOrderTotal_SuccessWithDiscount() {
        manager.addBookToInventory(testBook);
        manager.setDiscountRate(0.1); // Apply 10% discount
        double total = manager.calculateOrderTotal("Harry Potter", 2);
        assertEquals(36.0, total, "Total should include the 10% discount");
    }

    @Test
    void testCalculateOrderTotal_ZeroDiscount_ReturnsFullPrice() {
        manager.addBookToInventory(testBook);
        // Default discount is 0.0; this covers the false branch of if (discountRate > 0)
        double total = manager.calculateOrderTotal("Harry Potter", 1);
        assertEquals(20.0, total, "Total should be the full price when discount is 0");
    }

    @Test
    void testCalculateOrderTotal_NullTitle_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal(null, 1);
        });
        assertEquals("Book title cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCalculateOrderTotal_EmptyTitle_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal("   ", 1);
        });
        assertEquals("Book title cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCalculateOrderTotal_InvalidQuantity_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal("Harry Potter", 0);
        });
        assertEquals("Quantity must be greater than zero.", exception.getMessage());
    }

    @Test
    void testCalculateOrderTotal_BookNotFound_ThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            manager.calculateOrderTotal("Unknown Book", 1);
        });
        assertEquals("Book not found in inventory.", exception.getMessage());
    }

    @Test
    void testCalculateOrderTotal_InsufficientStock_ThrowsException() {
        manager.addBookToInventory(testBook);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            manager.calculateOrderTotal("Harry Potter", 11); // Only 10 in stock
        });
        assertEquals("Insufficient stock available.", exception.getMessage());
    }

    // ==========================================
    // Tests for processCheckout()
    // ==========================================

    @Test
    void testProcessCheckout_Success() {
        manager.addBookToInventory(testBook);
        boolean result = manager.processCheckout("Harry Potter", 2, true);
        assertTrue(result);
        assertEquals(8, testBook.getStock(), "Stock should be reduced by the ordered quantity");
    }

    @Test
    void testProcessCheckout_MultipleBooksInInventory() {
        // Covers the loop checking a non-matching book and then finding the right one
        Book secondBook = new Book("Lord of the Rings", 30.0, 5);
        manager.addBookToInventory(testBook);
        manager.addBookToInventory(secondBook);

        boolean result = manager.processCheckout("Lord of the Rings", 1, true);
        assertTrue(result);
        assertEquals(4, secondBook.getStock());
    }

    @Test
    void testProcessCheckout_NullTitle_ReturnsFalse() {
        boolean result = manager.processCheckout(null, 1, false);
        assertFalse(result);
    }

    @Test
    void testProcessCheckout_InvalidQuantity_ReturnsFalse() {
        boolean result = manager.processCheckout("Harry Potter", -1, false);
        assertFalse(result);
    }

    @Test
    void testProcessCheckout_BookNotFound_ThrowsException() {
        manager.addBookToInventory(testBook);
        assertThrows(IllegalStateException.class, () -> {
            manager.processCheckout("Unknown Book", 1, true);
        });
    }

    @Test
    void testProcessCheckout_InsufficientStock_ThrowsException() {
        manager.addBookToInventory(testBook);
        assertThrows(IllegalStateException.class, () -> {
            manager.processCheckout("Harry Potter", 15, true);
        });
    }

    @Test
    void testProcessCheckout_EmptyInventory_ThrowsException() {
        assertThrows(IllegalStateException.class, () -> {
            manager.processCheckout("Harry Potter", 1, true);
        });
    }
}