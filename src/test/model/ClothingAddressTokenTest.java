package model;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ClothingAddressTokenTest {

    @Test
    void testParse() {
        String input = ClothingAddressToken.BRAND_TOKEN_STR
                + ClothingAddressToken.EQUALITY_TOKEN_STR
                + "Adidas";
        ClothingAddress address = ClothingAddressToken.parse(new Scanner(input));
        assertTrue(address.getBrands().isPresent());
        assertEquals(address.getBrands().get().size(), 1);
        assertEquals(address.getBrands().get().get(0), "Adidas");
    }

}