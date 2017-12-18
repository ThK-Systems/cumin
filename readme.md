# Cumin

## Summary

cumin provides some utility classes for java (standard edition): \[tabby title="Overview"\]

*   **CollectionUtils** \- Simply creating collections
*   **MapUtils** \- Simply creating maps
*   **ExceptionUtils** \- Get nice mini stacktrace, check for exception types in cause hierarchy
*   **ZipUtils** \- Write data or a string to a zip-file
*   **ReflectionUtils** \- Some relections helper
*   **AnnotationUtils** \- Gets fields of a class annotated with a special annotation
*   **ObjectUtils** \- Annotation based toString(), equals(), hashCode
*   **LocaleUtils** \- Access locales (JDK or ICU (preferred))
*   **StringUtils** \- Get capitals of a string
*   **RandomStringUtils** \- Create random strings
*   **CSVUtils** \- Reading a CSV file as a list of string-arrays
*   **IOUtils** \- Copy a StringWriter
*   **ParseUtils** \- Parsing filesizes (like '5.3GB', '6k') or durations (like '5s', '2.5h')
*   **DNSUtils** \- Get domain record, check for hostname against ip-address
*   **DomUtils** \- Manipulation DOM, DOM as string
*   **PasswortUtils** \- Hashes for password
*   **WrappingAtomicCounter** \- used for wrapping counter 0..1..2..3..4..5..0..1..2.. .
*   **Locker** \- For locking arbitrary objects.
*   **Deferred** \- For lazy initialization
*   **NamedRunnable** \- Runnable with the ability to set the thread-name that is used while running
*   **NamedCallable** \- Callable with the ability to set the thread-name that is used while calling
*   **CheckedFunction**, **CheckedSupplier**, **CheckedConsumer**, **CheckedRunnable** - Functional interfaces that can throws exceptions

... more to come ... 


## Documentation
Browse the **[Javadoc of the latest version](http://www.thk-systems.de/content/oss/javadoc/cumin/current/index.html)**. ('cumin' is well documented there.)


## Installation

```xml
<dependency>
  <groupId>net.thk-systems.commons</groupId>
  <artifactId>cumin</artifactId>
  <version>3.12.0</version>
</dependency>
```
You can browse [maven-central](http://search.maven.org/#artifactdetails|net.thk-systems.commons|cumin|3.12.0|jar) to download the jar or another versions.


## Changelog

**3.13.0 (unreleased)**
* Updated dependencies
* Moved to GitHub

**3.12.0**

*   Added ReflectivBasicBean
*   Added RandomStringUtils

**3.11.0**

*   Added StringUtils.getCapitals
*   Extended NamedCallable to use the thread-id
*   Changed logger for Locker to SLF4J
*   Fixed bug of Locker regarding garbage collection

**3.10.0**

*   Added LocaleUtils.isValidLocaleCode

**3.9.0**

*   Added ExceptionUtils.getCauseWithType

**3.8.0**

*   Added LocaleUtils.isValidCurrencyCode

**3.7.0**

*   Added LocaleUtils

**3.6.0**

*   Added CheckedRunnable

**3.5.0**

*   Added CheckedFunction

**3.4.0**

*   Added IOUtils.copyStringWriter

**3.3.0**

*   Added CheckedConsumer
*   Added CheckedSupplier
*   Fixed WrappingAtomicCounter -> Now its really atomic
*   Added Locker.executeWithLock variants
*   Added Locker.executeCheckedWithLock

**3.2.0**

*   Added NamedRunnable
*   Added NamedCallable

**3.1.0**

*   Optimized Locker
*   Added Locker.tryLock

**3.0.0**

*   Added Deferred
*   Added Locker.executeWithLock
*   Move to Java 8 (first without changing anything)

**2.2.0**

*   Added Locker

**2.1.0**

*   REMOVED EncryptionUtils in cause of security reasons

**2.0.0**

*   Released as 2.0.0 under new maven group id

**1.3.1**

*   Fixed BaseService.commit does a rollback, if set rollbackOnly

**1.3**

*   Added UnsafeUtils
*   Added ServletUtils.getServletDump
*   Added ReflectionUtils.getFieldValue
*   Added AnnotationUtils.getFieldsAnnotatedWith (2 methods)
*   Added WrappingAtomicCounter

**1.2**

*   Renamed to 'cumin'
*   Added ObjectUtils
*   Added AnnotationUtils
*   Fixed IdentifiedEntity.equals to be symmetric
*   Added IdentifiedEntity.hashCode
*   Added IdentifiedEntity.compareTo
*   Added ReflectionUtils.getFieldValuesOfType

**1.1**

*   Added BaseRepository
*   Added IdentifiedEntity
*   Added BaseService

**1.0**

*   Merged with tksCommons-Crypto
*   Merged with tksCommons-XStream
*   Merged with tksCommons-Web
*   Merged with tksCommons-Lang

**0.5**

*   AddressUtils - Added
*   DNSUtils - Added
*   ServletUtils - Added
*   ExceptionUtils - Added
*   ParseUtils.parseDuration - Added

**0.3.1**

*   Compiled with JDK 1.7 (before 1.8)

**0.3**

*   CollectionUtils - Added
*   CollectionUtils#createArrayList - Added
*   CollectionUtils#addToCollection - Added
*   ParseUtils#parseFileSize - Fixed bug: Return null for negative input value
*   ParseUtils#parseFileSize - Changed: Return null for invalid input value (not -1)
*   CSVUtils - Moved to package util.text
*   DOMUtils - Added
*   ZipUtils - Added
*   MapUtils - Added