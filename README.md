# Refract

Gives you the ability to add exports and opens during runtime. This way you can save yourself from having to stack
dozens of `--add-exports` and `--add-opens` when launching your application.

## Usage

Simply call the appropriate function to add the required exports or opens before you call the code making use of such.
A commonly good place is the static initializer of a class, but it can also be put anywhere in between. Consecutive
adds of the same exports or opens will do nothing.

Something like `--add-opens java.desktop/sun.swing=EVERYONE` becomes

```java
Modules.addOpens("java.desktop", "sun.swing", Modules.EVERYONE);
```

## Scope

This library should be used with caution. Accessing the internals of the JDK can cause issues with compatibility down
the line. In most cases there are better alternatives, which should be used instead.

This is, however, not the cases when it comes to Java Swing, as it was never designed with the module system in mind and
has never been sufficiently adapted to work with said system. While it might seem to be working just fine, if you are
doing basic things, you will quickly struggle, if you try to do more advanced things. Some example are
the `SwingUtilities2.AA_TEXT_PROPERTY_KEY`, `Font2D` and its properties like `weight` and `style`, `Toolkit.grab()` and
`Toolkit.ungrab` or `FontUtilities.getCompositeFontUIResource(font)`. They all have in common, that there are no
equivalents in the accessible packages. There are many more cases like these. Given the old age of Java Swing, this is
probably never going to change BUT no change also means that using a library like this cannot do harm :)