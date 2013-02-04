SJWidgets FAQ
============

#Why isn't SJWidgets a Look and Feel?#

The Java Pluggable Look and Feel architecture is extremely complex and powerful.
However, it is designed primarilly to replace the look and feel
of all widgets with a predefied set of looks. For example, if you want your entire UI to
have a unified look, it works well, but only so far as defining what widgets of a certain
type should look like. ie, making all your buttons look a certain way. This may work well
for simple UIs or prototypes, but it simply isn't adequate for complex, beautiful UIs. The
Java Folks obviously realized this when they built JavaFX, but for many of us, JavaFX isn't
a realistic option either.

The truth is, swing, with or without PLaF, is capable of building rich, beautiful UIs
except that it lacks a good way to define the appearance of individuial widgets easilly, outside of
code. SJWidgets is designed for this purpose.

SJWidgets is ideal for building UIs that require attention to detail and which may
have significant design collatoral and/or tweaking of individual widgets, rather than just
widget types.


#Where are SJTable and SJList?#

It may seem like you should be able to create an SJTable the same way you can create an
SJButton. As it so happens, JTable and JList don't do their own rendering. Rather, they delegate
their rendering to other components. As a result, it is not necessary to subclass JTable
in SJWidgets -- you can use JTable directly. For an example of how this is done, see
com.xowave.sjwidget.examples.TableExample. The same methods can be used for changing the
rendering (and editing) of JList data.


#Why are the examples so ugly? I thought SJWidgets was supposed to be pretty!#

SJWidgets is deisgned to be flexible, and the example code shows you the flexibility. It's up
to you (and your design team!) to make it pretty.
