package de.thksystems.util.text;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LocaleUtilsTest {

    @Test
    public void testIsValidCountryCode() {
        assertFalse(LocaleUtils.isValidCountryCode("XX"));
        assertTrue(LocaleUtils.isValidCountryCode("DE"));
        assertFalse(LocaleUtils.isValidCountryCode("IC"));
    }

    @Test
    public void testIsValidCurrencyCode() {
        assertFalse(LocaleUtils.isValidCurrencyCode("YYY"));
        assertTrue(LocaleUtils.isValidCurrencyCode("EUR"));
        assertTrue(LocaleUtils.isValidCurrencyCode("CHF"));
        assertTrue(LocaleUtils.isValidCurrencyCode("PLN"));
        assertTrue(LocaleUtils.isValidCurrencyCode("NOK"));
        assertTrue(LocaleUtils.isValidCurrencyCode("DKK"));
        assertTrue(LocaleUtils.isValidCurrencyCode("BGN"));
        assertTrue(LocaleUtils.isValidCurrencyCode("CZK"));
        assertTrue(LocaleUtils.isValidCurrencyCode("HUF"));
        assertTrue(LocaleUtils.isValidCurrencyCode("RON"));
    }

    @Test
    public void testIsValidLocaleCode() {
        assertTrue(LocaleUtils.isValidLocaleCode("de_DE"));
        assertTrue(LocaleUtils.isValidLocaleCode("de_CH"));
        assertTrue(LocaleUtils.isValidLocaleCode("fr_CH"));
        assertTrue(LocaleUtils.isValidLocaleCode("de"));
        assertFalse(LocaleUtils.isValidLocaleCode("de-DE"));
        assertFalse(LocaleUtils.isValidLocaleCode("DEUTSCH"));
        assertFalse(LocaleUtils.isValidLocaleCode("CH"));
        assertFalse(LocaleUtils.isValidLocaleCode("ch"));
        assertFalse(LocaleUtils.isValidLocaleCode("ch_CH"));
    }
}
