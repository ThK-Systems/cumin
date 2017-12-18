/*
 * tksCommons
 * 
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.text;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils;

import com.ibm.icu.util.ULocale;

import de.thksystems.util.lang.Deferred;

/**
 * Accesses locales from JDK or ICU (preferred).
 */
public final class LocaleUtils {

	private static final Logger LOG = Logger.getLogger(LocaleUtils.class.getName());

	private static final Deferred<LocaleDelegate> localeDelegate = new Deferred<>(() -> LocaleDelegate.getImplementation());

	private static final Deferred<Collection<String>> countryCodes = new Deferred<>(() -> localeDelegate.get().getCountryCodes());

	private static final Deferred<Collection<String>> currencyCodes = new Deferred<>(() -> localeDelegate.get().getCurrencyCodes());

	private static final Deferred<Collection<String>> localeCodes = new Deferred<>(() -> localeDelegate.get().getLocaleCodes());

	private LocaleUtils() {
	}

	public static boolean isValidCountryCode(String countryCode) {
		return countryCodes.get().contains(countryCode);
	}

	public static boolean isValidCurrencyCode(String currencyCode) {
		return currencyCodes.get().contains(currencyCode);
	}

	public static boolean isValidLocaleCode(String localeCode) {
		return localeCodes.get().contains(localeCode);
	}

	static interface LocaleDelegate {

		Collection<String> getCountryCodes();

		Collection<String> getCurrencyCodes();

		Collection<String> getLocaleCodes();

		static LocaleDelegate getImplementation() {
			try {
				ClassUtils.getClass("com.ibm.icu.util.ULocale");
				ClassUtils.getClass("com.ibm.icu.util.Currency");
				LOG.info("Using ICU implementation");
				return new ICUDelegate();
			} catch (ClassNotFoundException e) {
				LOG.info("Using JDK implementation");
				return new JDKDelegate();
			}
		}

	};

	static class JDKDelegate implements LocaleDelegate {

		@Override
		public Collection<String> getCountryCodes() {
			return Arrays.asList(Locale.getISOCountries());
		}

		@Override
		public Collection<String> getCurrencyCodes() {
			return java.util.Currency.getAvailableCurrencies().stream().map(c -> c.getCurrencyCode()).collect(Collectors.toSet());
		}

		@Override
		public Collection<String> getLocaleCodes() {
			return Arrays.stream(Locale.getAvailableLocales()).map(l -> l.toString()).collect(Collectors.toSet());
		}

	}

	static class ICUDelegate implements LocaleDelegate {

		@Override
		public Collection<String> getCountryCodes() {
			return Arrays.asList(ULocale.getISOCountries());
		}

		@Override
		public Collection<String> getCurrencyCodes() {
			return com.ibm.icu.util.Currency.getAvailableCurrencies().stream().map(c -> c.getCurrencyCode()).collect(Collectors.toSet());
		}

		@Override
		public Collection<String> getLocaleCodes() {
			return Arrays.stream(ULocale.getAvailableLocales()).map(l -> l.getName()).collect(Collectors.toSet());
		}

	}

}
